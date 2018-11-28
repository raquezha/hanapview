package net.raquezha.hanapview

import com.google.android.material.tabs.TabLayout

abstract class OnTabSelectedListener : TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab) {
        // No action
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        // No action
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        // No action
    }
}