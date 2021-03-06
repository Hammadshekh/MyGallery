package com.example.selector.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.selector.basic.PictureContentResolver
import com.example.selector.config.PictureConfig
import com.example.selector.config.PictureMimeType
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream

object BitmapUtils {
    private const val ARGB_8888_MEMORY_BYTE = 4
    private const val MAX_BITMAP_SIZE = 100 * 1024 * 1024 // 100 MB

    /**
     * 判断拍照 图片是否旋转
     *
     * @param context
     * @param path    资源路径
     */
    fun rotateImage(context: Context?, path: String?) {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        var bitmap: Bitmap? = null
        try {
            val degree = readPictureDegree(context, path)
            if (degree > 0) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                if (PictureMimeType.isContent(path!!)) {
                    inputStream = context?.let {
                        PictureContentResolver.getContentResolverOpenInputStream(
                            it,
                            Uri.parse(path))
                    }
                    BitmapFactory.decodeStream(inputStream, null, options)
                } else {
                    BitmapFactory.decodeFile(path, options)
                }
                options.inSampleSize = computeSize(options.outWidth, options.outHeight)
                options.inJustDecodeBounds = false
                if (PictureMimeType.isContent(path)) {
                    inputStream = context?.let {
                        PictureContentResolver.getContentResolverOpenInputStream(
                            it,
                            Uri.parse(path))
                    }
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                } else {
                    bitmap = BitmapFactory.decodeFile(path, options)
                }
                if (bitmap != null) {
                    bitmap = rotatingImage(bitmap, degree)
                    outputStream = if (PictureMimeType.isContent(path)) ({
                        context?.let {
                            PictureContentResolver.getContentResolverOpenOutputStream(
                                it,
                                Uri.parse(path))
                        }
                    }) as FileOutputStream? else {
                        FileOutputStream(path)
                    }
                    saveBitmapFile(bitmap, outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(inputStream)
            PictureFileUtils.close(outputStream)
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    /**
     * 旋转Bitmap
     *
     * @param bitmap
     * @param angle
     * @return
     */
    fun rotatingImage(bitmap: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 保存Bitmap至本地
     *
     * @param bitmap
     * @param fos
     */
    private fun saveBitmapFile(bitmap: Bitmap?, fos: FileOutputStream?) {
        var stream: ByteArrayOutputStream? = null
        try {
            stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos!!.write(stream.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(fos)
            PictureFileUtils.close(stream)
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param context
     * @param filePath 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(context: Context?, filePath: String?): Int {
        val exifInterface: ExifInterface
        var inputStream: InputStream? = null
        return try {
            if (filePath?.let { PictureMimeType.isContent(it) } == true) {
                inputStream = context?.let {
                    PictureContentResolver.getContentResolverOpenInputStream(
                        it,
                        Uri.parse(filePath))
                }
                exifInterface = inputStream?.let { ExifInterface(it) }!!
            } else {
                exifInterface = ExifInterface(filePath!!)
            }
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            PictureFileUtils.close(inputStream)
        }
    }

    /**
     * 获取图片的缩放比例
     *
     * @param imageWidth  图片原始宽度
     * @param imageHeight 图片原始高度
     * @return
     */
    fun getMaxImageSize(imageWidth: Int, imageHeight: Int): IntArray {
        var maxWidth: Int = PictureConfig.UNSET
        var maxHeight: Int = PictureConfig.UNSET
        if (imageWidth == 0 && imageHeight == 0) {
            return intArrayOf(maxWidth, maxHeight)
        }
        var inSampleSize = computeSize(imageWidth, imageHeight)
        val totalMemory = totalMemory
        var decodeAttemptSuccess = false
        while (!decodeAttemptSuccess) {
            maxWidth = imageWidth / inSampleSize
            maxHeight = imageHeight / inSampleSize
            val bitmapSize = maxWidth * maxHeight * ARGB_8888_MEMORY_BYTE
            if (bitmapSize > totalMemory) {
                inSampleSize *= 2
                continue
            }
            decodeAttemptSuccess = true
        }
        return intArrayOf(maxWidth, maxHeight)
    }

    /**
     * 获取当前应用可用内存
     *
     * @return
     */
    val totalMemory: Long
        get() {
            val totalMemory = Runtime.getRuntime().totalMemory()
            return if (totalMemory > MAX_BITMAP_SIZE) MAX_BITMAP_SIZE.toLong() else totalMemory
        }

    /**
     * 计算图片合适压缩比较
     *
     * @param srcWidth  资源宽度
     * @param srcHeight 资源高度
     * @return
     */
    fun computeSize(srcWidth: Int, srcHeight: Int): Int {
        var srcWidth = srcWidth
        var srcHeight = srcHeight
        srcWidth = if (srcWidth % 2 == 1) srcWidth + 1 else srcWidth
        srcHeight = if (srcHeight % 2 == 1) srcHeight + 1 else srcHeight
        val longSide = Math.max(srcWidth, srcHeight)
        val shortSide = Math.min(srcWidth, srcHeight)
        val scale = shortSide.toFloat() / longSide
        return if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                1
            } else if (longSide < 4990) {
                2
            } else if (longSide > 4990 && longSide < 10240) {
                4
            } else {
                longSide / 1280
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (longSide / 1280 == 0) 1 else longSide / 1280
        } else {
            Math.ceil(longSide / (1280.0 / scale)).toInt()
        }
    }
}
