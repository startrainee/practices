package com.stardust.practices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PagesActivity extends AppCompatActivity {

    FragmentPagerAdapter fragmentPagerAdapter;
    List<PageModel> pageModel = new ArrayList<>();
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);
        initView();
    }

    private void initView() {
        pageModel.add(new PageModel(R.string.page_0,R.layout.fragment_page_0));
        pageModel.add(new PageModel(R.string.page_1,R.layout.fragment_page_1));
        pageModel.add(new PageModel(R.string.page_2,R.layout.fragment_page_2));

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                PageModel model = pageModel.get(position);
                return PageFragment.newInstance(model.titleId, model.layoutId);
            }
            @Override
            public int getCount() {
                return pageModel.size();
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return getString(pageModel.get(position).titleId);
            }
        };
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    class PageModel {

        PageModel(int titleId, int layoutId) {
            this.titleId = titleId;
            this.layoutId = layoutId;
        }

        int titleId;
        int layoutId;

    }
}


