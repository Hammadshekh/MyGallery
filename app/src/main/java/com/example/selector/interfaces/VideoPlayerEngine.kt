package com.example.selector.interfaces

import android.content.Context
import android.view.View
import com.example.selector.engine.MediaPlayerView
import com.luck.picture.lib.entity.LocalMedia

interface VideoPlayerEngine<T> {
    /**
     * Create player instance
     *
     * @param context
     */
    fun onCreateVideoPlayer(context: Context?): View?

    /**
     * Start playing video
     *
     * @param player
     * @param media
     */
    fun onStarPlayer(player: T, media: LocalMedia?)

    /**
     * 恢复播放
     */
    fun onResume(player: T)

    /**
     * 暂停播放
     */
    fun onPause(player: T)

    /**
     * Video Playing status
     *
     * @param player
     */
    fun isPlaying(player: T): Boolean

    /**
     * addPlayListener
     * [OnPlayerListener]
     *
     * @param playerListener
     */
     fun addPlayListener(playerListener: OnPlayerListener?)

    /**
     * removePlayListener
     *
     *
     * [OnPlayerListener]
     *
     * @param playerListener
     */
    fun removePlayListener(playerListener: OnPlayerListener?)

    /**
     * Player attached to window
     *
     * @param player
     */
    fun onPlayerAttachedToWindow(player: MediaPlayerView?)

    /**
     * Player detached to window
     *
     * @param player
     */
    fun onPlayerDetachedFromWindow(player: T)

    /**
     * destroy release player
     *
     * @param player
     */
    fun destroy(player: T)
}


