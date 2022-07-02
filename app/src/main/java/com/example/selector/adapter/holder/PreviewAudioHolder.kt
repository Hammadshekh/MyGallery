package com.example.selector.adapter.holder

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

class PreviewAudioHolder(itemView: View) : BasePreviewHolder(itemView) {
    private val mHandler = Handler(Looper.getMainLooper())
    var ivPlayButton: ImageView
    var tvAudioName: TextView
    var tvTotalDuration: TextView
    var tvCurrentTime: TextView
    var seekBar: SeekBar
    var ivPlayBack: ImageView
    var ivPlayFast: ImageView
    private var mPlayer: MediaPlayer? = MediaPlayer()
    private var isPausePlayer = false

    /**
     * 播放计时器
     */
    var mTickerRunnable: Runnable = object : Runnable {
        override fun run() {
            val currentPosition = mPlayer!!.currentPosition.toLong()
            val time: String = DateUtils.formatDurationTime(currentPosition)
            if (!TextUtils.equals(time, tvCurrentTime.text)) {
                tvCurrentTime.text = time
                if (mPlayer!!.duration - currentPosition > MIN_CURRENT_POSITION) {
                    seekBar.progress = currentPosition.toInt()
                } else {
                    seekBar.progress = mPlayer!!.duration
                }
            }
            val nextSecondMs = MAX_UPDATE_INTERVAL_MS - currentPosition % MAX_UPDATE_INTERVAL_MS
            mHandler.postDelayed(this, nextSecondMs)
        }
    }

