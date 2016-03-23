package com.jardenconsulting.drawerdemo;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

// TODO: see http://blog.teamtreehouse.com/add-navigation-drawer-android
public class MainActivity extends AppCompatActivity
        implements /*!!AdapterView.OnItemClickListener*/
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String VIEWLESS = "VIEWLESS";
    private TextView textView;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private LunesFragment lunesFragment;
    private MartesFragment martesFragment;
    private MiercolesFragment miercolesFragment;
    private ViewlessFragment viewlessFragment;
    private String currentTag;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private boolean doubleBackToExitPressedOnce = false;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(" + (savedInstanceState == null ? "" : "not ") + "null)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //!! title = drawerTitle = getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.actionBar = getSupportActionBar();
        /*!!
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.actionBar.setHomeButtonEnabled(true);
        */

        this.textView = (TextView) findViewById(R.id.textView);
        String spanishPhrase = "aá eé ií oó uú nñ ¡qué! ¿cómo?";
        textView.setText(spanishPhrase);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        /*!! this.drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        this.drawerListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dayTitles));
        // Set the list's click listener
        this.drawerListView.setOnItemClickListener(this);
         */
        this.drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.fragmentManager = getSupportFragmentManager();
        this.lunesFragment = (LunesFragment) fragmentManager.findFragmentById(
                R.id.lunesFragment);
        if (savedInstanceState == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.hide(this.lunesFragment);
            this.viewlessFragment = new ViewlessFragment();
            ft.add(this.viewlessFragment, VIEWLESS);
            ft.commit();
        } else {
            this.viewlessFragment = (ViewlessFragment) fragmentManager
                    .findFragmentByTag(VIEWLESS);
        }
    }

    /*!!
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected(menuItem.id=" + id + ")");
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    /*!!
    // Called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = this.drawerLayout.isDrawerOpen(drawerListView);
        Log.d(TAG, "onPrepareOptionsMenu(drawerOpen=" + drawerOpen + ")");
        Snackbar.make(textView, "drawerOpen=" + drawerOpen, Snackbar.LENGTH_INDEFINITE).show();
        return super.onPrepareOptionsMenu(menu);
    }
    */

    /*!!
    @Override // OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(position=" + position + ")");
        drawerListView.setItemChecked(position, true);
        this.drawerTitle = dayTitles[position];
        String tag = this.drawerTitle.toString();
        showFragment(tag);
        drawerLayout.closeDrawer(drawerListView);
    }
    */
    @Override // OnNavigationItemSelectedListener
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected(menuItem.id=" + id + ")");
        String tag;
        if (id == R.id.qaStyle) {
            tag = "lunes";
        } else if (id == R.id.topic) {
            tag = "martes";
        } else if (id == R.id.wordLookup) {
            tag = "miércoles";
        } else if (id == R.id.numbersGame) {
            tag = "jueves";
        } else if (id == R.id.qByLevel) {
            tag = "viernes";
        } else if (id == R.id.help) {
            tag = "sábado";
        } else if (id == R.id.exit) {
            super.onBackPressed();
            return true;
        } else {
            throw new IllegalStateException("unrecognised id: " + id);
        }
        showFragment(tag);

        //!! DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(String tag) {
        Log.d(TAG, "showFragment(tag=" + tag + ")");
        if (tag.equals(this.currentTag)) {
            Log.d(TAG, "fragment already showing: " + tag);
            return;
        }
        if (tag.equals("lunes")) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.show(this.lunesFragment);
            if (this.currentFragment != null) ft.remove(this.currentFragment);
            ft.commit();
            currentFragment = lunesFragment;
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
            transaction.hide(this.lunesFragment);
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
            if (this.currentTag.equals("lunes")) {
                transaction.hide(this.lunesFragment);
            } else {
                transaction.remove(this.currentFragment);
            }
            transaction.commit();
            this.currentTag = null;
            setTitle(this.title);
            this.textView.setText("back to MainActivity!");
        }
    }

    /*!!
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate(" + savedInstanceState == null ? "" : "not " + "null)");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerToggle != null) drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged(" + newConfig + ")");
        drawerToggle.onConfigurationChanged(newConfig);
    }
    */
}
