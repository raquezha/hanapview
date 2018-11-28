package net.raquezha.hanapview.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import net.raquezha.hanapview.R

object ContextUtils {

    fun scanForActivity(context: Context): Activity? {
        if (context is Activity)
            return context
        else if (context is ContextWrapper)
            return scanForActivity(context.baseContext)

        return null
    }

    @ColorInt
    fun getPrimaryColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, value, true)
        return value.data
    }

    @ColorInt
    fun getAccentColor(context: Context): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
