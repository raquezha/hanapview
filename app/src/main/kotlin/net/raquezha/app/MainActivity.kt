package net.raquezha.app


import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import net.raquezha.hanapview.utils.DimensUtils

class MainActivity : AppCompatActivity() {
    val EXTRA_REVEAL_CENTER_PADDING = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        setupSearchView(menu)
        return true
    }

    private fun setupSearchView(menu: Menu) {
        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)
        searchView.tabLayout = tabLayout

//        // Adding padding to the animation because of the hidden menu item
//        val revealCenter = searchView.revealAnimationCenter!!
//        revealCenter.x -= DimensUtils.convertDpToPx(EXTRA_REVEAL_CENTER_PADDING, this)
    }

    override fun onBackPressed() {
        if (searchView.onBackPressed()) {
            return
        }

        super.onBackPressed()
    }
}
