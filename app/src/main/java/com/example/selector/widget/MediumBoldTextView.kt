package com.example.selector.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MediumBoldTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) :
    AppCompatTextView(context, attrs, defStyleAttr) {
    private var mStrokeWidth = 0.6f
    override fun onDraw(canvas: Canvas) {
        val paint = paint
        if (paint.strokeWidth != mStrokeWidth) {
            paint.strokeWidth = mStrokeWidth
            paint.style = Paint.Style.FILL_AND_STROKE
        }
        super.onDraw(canvas)
    }

    fun setStrokeWidth(mStrokeWidth: Float) {
        this.mStrokeWidth = mStrokeWidth
        invalidate()
    }

    init {
        val a = context.theme.obtainStyledAttributes(attrs,
            R.styleable.PictureMediumBoldTextView,
            defStyleAttr,
            0)
        mStrokeWidth = a.getFloat(R.styleable.PictureMediumBoldTextView_stroke_Width, mStrokeWidth)
        a.recycle()
    }
}