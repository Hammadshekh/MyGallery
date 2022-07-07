package com.example.mygallery.files

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.example.mygallery.R
import com.example.selector.widget.TitleBar

class CustomTitleBar : TitleBar, View.OnClickListener {

    override  val titleCancelView: TextView?
        get() = tvCancel

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
    }

    private fun inflateLayout() {
        inflate(context, R.layout.ps_custom_title_bar, this)
    }

    override fun setTitleBarStyle() {
        super.setTitleBarStyle()
    }
}