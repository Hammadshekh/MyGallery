package com.example.mygallery.files

import android.content.Context
import android.util.AttributeSet
import com.example.selector.widget.PreviewTitleBar

class CustomPreviewTitleBar : PreviewTitleBar {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
    }
}
