package com.example.camerax.listener

import android.content.Context
import android.view.OrientationEventListener
import android.view.Surface

class CameraXOrientationEventListener(
    context: Context?,
    private val changedListener: OnOrientationChangedListener?,
) :
    OrientationEventListener(context) {
    private var mRotation = Surface.ROTATION_0
    override fun onOrientationChanged(orientation: Int) {
        if (orientation == ORIENTATION_UNKNOWN) {
            return
        }
        val currentRotation: Int
        currentRotation = if (orientation > 80 && orientation < 100) {
            Surface.ROTATION_270
        } else if (orientation > 170 && orientation < 190) {
            Surface.ROTATION_180
        } else if (orientation > 260 && orientation < 280) {
            Surface.ROTATION_90
        } else {
            Surface.ROTATION_0
        }
        if (mRotation != currentRotation) {
            mRotation = currentRotation
            changedListener?.onOrientationChanged(mRotation)
        }
    }

    interface OnOrientationChangedListener {
        fun onOrientationChanged(orientation: Int)
    }

    fun star() {
        enable()
    }

    fun stop() {
        disable()
    }
}
