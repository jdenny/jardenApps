package com.jardenconsulting.drawerdemo;

import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import jarden.engspa.EngSpa;
import jarden.engspa.EngSpaSQLite2;

// TODO: see http://blog.teamtreehouse.com/add-navigation-drawer-android
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String VIEWLESS = "VIEWLESS";
    private TextView textView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence title;
    private HelpFragment helpFragment;
    private MartesFragment martesFragment;
    private MiercolesFragment miercolesFragment;
    private ViewlessFragment viewlessFragment;
    private String currentTag;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private boolean doubleBackToExitPressedOnce = false;
    private EngSpaSQLite2 engSpaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(" + (savedInstanceState == null ? "" : "not ") + "null)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.textView = (TextView) findViewById(R.id.textView);
        String spanishPhrase = "aá eé ií oó uú nñ ¡qué! ¿cómo?";
        textView.setText(spanishPhrase);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        this.drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.fragmentManager = getSupportFragmentManager();
        this.helpFragment = (HelpFragment) fragmentManager.findFragmentById(
                R.id.helpFragment);
        if (savedInstanceState == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.hide(this.helpFragment);
            this.viewlessFragment = new ViewlessFragment();
            ft.add(this.viewlessFragment, VIEWLESS);
            ft.commit();
        } else {
            this.viewlessFragment = (ViewlessFragment) fragmentManager
                    .findFragmentByTag(VIEWLESS);
        }
    }
    @Override // OnNavigationItemSelectedListener
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected(menuItem.id=" + id + ")");
        String tag;
        if (id == R.id.qaStyle) {
            tag = "help";
        } else if (id == R.id.topicMode) {
            tag = "martes";
        } else if (id == R.id.wordLookup) {
            tag = "miércoles";
        } else if (id == R.id.numbersGame) {
            tag = "jueves";
        } else if (id == R.id.learnMode) {
            tag = "viernes";
        } else if (id == R.id.exit) {
            super.onBackPressed();
            return true;
        } else {
            throw new IllegalStateException("unrecognised id: " + id);
        }
        showFragment(tag);

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(String tag) {
        Log.d(TAG, "showFragment(tag=" + tag + ")");
        if (tag.equals(this.currentTag)) {
            Log.d(TAG, "fragment already showing: " + tag);
            return;
        }
        if (tag.equals("help")) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.show(this.helpFragment);
            if (this.currentFragment != null) ft.remove(this.currentFragment);
            ft.commit();
            currentFragment = helpFragment;
            this.currentTag = tag;
        } else {
            if (tag.equals("martes")) {
                if (this.martesFragment == null) {
                    this.martesFragment = new MartesFragment();
                }
                this.currentFragment = martesFragment;
            } else if (tag.equals("miércoles")) {
                if (this.miercolesFragment == null) {
                    this.miercolesFragment = new MiercolesFragment();
                }
                this.currentFragment = miercolesFragment;
            } else if (tag.equals("Exit")) {
                super.onBackPressed();
                return;
            } else {
                String message = "we haven't yet written fragment " + tag + "!";
                this.textView.setText(message);
                return;
            }
            this.currentTag = tag;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(this.helpFragment);
            transaction.replace(R.id.fragmentLayout, currentFragment, tag);
            transaction.commit();
        }
        setTitle(this.currentTag);
        this.textView.setText("should be showing fragment " + this.currentTag);
    }

    /**
     * if showing any fragment, hide or close it, thus showing activity
     * otherwise close app, after user clicks back within 2 seconds.
     * User can also exit the app using the Exit option on the drawer
     */
    @Override // Activity
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed(); currentTag=" + this.currentTag);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (this.currentTag == null) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(this.textView, "Please click BACK again to exit",
                    Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            FragmentTransaction transaction = this.fragmentManager.beginTransaction();
            if (this.currentTag.equals("help")) {
                transaction.hide(this.helpFragment);
            } else {
                transaction.remove(this.currentFragment);
            }
            transaction.commit();
            this.currentTag = null;
            setTitle(this.title);
            this.textView.setText("back to MainActivity!");
        }
    }
    @Override // Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override // Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (BuildConfig.DEBUG) Log.d(TAG,
                "onOptionsItemSelected(itemId=" + id + ")");
        if (id == R.id.findDuplicates) {
            findDuplicates();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void findDuplicates() {
        if (this.engSpaDAO == null) {
            this.engSpaDAO = EngSpaSQLite2.getInstance(getApplicationContext());
        }
        // for each word, using word id
        // find words by English
        // find words by Spanish
        int dbSize = this.engSpaDAO.getDictionarySize();
        if (dbSize == 0) dbSize = 100;
        Log.i(TAG, "findDuplicates(); dbSize=" + dbSize);
        EngSpa engSpa;
        String english, spanish;
        List<EngSpa> englishList, spanishList;
        for (int id = 0; id < dbSize; id++) {
            engSpa = this.engSpaDAO.getWordById(id);
            english = engSpa.getEnglish();
            englishList = this.engSpaDAO.getEnglishWord(english);
            if (englishList.size() > 1) {
                Log.i(TAG, "duplicates for " + english);
                for (EngSpa es: englishList) {
                    Log.i(TAG, "  " + es);
                }
            }
            spanish = engSpa.getEnglish();
            spanishList = this.engSpaDAO.getEnglishWord(spanish);
            if (spanishList.size() > 1) {
                Log.i(TAG, "duplicates for " + spanish);
                for (EngSpa es: spanishList) {
                    Log.i(TAG, "  " + es);
                }
            }
        }
        Log.i(TAG, "end of findDuplicates()");
    }

}
