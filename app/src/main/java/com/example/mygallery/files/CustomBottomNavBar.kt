package com.example.mygallery.files

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.example.mygallery.R
import com.example.selector.widget.BottomNavBar

class CustomBottomNavBar : BottomNavBar, View.OnClickListener {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
    }

     override fun inflateLayout() {
        inflate(context, R.layout.ps_custom_bottom_nav_bar, this)
    }
}