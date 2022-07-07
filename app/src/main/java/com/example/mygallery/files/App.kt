package com.example.mygallery.files

import android.app.Application
import android.content.Context
import android.os.Build.VERSION
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import coil.ComponentRegistry
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.selector.app.IApp
import com.example.selector.app.PictureAppMaster
import com.example.selector.engine.PictureSelectorEngine
import java.io.File



class App : Application(), IApp, CameraXConfig.Provider, ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        PictureAppMaster.instance!!.app = (this)
    }

    override val appContext: Context
        get() = this
    override val pictureSelectorEngine: PictureSelectorEngine
        get() = PictureSelectorEngineImp()


    override fun newImageLoader(): ImageLoader {
        val imageLoader = ImageLoader.Builder(appContext)
        val newBuilder: ComponentRegistry.Builder = ComponentRegistry().newBuilder()
        newBuilder.add(VideoFrameDecoder.Factory())
        if (VERSION.SDK_INT >= 28) {
            newBuilder.add(ImageDecoderDecoder.Factory())
        } else {
            newBuilder.add( GifDecoder.Factory())
        }
        val componentRegistry: ComponentRegistry = newBuilder.build()
        imageLoader.components(componentRegistry)
        imageLoader.memoryCache(
            MemoryCache.Builder(appContext)
                .maxSizePercent(0.25).build()
        )
        imageLoader.diskCache(
            DiskCache.Builder()
                .directory(File(cacheDir, "image_cache"))
                .maxSizePercent(0.02)
                .build()
        )
        return imageLoader.build()
    }

    companion object {
        private val TAG = App::class.java.simpleName
    }

    override fun getCameraXConfig(): CameraXConfig {
        val build = CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR).build()
        return build
    }
}
