package net.raquezha.hanapview.utils

import android.view.View

abstract open class AnimationListener : AnimationUtils.AnimationListener {
    override fun onAnimationStart(view: View): Boolean {
        // No action
        return false
    }

    override fun onAnimationEnd(view: View): Boolean {
        // No action
        return false
    }

    override fun onAnimationCancel(view: View): Boolean {
        // No action
        return false
    }
}
