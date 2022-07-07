package com.example.mygallery.files

import android.content.Context
import android.util.Log
import com.example.mygallery.R
import com.example.mygallery.engine.GlideEngine
import com.example.mygallery.engine.ImageEngine
import com.example.selector.basic.IBridgeLoaderFactory
import com.example.selector.config.InjectResourceSource
import com.example.selector.engine.*
import com.example.selector.interfaces.OnInjectLayoutResourceListener
import com.example.selector.interfaces.OnResultCallbackListener
import com.luck.picture.lib.entity.LocalMedia
import java.util.*

class PictureSelectorEngineImp : PictureSelectorEngine,
    com.example.selector.engine.PictureSelectorEngine {
    /**
     * 重新创建[ImageEngine]引擎
     *
     * @return
     */
    override fun createImageLoaderEngine(): ImageEngine {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致ImageEngine被回收
        return GlideEngine.createGlideEngine()
    }

    /**
     * 重新创建[CompressEngine]引擎
     *
     * @return
     */
    override fun createCompressEngine(): CompressEngine? {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致CompressEngine被回收
        return null
    }

    /**
     * 重新创建[CompressEngine]引擎
     *
     * @return
     */
    override fun createCompressFileEngine(): CompressFileEngine? {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致CompressFileEngine被回收
        return null
    }

    /**
     * 重新创建[ExtendLoaderEngine]引擎
     *
     * @return
     */
    override fun createLoaderDataEngine(): ExtendLoaderEngine? {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致ExtendLoaderEngine被回收
        return null
    }

    /**
     * 重新创建[IBridgeMediaLoader]引擎
     * @return
     */
    override fun onCreateLoader(): IBridgeLoaderFactory? {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致IBridgeLoaderFactory被回收
        return null
    }

    /**
     * 重新创建[SandboxFileEngine]引擎
     *
     * @return
     */
    override fun createSandboxFileEngine(): SandboxFileEngine? {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致SandboxFileEngine被回收
        return null
    }

    /**
     * 重新创建[UriToFileTransformEngine]引擎
     *
     * @return
     */
    override fun createUriToFileTransformEngine(): UriToFileTransformEngine? {
        // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致UriToFileTransformEngine被回收
        return null
    }

    /**
     * 如果出现内存不足导致OnInjectLayoutResourceListener被回收，需要重新引入自定义布局
     *
     * @return
     */
    override fun createLayoutResourceListener(): OnInjectLayoutResourceListener {
        return object : OnInjectLayoutResourceListener {
            override fun getLayoutResourceId(context: Context?, resourceSource: Int): Int {
                return when (resourceSource) {
                    InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE -> R.layout.ps_custom_fragment_selector
                    InjectResourceSource.PREVIEW_LAYOUT_RESOURCE -> R.layout.ps_custom_fragment_preview
                    InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE -> R.layout.ps_custom_item_grid_image
                    InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE -> R.layout.ps_custom_item_grid_video
                    InjectResourceSource.MAIN_ITEM_AUDIO_LAYOUT_RESOURCE -> R.layout.ps_custom_item_grid_audio
                    InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE -> R.layout.ps_custom_album_folder_item
                    InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE -> R.layout.ps_custom_preview_image
                    InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE -> R.layout.ps_custom_preview_video
                    InjectResourceSource.PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE -> R.layout.ps_custom_preview_gallery_item
                    else -> 0
                }
            }
        }
    }

    // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致OnResultCallbackListener被回收
    // 可以在这里进行一些补救措施，通过广播或其他方式将结果推送到相应页面，防止结果丢失的情况
    override val resultCallbackListener: OnResultCallbackListener<LocalMedia>
        get() = object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: ArrayList<LocalMedia>) {
                // TODO 这种情况是内存极度不足的情况下，比如开启开发者选项中的不保留活动或后台进程限制，导致OnResultCallbackListener被回收
                // 可以在这里进行一些补救措施，通过广播或其他方式将结果推送到相应页面，防止结果丢失的情况
                Log.i(TAG, "onResult:" + result.size)
            }

            override fun onCancel() {
                Log.i(TAG, "PictureSelector onCancel")
            }
        }

    companion object {
        private val TAG = PictureSelectorEngineImp::class.java.simpleName
    }
}
