package net.raquezha.app;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import net.raquezha.hanapview.HanapView;

public class Main extends AppCompatActivity {
    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;
    private HanapView searchView;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.searchView);
        tabLayout = findViewById(R.id.tabLayout);

//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        ViewPager viewPager = findViewById(R.id.container);
//        viewPager.setAdapter(sectionsPagerAdapter);
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
//        searchView.setTabLayout(tabLayout);
    }

    @Override
    public void onBackPressed() {
        if (searchView.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }
}
