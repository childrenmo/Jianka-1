package tech.jianka.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import tech.jianka.adapter.HomeFragmentPagerAdapter;
import tech.jianka.fragment.FragmentManager;
import tech.jianka.fragment.GroupFragment;
import tech.jianka.fragment.RecentFragment;
import tech.jianka.fragment.TaskFragment;
import tech.jianka.utils.PreferenceHelper;

import static tech.jianka.fragment.FragmentManager.fragmentList;
import static tech.jianka.utils.ItemUtils.getSDCardPath;
import static tech.jianka.utils.PreferenceHelper.getBoolean;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ViewPager.OnAdapterChangeListener, GroupFragment.OnFragmentInteractionListener {
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final Context context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                switch (viewPager.getCurrentItem()){
                    case FragmentManager.GROUP_FRAGMENT:
                        intent.setClass(context, NewCardGroupActivity.class);
                        break;
                    case FragmentManager.RECENT_FRAGMENT:
                         intent = new Intent(context, NewCardActivity.class);
                        break;
                    case FragmentManager.TASK_FRAGMENT:
                        intent = new Intent(context, NewCardActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // TODO: 2017/7/29 长按直接解析粘贴板内容
                return true;
            }
        });

        checkFirstLaunch();

        initGroup();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
    }

    private void checkFirstLaunch() {
        boolean flag = getSharedPreferences("tech.jianka",MODE_PRIVATE).getBoolean("isFirstRun",true);
        if(flag){
            String[] groups = getResources().getStringArray(R.array.default_group_path);
            for (String group : groups) {
                new File(getSDCardPath(group)).mkdirs();
            }
            getSharedPreferences("tech.jianka", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
        }
    }

    private void initGroup() {
        Boolean defaultGroupReady = getBoolean("pref_group", "DEFAULT_GROUP_READY", this);
        if(defaultGroupReady){
            String[] groups = getResources().getStringArray(R.array.default_group);
            for (String group : groups) {
                PreferenceHelper.putString("pref_group",group,group,this);
            }
            PreferenceHelper.putBoolean("pref_group","DEFAULT_GROUP_READY",true,this);
        }
    }

    private void initView() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        String[] titles = getResources().getStringArray(R.array.main_tab_titles);

        fragmentList.add(GroupFragment.newInstance(FragmentManager.GROUP_FRAGMENT));
        fragmentList.add(RecentFragment.newInstance(FragmentManager.RECENT_FRAGMENT));
        fragmentList.add(TaskFragment.newInstance(FragmentManager.TASK_FRAGMENT));
        //fragment的Adapter
        HomeFragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(getSupportFragmentManager(), titles, fragmentList);
        //viewpager的设置
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnAdapterChangeListener(this);
        viewPager.setCurrentItem(FragmentManager.RECENT_FRAGMENT,false);//从中间页启动
        //tab的设置
        tabLayout.setupWithViewPager(viewPager);//tab和viewpager联动
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            navSettings();
            return true;
        } else if (id == R.id.action_new_card_list) {
            navCreateCardList();
            return true;
        } else if (id == R.id.action_show_hidden) {
            showHiddenCardList();
            return true;
        } else if (id == R.id.action_search) {
            searchCard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchCard() {
        Toast.makeText(this, "还没有实现,敬请期待", Toast.LENGTH_SHORT);
        // TODO: 2017/7/22
    }

    private void showHiddenCardList() {
        Toast.makeText(this, "还没有实现,敬请期待", Toast.LENGTH_SHORT);
        // TODO: 2017/7/22
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_friend:
                navFriendList();
                break;
            case R.id.nav_message:
                navMessage();
                break;
            case R.id.nav_arrange:
                navArrange();
                break;
            case R.id.nav_create_cardlist:
                navCreateCardList();
                break;
            case R.id.nav_add_folder:
                navAddFolder();
                break;
            case R.id.nav_recyclebin:
                navRecycleBin();
                break;
            case R.id.nav_settings:
                navSettings();

                break;
            case R.id.nav_share:
                navShare();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navShare() {

    }

    private void navSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void navRecycleBin() {

    }

    private void navAddFolder() {

    }


    private void navCreateCardList() {
        startActivity(new Intent(this, NewCardGroupActivity.class));
    }

    private void navArrange() {

    }

    private void navMessage() {
        startActivity(new Intent(this, MessageActivity.class));
    }

    private void navFriendList() {
        startActivity(new Intent(this, FriendListActivity.class));
    }

    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
