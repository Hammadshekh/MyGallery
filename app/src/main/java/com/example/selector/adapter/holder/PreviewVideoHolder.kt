package com.example.selector.adapter.holder

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.mygallery.R
import com.example.selector.config.PictureConfig
import com.example.selector.config.PictureSelectionConfig
import com.example.selector.engine.MediaPlayerEngine
import com.example.selector.interfaces.OnPlayerListener
import com.example.selector.interfaces.VideoPlayerEngine
import com.example.selector.photoview.OnViewTapListener
import com.luck.picture.lib.entity.LocalMedia

class PreviewVideoHolder(itemView: View) : BasePreviewHolder(itemView) {
    var ivPlayButton: ImageView
    var progress: ProgressBar? = null
    var videoPlayer: View?
    private var isPlayed = false
    private fun findViews(itemView: View?) {}
    override fun loadImage(media: LocalMedia, maxWidth: Int, maxHeight: Int) {
        if (PictureSelectionConfig.imageEngine != null) {
            val availablePath = media.availablePath
            if (maxWidth == PictureConfig.UNSET && maxHeight == PictureConfig.UNSET) {
                PictureSelectionConfig.imageEngine!!.loadImage(
                    itemView.context,
                    availablePath,
                    coverImageView
                )
            } else {
                PictureSelectionConfig.imageEngine!!.loadImage(
                    itemView.context,
                    coverImageView,
                    availablePath,
                    maxWidth,
                    maxHeight
                )
            }
        }
    }

    private fun onClickBackPressed() {
        coverImageView!!.setOnViewTapListener(object : OnViewTapListener {
            override fun onViewTap(view: View?, x: Float, y: Float) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener!!.onBackPressed()
                }
            }
        })
    }

    private fun onLongPressDownload(media: LocalMedia?) {
        coverImageView!!.setOnLongClickListener {
            if (mPreviewEventListener != null) {
                mPreviewEventListener!!.onLongPressDownload(media)
            }
            false
        }
    }

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        setScaleDisplaySize(media)
        ivPlayButton.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay()
            } else {
                startPlay()
            }
        }
        itemView.setOnClickListener {
            if (config.isPauseResumePlay) {
                dispatchPlay()
            } else {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener!!.onBackPressed()
                }
            }
        }
    }

    /**
     * 视频播放状态分发
     */
    private fun dispatchPlay() {
        if (isPlayed) {
            if (isPlaying) {
                onPause()
            } else {
                onResume()
            }
        } else {
            startPlay()
        }
    }

    /**
     * 恢复播放
     */
    private fun onResume() {
        ivPlayButton.visibility = View.GONE
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine!!.onResume(videoPlayer)
        }
    }

    /**
     * 暂停播放
     */
    private fun onPause() {
        ivPlayButton.visibility = View.VISIBLE
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine!!.onPause(videoPlayer)
        }
    }

    /**
     * 是否正在播放中
     */
    val isPlaying: Boolean
        get() = (PictureSelectionConfig.videoPlayerEngine != null
                && PictureSelectionConfig.videoPlayerEngine!!.isPlaying(videoPlayer))

    /**
     * 外部播放状态监听回调
     */
    private val mPlayerListener: OnPlayerListener = object : OnPlayerListener {
        override fun onPlayerError() {
            playerDefaultUI()
        }

        override fun onPlayerReady() {
            playerIngUI()
        }

        override fun onPlayerLoading() {
            progress!!.visibility = View.VISIBLE
        }

        override fun onPlayerEnd() {
            playerDefaultUI()
        }
    }

    /**
     * 开始播放视频
     */
    fun startPlay() {
        if (videoPlayer == null) {
            throw NullPointerException("VideoPlayer cannot be empty,Please implement " + VideoPlayerEngine::class.java)
        }
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            progress!!.visibility = View.VISIBLE
            ivPlayButton.visibility = View.GONE
            mPreviewEventListener!!.onPreviewVideoTitle(media!!.fileName)
            isPlayed = true
            PictureSelectionConfig.videoPlayerEngine!!.onStarPlayer(videoPlayer, media)
        }
    }

    override fun setScaleDisplaySize(media: LocalMedia) {
        super.setScaleDisplaySize(media)
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            val layoutParams = videoPlayer!!.layoutParams
            if (layoutParams is FrameLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.gravity = Gravity.CENTER
            } else if (layoutParams is RelativeLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            } else if (layoutParams is LinearLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.gravity = Gravity.CENTER
            } else if (layoutParams is ConstraintLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.topToTop = ConstraintSet.PARENT_ID
                playerLayoutParams.bottomToBottom = ConstraintSet.PARENT_ID
            }
        }
    }

    private fun playerDefaultUI() {
        isPlayed = false
        ivPlayButton.visibility = View.VISIBLE
        progress!!.visibility = View.GONE
        coverImageView!!.visibility = View.VISIBLE
        videoPlayer!!.visibility = View.GONE
        if (mPreviewEventListener != null) {
            mPreviewEventListener!!.onPreviewVideoTitle(null)
        }
    }

    private fun playerIngUI() {
        progress!!.visibility = View.GONE
        ivPlayButton.visibility = View.GONE
        coverImageView!!.visibility = View.GONE
        videoPlayer!!.visibility = View.VISIBLE
    }

    override fun onViewAttachedToWindow() {
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine!!.onPlayerAttachedToWindow(videoPlayer)
            PictureSelectionConfig.videoPlayerEngine!!.addPlayListener(mPlayerListener)
        }
    }

    override fun onViewDetachedFromWindow() {
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine!!.onPlayerDetachedFromWindow(videoPlayer)
            PictureSelectionConfig.videoPlayerEngine!!.removePlayListener(mPlayerListener)
        }
        playerDefaultUI()
    }

    /**
     * 释放VideoView
     */
    fun releaseVideo() {
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine!!.removePlayListener(mPlayerListener)
            PictureSelectionConfig.videoPlayerEngine!!.destroy(videoPlayer)
        }
    }

    init {
        ivPlayButton = itemView.findViewById(R.id.iv_play_video)
        progress = itemView.findViewById(R.id.progress)
        val config: PictureSelectionConfig = PictureSelectionConfig.instance!!
        ivPlayButton.visibility = if (config.isPreviewZoomEffect) View.GONE else View.VISIBLE
        if (PictureSelectionConfig.videoPlayerEngine == null) {
            PictureSelectionConfig.videoPlayerEngine = MediaPlayerEngine()
        }
        videoPlayer =
            PictureSelectionConfig.videoPlayerEngine!!.onCreateVideoPlayer(itemView.context)
        if (videoPlayer == null) {
            throw NullPointerException("onCreateVideoPlayer cannot be empty,Please implement " + VideoPlayerEngine::class.java)
        }
        if (videoPlayer!!.layoutParams == null) {
            videoPlayer!!.layoutParams =
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
        }
        val viewGroup = itemView as ViewGroup
        if (viewGroup.indexOfChild(videoPlayer) != -1) {
            viewGroup.removeView(videoPlayer)
        }
        viewGroup.addView(videoPlayer, 0)
        videoPlayer!!.visibility = View.GONE
    }
}

