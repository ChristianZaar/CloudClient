package com.asome.cloudclient;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Slide;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private RelativeLayout mLayout;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private final String TAG = "MenuActivity";
    public static final String SESSION = "menuactivity.session";
    public static final String START_WITH_ALARMS = "menuactivity.start_alarms";
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, MenuActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.activity_menu);
        configureToolbar();
        configureNavigationDrawer();
        Fragment f = new WelcomeFragment();
        f.setEnterTransition(new Slide(Gravity.RIGHT));
        f.setExitTransition(new Slide(Gravity.LEFT));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack(TAG);
        transaction.replace(R.id.frame, f);
        transaction.commit();
        if(savedInstanceState != null) {
            //Something future perhaps
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelable();
    }

    private void configureToolbar() {
        mToolbar = findViewById(R.id.menuToolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }


    private void configureNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            // Android home
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.toggleDarkMode:
                SharedPreferences prefs = this.getSharedPreferences(
                        MyApplication.TAG, Context.MODE_PRIVATE);
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefs.edit().putInt(MyApplication.NightModeTag, AppCompatDelegate.MODE_NIGHT_NO).apply();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefs.edit().putInt(MyApplication.NightModeTag, AppCompatDelegate.MODE_NIGHT_YES).apply();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment f = null;
        FragmentManager fm = getSupportFragmentManager();
        String tag = "";

        switch (menuItem.getItemId()){
            case R.id.nav_placeholder:
                tag = PlaceHolderFragment.TAG;
                f = fm.findFragmentByTag(PlaceHolderFragment.TAG);
                if (f == null)
                    f = new PlaceHolderFragment();
                break;
            case R.id.nav_about:
                tag = AboutFragment.TAG;
                f = fm.findFragmentByTag(AboutFragment.TAG);
                if (f == null)
                    f = new AboutFragment();
                break;
            case R.id.nav_help:
                tag = HelpFragment.TAG;
                f = fm.findFragmentByTag(HelpFragment.TAG);
                if (f == null)
                    f = new HelpFragment();
                break;
            case R.id.nav_logout:
                Intent intent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
                //Clearing stack
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                MyApplication.getAppContext()
//                        .getSharedPreferences(MyApplication.TAG, Context.MODE_PRIVATE)
//                        .edit().putString(MyApplication.Session, "").apply();
                startActivity(intent);
                f = null;//Not implemented
                break;
            case R.id.nav_settings:
                tag = SettingsFragment.TAG;
                f = fm.findFragmentByTag(SettingsFragment.TAG);
                if (f == null)
                    f = new SettingsFragment();
                break;
        }

        if (f != null) {
            f.setEnterTransition(new Slide(Gravity.RIGHT));
            f.setExitTransition(new Slide(Gravity.LEFT));
            FragmentTransaction transaction = fm.beginTransaction().addToBackStack(tag);
            transaction.replace(R.id.frame, f,tag);
            transaction.commit();
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }
}
