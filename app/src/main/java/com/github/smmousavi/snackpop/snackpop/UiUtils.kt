package com.github.smmousavi.snackpop.snackpop

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

object UiUtils {

    fun convertDpToPixel(dp: Float, context: Context): Int {
        val px: Float =
            dp * (getDisplayMetrics(context).densityDpi / 160f)
        return px.roundToInt()
    }

    fun convertPixelsToDp(px: Float, context: Context): Int {
        val dp: Float =
            px / (getDisplayMetrics(context).densityDpi / 160f)
        return dp.toInt()
    }

    fun convertSpToPixel(sp: Float, context: Context): Int {
        return (sp * getDisplayMetrics(context).scaledDensity).toInt()
    }

    fun convertPixelToSp(pixel: Float, context: Context): Int {
        return (pixel / getDisplayMetrics(context).scaledDensity).toInt()
    }

    /* get screen dimen */
    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}