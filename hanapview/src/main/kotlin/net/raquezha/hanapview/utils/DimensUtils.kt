package net.raquezha.hanapview.utils

import android.content.Context
import android.util.TypedValue

object DimensUtils {

    fun convertDpToPx(dp: Int, context: Context): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                context.resources.displayMetrics
            )
        )
    }

    fun convertDpToPx(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }
}
