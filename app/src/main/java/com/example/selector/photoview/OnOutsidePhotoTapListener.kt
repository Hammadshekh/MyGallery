package com.example.selector.photoview

import android.widget.ImageView

interface OnOutsidePhotoTapListener {
    /**
     * The outside of the photo has been tapped
     */
    fun onOutsidePhotoTap(imageView: ImageView?)
}