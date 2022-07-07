package com.example.mygallery.files

import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import java.io.IOException

class VideoRequestHandler : RequestHandler() {
    var SCHEME_VIDEO = "video"
    fun canHandleRequest(data: Request): Boolean {
        val scheme: String = data.uri.scheme.toString()
        return SCHEME_VIDEO == scheme
    }

    @Throws(IOException::class)
    override fun load(request: Request, networkPolicy: Int): Result? {
        val uri: Uri = request.uri
        val path = uri.path
        if (!TextUtils.isEmpty(path)) {
            val bm =
                ThumbnailUtils.createVideoThumbnail(path!!, MediaStore.Images.Thumbnails.MINI_KIND)
            return bm?.let { Result(it, Picasso.LoadedFrom.DISK) }
        }
        return null
    }
}
