package net.raquezha.hanapview.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.util.ArrayList

object AnimationUtils {
    val ANIMATION_DURATION_DEFAULT = 250


    val defaultInterpolator: Interpolator
        get() = FastOutSlowInInterpolator()

    fun revealOrFadeIn(@NonNull view: View, duration: Int, @Nullable center: Point): Animator {
        return revealOrFadeIn(view, duration, null, center)
    }

    fun revealOrFadeIn(@NonNull view: View, @Nullable listener: AnimationListener): Animator {
        return revealOrFadeIn(view, ANIMATION_DURATION_DEFAULT, listener, null)
    }

    fun revealOrFadeIn(@NonNull view: View, @Nullable center: Point): Animator {
        return revealOrFadeIn(view, ANIMATION_DURATION_DEFAULT, null, center)
    }

    fun revealOrFadeIn(@NonNull view: View, @Nullable listener: AnimationListener, @Nullable center: Point): Animator {
        return revealOrFadeIn(view, ANIMATION_DURATION_DEFAULT, listener, center)
    }

    @JvmOverloads
    fun revealOrFadeIn(@NonNull view: View, duration: Int = ANIMATION_DURATION_DEFAULT, @Nullable listener: AnimationListener? = null, @Nullable center: Point? = null): Animator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal(view, duration, listener, center)
        } else {
            fadeIn(view, duration, listener)
        }
    }

    fun hideOrFadeOut(@NonNull view: View, duration: Int, @Nullable center: Point): Animator {
        return hideOrFadeOut(view, duration, null, center)
    }

    fun hideOrFadeOut(@NonNull view: View, @Nullable listener: AnimationListener): Animator {
        return hideOrFadeOut(view, ANIMATION_DURATION_DEFAULT, listener, null)
    }

    fun hideOrFadeOut(@NonNull view: View, @Nullable center: Point): Animator {
        return hideOrFadeOut(view, ANIMATION_DURATION_DEFAULT, null, center)
    }

    fun hideOrFadeOut(@NonNull view: View, @Nullable listener: AnimationListener, @Nullable center: Point): Animator {
        return hideOrFadeOut(view, ANIMATION_DURATION_DEFAULT, listener, center)
    }

    @JvmOverloads
    fun hideOrFadeOut(@NonNull view: View, duration: Int = ANIMATION_DURATION_DEFAULT, @Nullable listener: AnimationListener? = null, @Nullable center: Point? = null): Animator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hide(view, duration, listener, center)
        } else {
            fadeOut(view, duration, listener)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun reveal(@NonNull view: View, duration: Int, @Nullable center: Point): Animator {
        return reveal(view, duration, null, center)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun reveal(@NonNull view: View, @Nullable listener: AnimationListener): Animator {
        return reveal(view, ANIMATION_DURATION_DEFAULT, listener, null)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun reveal(@NonNull view: View, @Nullable center: Point): Animator {
        return reveal(view, ANIMATION_DURATION_DEFAULT, null, center)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun reveal(@NonNull view: View, @Nullable listener: AnimationListener, @Nullable center: Point): Animator {
        return reveal(view, ANIMATION_DURATION_DEFAULT, listener, center)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @JvmOverloads
    fun reveal(@NonNull view: View, duration: Int = ANIMATION_DURATION_DEFAULT, @Nullable listener: AnimationListener? = null, @Nullable center: Point? = null): Animator {
        var center = center
        if (center == null) {
            center = getDefaultCenter(view)
        }

        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            center.x,
            center.y,
            0f,
            getRevealRadius(center, view).toFloat()
        )
        anim.addListener(object : DefaultActionAnimationListener(view, listener) {
            override fun defaultOnAnimationStart(@NonNull view: View) {
                view.visibility = View.VISIBLE
            }
        })

        anim.duration = duration.toLong()
        anim.interpolator = defaultInterpolator
        return anim
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun hide(@NonNull view: View, duration: Int, @Nullable center: Point): Animator {
        return hide(view, duration, null, center)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun hide(@NonNull view: View, @Nullable listener: AnimationListener): Animator {
        return hide(view, ANIMATION_DURATION_DEFAULT, listener, null)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun hide(@NonNull view: View, @Nullable center: Point): Animator {
        return hide(view, ANIMATION_DURATION_DEFAULT, null, center)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun hide(@NonNull view: View, @Nullable listener: AnimationListener, @Nullable center: Point): Animator {
        return hide(view, ANIMATION_DURATION_DEFAULT, listener, center)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @JvmOverloads
    fun hide(@NonNull view: View, duration: Int = ANIMATION_DURATION_DEFAULT, @Nullable listener: AnimationListener? = null, @Nullable center: Point? = null): Animator {
        var center = center
        if (center == null) {
            center = getDefaultCenter(view)
        }

        val anim = ViewAnimationUtils.createCircularReveal(
            view,
            center.x,
            center.y,
            getRevealRadius(center, view).toFloat(),
            0f
        )
        anim.addListener(object : DefaultActionAnimationListener(view, listener) {
            override fun defaultOnAnimationEnd(@NonNull view: View) {
                view.visibility = View.GONE
            }
        })

        anim.duration = duration.toLong()
        anim.interpolator = defaultInterpolator
        return anim
    }


    internal fun getDefaultCenter(@NonNull view: View): Point {
        return Point(view.width / 2, view.height / 2)
    }

    internal fun getRevealRadius(@NonNull center: Point, @NonNull view: View): Int {
        var radius = 0f
        val points = ArrayList<Point>()
        points.add(Point(view.left, view.top))
        points.add(Point(view.right, view.top))
        points.add(Point(view.left, view.bottom))
        points.add(Point(view.right, view.bottom))

        for (point in points) {
            val distance = distance(center, point)
            if (distance > radius) {
                radius = distance
            }
        }

        return Math.ceil(radius.toDouble()).toInt()
    }


    fun distance(first: Point, second: Point): Float {
        return Math.sqrt(
            Math.pow((first.x - second.x).toDouble(), 2.0) + Math.pow(
                (first.y - second.y).toDouble(),
                2.0
            )
        ).toFloat()
    }

    fun fadeIn(@NonNull view: View, @Nullable listener: AnimationListener): Animator {
        return fadeIn(view, ANIMATION_DURATION_DEFAULT, listener)
    }

    @JvmOverloads
    fun fadeIn(@NonNull view: View, duration: Int = ANIMATION_DURATION_DEFAULT, @Nullable listener: AnimationListener? = null): Animator {
        if (view.alpha == 1f) {
            view.alpha = 0f
        }

        val anim = ObjectAnimator.ofFloat(view, "alpha", 1f)
        anim.addListener(object : DefaultActionAnimationListener(view, listener) {
            override fun defaultOnAnimationStart(@NonNull view: View) {
                view.visibility = View.VISIBLE
            }
        })

        anim.duration = duration.toLong()
        anim.interpolator = defaultInterpolator
        return anim
    }

    fun fadeOut(@NonNull view: View, @Nullable listener: AnimationListener): Animator {
        return fadeOut(view, ANIMATION_DURATION_DEFAULT, listener)
    }

    @JvmOverloads
    fun fadeOut(@NonNull view: View, duration: Int = ANIMATION_DURATION_DEFAULT, @Nullable listener: AnimationListener? = null): Animator {
        val anim = ObjectAnimator.ofFloat(view, "alpha", 0f)
        anim.addListener(object : DefaultActionAnimationListener(view, listener) {
            override fun defaultOnAnimationEnd(@NonNull view: View) {
                view.visibility = View.GONE
            }
        })

        anim.duration = duration.toLong()
        anim.interpolator = defaultInterpolator
        return anim
    }

    @JvmOverloads
    fun verticalSlideView(
        @NonNull view: View, fromHeight: Int,
        toHeight: Int, @Nullable listener: AnimationListener? = null
    ): Animator {
        return verticalSlideView(view, fromHeight, toHeight, ANIMATION_DURATION_DEFAULT, listener)
    }

    @JvmOverloads
    fun verticalSlideView(
        @NonNull view: View, fromHeight: Int,
        toHeight: Int,
        duration: Int, @Nullable listener: AnimationListener? = null
    ): Animator {
        val anim = ValueAnimator
            .ofInt(fromHeight, toHeight)

        anim.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }

        anim.addListener(DefaultActionAnimationListener(view, listener))

        anim.duration = duration.toLong()
        anim.interpolator = defaultInterpolator
        return anim
    }


    interface AnimationListener {
        /**
         * @return return true to override the default behaviour
         */
        fun onAnimationStart(@NonNull view: View): Boolean

        /**
         * @return return true to override the default behaviour
         */
        fun onAnimationEnd(@NonNull view: View): Boolean

        /**
         * @return return true to override the default behaviour
         */
        fun onAnimationCancel(@NonNull view: View): Boolean
    }

    open class DefaultActionAnimationListener internal constructor(@param:NonNull private val view: View, @param:Nullable private val listener: AnimationListener?) :
        AnimatorListenerAdapter() {

        override fun onAnimationStart(animation: Animator) {
            if (listener == null || !listener.onAnimationStart(view)) {
                defaultOnAnimationStart(view)
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            if (listener == null || !listener.onAnimationEnd(view)) {
                defaultOnAnimationEnd(view)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            if (listener == null || !listener.onAnimationCancel(view)) {
                defaultOnAnimationCancel(view)
            }
        }

        internal open fun defaultOnAnimationStart(@NonNull view: View) {
            // No default action
        }

        internal open fun defaultOnAnimationEnd(@NonNull view: View) {
            // No default action
        }

        internal open fun defaultOnAnimationCancel(@NonNull view: View) {
            // No default action
        }
    }
}
