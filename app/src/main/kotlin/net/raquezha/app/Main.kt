package net.raquezha.app

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import net.raquezha.hanapview.HanapView

class Main : AppCompatActivity() {
    private var searchView: HanapView? = null
    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchView = findViewById(R.id.searchView)
        tabLayout = findViewById(R.id.tabLayout)

        //        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //        ViewPager viewPager = findViewById(R.id.container);
        //        viewPager.setAdapter(sectionsPagerAdapter);
        //
        //        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        val statusBarHeight = getStatusBarHeight(this@Main)
        //log("statusBartHeight: $statusBarHeight")
        val params = toolbar.layoutParams as FrameLayout.LayoutParams
        params.setMargins(0, statusBarHeight, 0, 0)

        val params2 = searchView?.layoutParams as FrameLayout.LayoutParams
        params.setMargins(0, statusBarHeight, 0, 0)
        toolbar.layoutParams = params
        searchView?.layoutParams = params2
    }

    private fun getStatusBarHeight(c: Context): Int {
        val resourceId = c.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            c.resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        searchView!!.setMenuItem(item)
        //        searchView.setTabLayout(tabLayout);
    }

    override fun onBackPressed() {
        if (searchView!!.onBackPressed()) {
            return
        }

        super.onBackPressed()
    }

    companion object {
        val EXTRA_REVEAL_CENTER_PADDING = 40
    }
}