    override fun bindData(media: LocalMedia, position: Int) {
        val path: String = media.getAvailablePath()
        val dataFormat: String = DateUtils.getYearDataFormat(media.getDateAddedTime())
        val fileSize: String = PictureFileUtils.formatAccurateUnitFileSize(media.getSize())
        val stringBuilder = StringBuilder()
        stringBuilder.append(media.getFileName()).append("\n").append(dataFormat).append(" - ")
            .append(fileSize)
        val builder = SpannableStringBuilder(stringBuilder.toString())
        val indexOfStr = "$dataFormat - $fileSize"
        val startIndex = stringBuilder.indexOf(indexOfStr)
        val endOf = startIndex + indexOfStr.length
        builder.setSpan(AbsoluteSizeSpan(DensityUtil.dip2px(itemView.context, 12)),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        builder.setSpan(ForegroundColorSpan(-0x9a9a9b),
            startIndex,
            endOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tvAudioName.text = builder
        tvTotalDuration.setText(DateUtils.formatDurationTime(media.getDuration()))
        seekBar.max = media.getDuration()
        setBackFastUI(false)
        ivPlayBack.setOnClickListener { slowAudioPlay() }
        ivPlayFast.setOnClickListener { fastAudioPlay() }
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar.progress = progress
                    setCurrentPlayTime(progress)
                    if (mPlayer!!.isPlaying) {
                        mPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        itemView.setOnClickListener {
            if (mPreviewEventListener != null) {
                mPreviewEventListener.onBackPressed()
            }
        }
        ivPlayButton.setOnClickListener(View.OnClickListener {
            try {
                if (DoubleUtils.isFastDoubleClick()) {
                    return@OnClickListener
                }
                mPreviewEventListener.onPreviewVideoTitle(media.getFileName())
                if (mPlayer!!.isPlaying) {
                    pausePlayer()
                } else {
                    if (isPausePlayer) {
                        resumePlayer()
                    } else {
                        startPlayer(path)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        itemView.setOnLongClickListener {
            if (mPreviewEventListener != null) {
                mPreviewEventListener.onLongPressDownload(media)
            }
            false
        }
    }

    /**
     * 重新开始播放
     *
     * @param path
     */
    private fun startPlayer(path: String) {
        try {
            if (PictureMimeType.isContent(path)) {
                mPlayer!!.setDataSource(itemView.context, Uri.parse(path))
            } else {
                mPlayer!!.setDataSource(path)
            }
            mPlayer!!.prepare()
            mPlayer!!.seekTo(seekBar.progress)
            mPlayer!!.start()
            isPausePlayer = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 暂停播放
     */
    private fun pausePlayer() {
        mPlayer!!.pause()
        isPausePlayer = true
        playerDefaultUI(false)
        stopUpdateProgress()
    }

    /**
     * 恢复播放
     */
    private fun resumePlayer() {
        mPlayer!!.seekTo(seekBar.progress)
        mPlayer!!.start()
        startUpdateProgress()
        playerIngUI()
    }

    /**
     * 重置播放器
     */
    private fun resetMediaPlayer() {
        isPausePlayer = false
        mPlayer!!.stop()
        mPlayer!!.reset()
    }

    /**
     * 设置当前播放进度
     *
     * @param progress
     */
    private fun setCurrentPlayTime(progress: Int) {
        val time: String = DateUtils.formatDurationTime(progress)
        tvCurrentTime.text = time
    }

    /**
     * 快进
     */
    private fun fastAudioPlay() {
        if (seekBar.progress > MAX_BACK_FAST_MS) {
            seekBar.progress = seekBar.max
        } else {
            seekBar.progress = (seekBar.progress + MAX_BACK_FAST_MS).toInt()
        }
        setCurrentPlayTime(seekBar.progress)
        mPlayer!!.seekTo(seekBar.progress)
    }

    /**
     * 回退
     */
    private fun slowAudioPlay() {
        if (seekBar.progress < MAX_BACK_FAST_MS) {
            seekBar.progress = 0
        } else {
            seekBar.progress = (seekBar.progress - MAX_BACK_FAST_MS).toInt()
        }
        setCurrentPlayTime(seekBar.progress)
        mPlayer!!.seekTo(seekBar.progress)
    }

    /**
     * 播放完成监听
     */
    private val mPlayCompletionListener = OnCompletionListener {
        stopUpdateProgress()
        resetMediaPlayer()
        playerDefaultUI(true)
    }

    /**
     * 播放失败监听
     */
    private val mPlayErrorListener =
        MediaPlayer.OnErrorListener { mp, what, extra ->
            resetMediaPlayer()
            playerDefaultUI(true)
            false
        }

    /**
     * 资源装载完成
     */
    private val mPlayPreparedListener =
        OnPreparedListener { mp ->
            if (mp.isPlaying) {
                seekBar.max = mp.duration
                startUpdateProgress()
                playerIngUI()
            } else {
                stopUpdateProgress()
                resetMediaPlayer()
                playerDefaultUI(true)
            }
        }

    /**
     * 开始更新播放进度
     */
    private fun startUpdateProgress() {
        mHandler.post(mTickerRunnable)
    }

    /**
     * 停止更新播放进度
     */
    private fun stopUpdateProgress() {
        mHandler.removeCallbacks(mTickerRunnable)
    }

    /**
     * 黑夜UI样式
     *
     * @param isResetProgress 是否重置进度条
     */
    private fun playerDefaultUI(isResetProgress: Boolean) {
        stopUpdateProgress()
        if (isResetProgress) {
            seekBar.progress = 0
            tvCurrentTime.text = "00:00"
        }
        setBackFastUI(false)
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_play)
        if (mPreviewEventListener != null) {
            mPreviewEventListener.onPreviewVideoTitle(null)
        }
    }

    /**
     * 播放中UI样式
     */
    private fun playerIngUI() {
        startUpdateProgress()
        setBackFastUI(true)
        ivPlayButton.setImageResource(R.drawable.ps_ic_audio_stop)
    }

    /**
     * 设置快进和回退UI样式
     *
     * @param isEnabled
     */
    private fun setBackFastUI(isEnabled: Boolean) {
        ivPlayBack.isEnabled = isEnabled
        ivPlayFast.isEnabled = isEnabled
        if (isEnabled) {
            ivPlayBack.alpha = 1.0f
            ivPlayFast.alpha = 1.0f
        } else {
            ivPlayBack.alpha = 0.5f
            ivPlayFast.alpha = 0.5f
        }
    }

    override fun onViewAttachedToWindow() {
        isPausePlayer = false
        setMediaPlayerListener()
        playerDefaultUI(true)
    }

    override fun onViewDetachedFromWindow() {
        isPausePlayer = false
        mHandler.removeCallbacks(mTickerRunnable)
        setNullMediaPlayerListener()
        resetMediaPlayer()
        playerDefaultUI(true)
    }

    /**
     * 释放PlayerView
     */
    fun releaseAudio() {
        mHandler.removeCallbacks(mTickerRunnable)
        if (mPlayer != null) {
            setNullMediaPlayerListener()
            mPlayer!!.release()
            mPlayer = null
        }
    }

    /**
     * 设置监听器
     */
    private fun setMediaPlayerListener() {
        mPlayer!!.setOnCompletionListener(mPlayCompletionListener)
        mPlayer!!.setOnErrorListener(mPlayErrorListener)
        mPlayer!!.setOnPreparedListener(mPlayPreparedListener)
    }

    /**
     * 置空监听器
     */
    private fun setNullMediaPlayerListener() {
        mPlayer!!.setOnCompletionListener(null)
        mPlayer!!.setOnErrorListener(null)
        mPlayer!!.setOnPreparedListener(null)
    }

    companion object {
        private const val MAX_BACK_FAST_MS = (3 * 1000).toLong()
        private const val MAX_UPDATE_INTERVAL_MS: Long = 1000
        private const val MIN_CURRENT_POSITION: Long = 1000
    }

    init {
        ivPlayButton = itemView.findViewById(R.id.iv_play_video)
        tvAudioName = itemView.findViewById(R.id.tv_audio_name)
        tvCurrentTime = itemView.findViewById(R.id.tv_current_time)
        tvTotalDuration = itemView.findViewById(R.id.tv_total_duration)
        seekBar = itemView.findViewById(R.id.music_seek_bar)
        ivPlayBack = itemView.findViewById(R.id.iv_play_back)
        ivPlayFast = itemView.findViewById(R.id.iv_play_fast)
    }
}
