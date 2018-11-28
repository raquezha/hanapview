package net.raquezha.hanapview.utils

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

import java.lang.reflect.Field

/**
 * @author Fernando A. H. Falkiewicz
 */
object EditTextReflectionUtils {
    private val TAG = EditTextReflectionUtils::class.java.simpleName
    private val EDIT_TEXT_FIELD_CURSOR_DRAWABLE_RES = "mCursorDrawableRes"
    private val EDIT_TEXT_FIELD_EDITOR = "mEditor"
    private val EDIT_TEXT_FIELD_CURSOR_DRAWABLE = "mCursorDrawable"

    /**
     * Uses reflection to set an EditText cursor drawable
     */
    fun setCursorDrawable(editText: EditText, drawable: Int) {
        try {
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            val f = TextView::class.java.getDeclaredField(EDIT_TEXT_FIELD_CURSOR_DRAWABLE_RES)
            f.isAccessible = true
            f.set(editText, drawable)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }

    /**
     * Uses reflection to set an EditText cursor color
     */
    fun setCursorColor(editText: EditText, @ColorInt color: Int) {
        try {
            // Get the cursor resource id
            var field = TextView::class.java.getDeclaredField(EDIT_TEXT_FIELD_CURSOR_DRAWABLE_RES)
            field.isAccessible = true
            val drawableResId = field.getInt(editText)

            // Get the editor
            field = TextView::class.java.getDeclaredField(EDIT_TEXT_FIELD_EDITOR)
            field.isAccessible = true
            val editor = field.get(editText)

            // Get the drawable and set a color filter
            val drawable = ContextCompat.getDrawable(editText.context, drawableResId)
            drawable!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            val drawables = arrayOf(drawable, drawable)

            // Set the drawables
            field = editor.javaClass.getDeclaredField(EDIT_TEXT_FIELD_CURSOR_DRAWABLE)
            field.isAccessible = true
            field.set(editor, drawables)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }

    }
}
