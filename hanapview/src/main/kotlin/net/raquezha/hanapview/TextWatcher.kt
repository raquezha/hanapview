package net.raquezha.hanapview

import android.text.Editable
import android.text.TextWatcher

abstract class TextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // No action
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // No action
    }

    override fun afterTextChanged(s: Editable) {
        // No action
    }
}
