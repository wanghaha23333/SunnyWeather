package com.sunnyweather.android.ui.weather

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.tabs.TabLayout

class PlaceTabLayout(context: Context, attrs: AttributeSet): TabLayout(context, attrs) {

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val screenWidth = getScreenWidth()
        val widthSize: Int
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            widthSize = screenWidth / 2
            layoutParams.width = widthSize
        }
//        getChildMeasureSpec(widthMeasureSpec, 20, widthMeasureSpec / childCount)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            dm.widthPixels
        } else {
            wm.currentWindowMetrics.bounds.width()
        }
    }
}