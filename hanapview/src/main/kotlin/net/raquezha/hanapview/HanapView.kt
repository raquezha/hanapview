package net.raquezha.hanapview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.google.android.material.tabs.TabLayout
import net.raquezha.hanapview.utils.*

class HanapView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    var animationDuration = AnimationUtils.ANIMATION_DURATION_DEFAULT
    var revealAnimationCenter: Point? = null
        get() {
            if (field != null) {
                return field
            }

            val centerX = width - DimensUtils.convertDpToPx(ANIMATION_CENTER_PADDING, context)
            val centerY = height / 2

            this.revealAnimationCenter = Point(centerX, centerY)
            return field
        }
    var query: CharSequence? = null
    var oldQuery: CharSequence? = null
    var isSearchOpen = false
        private set
    var isClearingFocus = false


    @Style
    @get:Style
    var cardStyle = STYLE_BAR
        set(@Style style) {
            field = style

            val layoutParams =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            var elevation = 0f

            when (style) {
                STYLE_CARD -> {
                    searchContainer!!.background = cardStyleBackground
                    bottomLine!!.visibility = View.GONE

                    val cardPadding = DimensUtils.convertDpToPx(CARD_PADDING, context)
                    layoutParams.setMargins(cardPadding, cardPadding, cardPadding, cardPadding)

                    elevation = DimensUtils.convertDpToPx(CARD_ELEVATION, context).toFloat()
                }
                STYLE_BAR -> {
                    searchContainer!!.setBackgroundColor(Color.WHITE)
                    bottomLine!!.visibility = View.VISIBLE
                }
                else -> {
                    searchContainer!!.setBackgroundColor(Color.WHITE)
                    bottomLine!!.visibility = View.VISIBLE
                }
            }

            searchContainer!!.layoutParams = layoutParams
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                searchContainer!!.elevation = elevation
            }
        }

    var searchContainer: ViewGroup? = null

    var searchEditText: EditText? = null
        private set
    var backButton: ImageButton? = null
    var clearButton: ImageButton? = null
    var bottomLine: View? = null

    var tabLayout: TabLayout? = null
        set(tabLayout) {
            field = tabLayout

            this.tabLayout!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    tabLayoutInitialHeight = tabLayout?.height!!
                    tabLayout.viewTreeObserver?.removeOnPreDrawListener(this)
                    return true
                }
            })

            this.tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener() {
                override fun onTabUnselected(tab: TabLayout.Tab) {
                    closeSearch()
                }
            })
        }
    var tabLayoutInitialHeight: Int = 0

    var onQueryChangeListener: OnQueryTextListener? = null
    var searchViewListener: SearchViewListener? = null

    val cardStyleBackground: GradientDrawable
        get() {
            val drawable = GradientDrawable()
            drawable.setColor(Color.WHITE)
            drawable.cornerRadius = DimensUtils.convertDpToPx(CARD_CORNER_RADIUS, context).toFloat()
            return drawable
        }

    @IntDef(STYLE_BAR, STYLE_CARD)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Style

    init {
        inflate()
        initStyle(attrs, defStyleAttr)
        initSearchEditText()
        initClickListeners()
        if (!isInEditMode) {
            visibility = View.INVISIBLE
        }
    }

    private fun inflate() {
        LayoutInflater.from(context).inflate(R.layout.hanap_view, this, true)
        searchContainer = findViewById(R.id.searchContainer)
        searchEditText = findViewById(R.id.searchEditText)
        backButton = findViewById(R.id.buttonBack)
        clearButton = findViewById(R.id.buttonClear)
        bottomLine = findViewById(R.id.bottomLine)
    }

    private fun initStyle(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HanapView, defStyleAttr, 0)
        if (typedArray == null) {
            cardStyle = cardStyle
            return
        }

        if (typedArray.hasValue(R.styleable.HanapView_type)) {
            cardStyle = typedArray.getInt(R.styleable.HanapView_type, cardStyle)
        }

        if (typedArray.hasValue(R.styleable.HanapView_backIconAlpha)) {
            setBackIconAlpha(typedArray.getFloat(R.styleable.HanapView_backIconAlpha, BACK_ICON_ALPHA_DEFAULT))
        }

        if (typedArray.hasValue(R.styleable.HanapView_iconsAlpha)) {
            setIconsAlpha(typedArray.getFloat(R.styleable.HanapView_iconsAlpha, ICONS_ALPHA_DEFAULT))
        }

        if (typedArray.hasValue(R.styleable.HanapView_backIconTint)) {
            setBackIconColor(
                typedArray.getColor(
                    R.styleable.HanapView_backIconTint,
                    ContextUtils.getPrimaryColor(context)
                )
            )
        }

        if (typedArray.hasValue(R.styleable.HanapView_iconsTint)) {
            setIconsColor(typedArray.getColor(R.styleable.HanapView_iconsTint, Color.BLACK))
        }

        if (typedArray.hasValue(R.styleable.HanapView_cursorColor)) {
            setCursorColor(
                typedArray.getColor(
                    R.styleable.HanapView_cursorColor,
                    ContextUtils.getAccentColor(context)
                )
            )
        }

        if (typedArray.hasValue(R.styleable.HanapView_hintColor)) {
            setHintTextColor(
                typedArray.getColor(
                    R.styleable.HanapView_hintColor,
                    ContextCompat.getColor(context, R.color.default_textColorHint)
                )
            )
        }

        if (typedArray.hasValue(R.styleable.HanapView_searchBackground)) {
            setSearchBackground(typedArray.getDrawable(R.styleable.HanapView_searchBackground))
        }

        if (typedArray.hasValue(R.styleable.HanapView_searchBackIcon)) {
            setBackIconDrawable(typedArray.getDrawable(R.styleable.HanapView_searchBackIcon))
        }

        if (typedArray.hasValue(R.styleable.HanapView_searchClearIcon)) {
            setClearIconDrawable(typedArray.getDrawable(R.styleable.HanapView_searchClearIcon))
        }


        if (typedArray.hasValue(R.styleable.HanapView_android_hint)) {
            setHint(typedArray.getString(R.styleable.HanapView_android_hint))
        }

        if (typedArray.hasValue(R.styleable.HanapView_android_inputType)) {
            setInputType(
                typedArray.getInt(
                    R.styleable.HanapView_android_inputType,
                    EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                )
            )
        }

        if (typedArray.hasValue(R.styleable.HanapView_android_textColor)) {
            setTextColor(
                typedArray.getColor(
                    R.styleable.HanapView_android_textColor,
                    ContextCompat.getColor(context, R.color.default_textColorHint)
                )
            )
        }

        typedArray.recycle()
    }

    private fun initSearchEditText() {
        searchEditText?.setOnEditorActionListener { _, _, _ ->
            onSubmitQuery()
            true
        }

        searchEditText?.addTextChangedListener(object : TextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                this@HanapView.onTextChanged(s)
            }
        })

        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                ContextUtils.showKeyboard(searchEditText!!)
            }
        }
    }

    private fun initClickListeners() {
        backButton?.setOnClickListener { closeSearch() }
        clearButton?.setOnClickListener { clearSearch() }
    }

    override fun clearFocus() {
        isClearingFocus = true
        ContextUtils.hideKeyboard(this)
        super.clearFocus()
        searchEditText?.clearFocus()
        isClearingFocus = false
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        if (isClearingFocus) {
            return false
        }
        return if (!isFocusable) {
            false
        } else searchEditText?.requestFocus(direction, previouslyFocusedRect)!!
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        val savedState = SavedState(superState!!)
        savedState.query = if (query != null) query!!.toString() else null
        savedState.isSearchOpen = isSearchOpen
        savedState.animationDuration = animationDuration

        return savedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        if (state.isSearchOpen) {
            showSearch(false)
            setQuery(
                state.query,
                false
            )
        }

        super.onRestoreInstanceState(state.superState)
    }

    fun clearSearch() {
        searchEditText!!.text = null
        if (onQueryChangeListener != null) {
            onQueryChangeListener!!.onQueryTextCleared()
        }
    }

    private fun onTextChanged(newText: CharSequence) {
        query = newText
        val hasText = !TextUtils.isEmpty(newText)
        if (hasText) {
            clearButton!!.visibility = View.VISIBLE
        } else {
            clearButton!!.visibility = View.GONE
        }

        if (onQueryChangeListener != null && !TextUtils.equals(newText, oldQuery)) {
            onQueryChangeListener!!.onQueryTextChange(newText.toString())
        }
        oldQuery = newText.toString()
    }

    private fun onSubmitQuery() {
        val submittedQuery = searchEditText!!.text
        if (submittedQuery != null && TextUtils.getTrimmedLength(submittedQuery) > 0) {
            if (onQueryChangeListener == null || !onQueryChangeListener!!.onQueryTextSubmit(submittedQuery.toString())) {
                closeSearch()
                searchEditText!!.text = null
            }
        }
    }

    /**
     * Shows search
     *
     * @param animate true to animate
     */
    @JvmOverloads
    fun showSearch(animate: Boolean = true) {
        if (isSearchOpen) {
            return
        }

        searchEditText!!.text = null
        searchEditText!!.requestFocus()

        if (animate) {
            val animationListener = object : AnimationListener() {
                override fun onAnimationEnd(@NonNull view: View): Boolean {
                    if (searchViewListener != null) {
                        searchViewListener!!.onSearchViewShownAnimation()
                    }
                    return false
                }
            }
            AnimationUtils.revealOrFadeIn(this, animationDuration, animationListener, revealAnimationCenter).start()
        } else {
            visibility = View.VISIBLE
        }

        hideTabLayout(animate)

        isSearchOpen = true
        if (searchViewListener != null) {
            searchViewListener!!.onSearchViewShown()
        }
    }

    /**
     * Closes search
     *
     * @param animate true if should be animated
     */
    @JvmOverloads
    fun closeSearch(animate: Boolean = true) {
        if (!isSearchOpen) {
            return
        }

        searchEditText?.text = null
        clearFocus()

        if (animate) {
            val animationListener = object : AnimationListener() {
                override fun onAnimationEnd(@NonNull view: View): Boolean {
                    if (searchViewListener != null) {
                        searchViewListener!!.onSearchViewClosedAnimation()
                    }
                    return false
                }
            }
            AnimationUtils.hideOrFadeOut(this, animationDuration, animationListener, revealAnimationCenter).start()
        } else {
            visibility = View.INVISIBLE
        }

        showTabLayout(animate)

        isSearchOpen = false
        if (searchViewListener != null) {
            searchViewListener!!.onSearchViewClosed()
        }
    }

    /**
     * Shows the attached TabLayout
     *
     * @param animate true if should be animated
     */
    @JvmOverloads
    fun showTabLayout(animate: Boolean = true) {
        if (this.tabLayout == null) {
            return
        }

        if (animate) {
            AnimationUtils.verticalSlideView(this.tabLayout!!, 0, tabLayoutInitialHeight, animationDuration).start()
        } else {
            this.tabLayout!!.visibility = View.VISIBLE
        }
    }

    /**
     * Hides the attached TabLayout
     *
     * @param animate true if should be animated
     */
    @JvmOverloads
    fun hideTabLayout(animate: Boolean = true) {
        if (this.tabLayout == null) {
            return
        }

        if (animate) {
            AnimationUtils.verticalSlideView(this.tabLayout!!, this.tabLayout!!.height, 0, animationDuration)
                .start()
        } else {
            this.tabLayout!!.visibility = View.GONE
        }
    }

    /**
     * Call this method on the onBackPressed method of the activity.
     * Returns true if the search was open and it closed with the call.
     * Returns false if the search was already closed and can continue with the default activity behavior.
     *
     * @return true if acted, false if not acted
     */
    fun onBackPressed(): Boolean {
        if (isSearchOpen) {
            closeSearch()
            return true
        }
        return false
    }

    /**
     * Sets icons alpha, does not set the back/up icon
     */
    fun setIconsAlpha(alpha: Float) {
        clearButton!!.alpha = alpha
    }

    /**
     * Sets icons colors, does not set back/up icon
     */
    fun setIconsColor(@ColorInt color: Int) {
        ImageViewCompat.setImageTintList(clearButton!!, ColorStateList.valueOf(color))
    }

    /**
     * Sets the back/up icon alpha; does not set other icons
     */
    fun setBackIconAlpha(alpha: Float) {
        backButton!!.alpha = alpha
    }

    /**
     * Sets the back/up icon drawable; does not set other icons
     */
    fun setBackIconColor(@ColorInt color: Int) {
        ImageViewCompat.setImageTintList(backButton!!, ColorStateList.valueOf(color))
    }

    /**
     * Sets the back/up icon drawable
     */
    fun setBackIconDrawable(drawable: Drawable?) {
        backButton!!.setImageDrawable(drawable)
    }

    /**
     * Sets a custom Drawable for the clear text button
     */
    fun setClearIconDrawable(drawable: Drawable?) {
        clearButton!!.setImageDrawable(drawable)
    }

    fun setSearchBackground(background: Drawable?) {
        searchContainer!!.background = background
    }

    fun setTextColor(@ColorInt color: Int) {
        searchEditText!!.setTextColor(color)
    }

    fun setHintTextColor(@ColorInt color: Int) {
        searchEditText!!.setHintTextColor(color)
    }

    fun setHint(hint: CharSequence?) {
        searchEditText!!.hint = hint
    }

    fun setInputType(inputType: Int) {
        searchEditText!!.inputType = inputType
    }

    /**
     * Uses reflection to set the search EditText cursor drawable
     */
    fun setCursorDrawable(@DrawableRes drawable: Int) {
        EditTextReflectionUtils.setCursorDrawable(searchEditText!!, drawable)
    }

    /**
     * Uses reflection to set the search EditText cursor color
     */
    fun setCursorColor(@ColorInt color: Int) {
        EditTextReflectionUtils.setCursorColor(searchEditText!!, color)
    }


    /**
     * @param query  query text
     * @param submit true to submit the query
     */
    fun setQuery(query: CharSequence?, submit: Boolean) {
        searchEditText!!.setText(query)
        if (query != null) {
            searchEditText!!.setSelection(searchEditText!!.length())
            this.query = query
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }


    /**
     * Handle click events for the MenuItem.
     *
     * @param menuItem MenuItem that opens the search
     */
    fun setMenuItem(@NonNull menuItem: MenuItem) {
        menuItem.setOnMenuItemClickListener { item ->
            showSearch()
            true
        }
    }

    /**
     * @param listener listens to query changes
     */
    fun setOnQueryTextListener(listener: OnQueryTextListener) {
        onQueryChangeListener = listener
    }

    /**
     * Set this listener to listen to search open and close events
     *
     * @param listener listens to HanapView opening, closing, and the animations end
     */
    fun setOnSearchViewListener(listener: SearchViewListener) {
        searchViewListener = listener
    }

    internal class SavedState : View.BaseSavedState {
        var query: String? = null
        var isSearchOpen: Boolean = false
        var animationDuration: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(`in`: Parcel) : super(`in`) {
            this.query = `in`.readString()
            this.isSearchOpen = `in`.readInt() == 1
            this.animationDuration = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(query)
            out.writeInt(if (isSearchOpen) 1 else 0)
            out.writeInt(animationDuration)
        }

    }


    interface OnQueryTextListener {

        /**
         * @param query the query text
         * @return true to override the default action
         */
        fun onQueryTextSubmit(query: String): Boolean

        /**
         * @param newText the query text
         * @return true to override the default action
         */
        fun onQueryTextChange(newText: String): Boolean

        /**
         * Called when the query text is cleared by the user.
         *
         * @return true to override the default action
         */
        fun onQueryTextCleared(): Boolean
    }


    interface SearchViewListener {

        /**
         * Called instantly when the search opens
         */
        fun onSearchViewShown()

        /**
         * Called instantly when the search closes
         */
        fun onSearchViewClosed()

        /**
         * Called at the end of the show animation
         */
        fun onSearchViewShownAnimation()

        /**
         * Called at the end of the close animation
         */
        fun onSearchViewClosedAnimation()
    }

    companion object {
        val CARD_CORNER_RADIUS = 4
        val ANIMATION_CENTER_PADDING = 26
        val CARD_PADDING = 6
        val CARD_ELEVATION = 2
        val BACK_ICON_ALPHA_DEFAULT = 0.87f
        val ICONS_ALPHA_DEFAULT = 0.54f

        const val STYLE_BAR = 1
        const val STYLE_CARD = 2
    }
}