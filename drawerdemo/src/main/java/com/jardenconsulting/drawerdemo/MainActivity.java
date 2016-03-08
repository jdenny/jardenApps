package com.jardenconsulting.drawerdemo;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    private TextView textView;
    private String[] dayTitles = {
            "lunes", "martes", "miércoles", "jueves", "viernes", "Exit"
    };
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private CharSequence drawerTitle;
    private CharSequence title;
    private LunesFragment lunesFragment;
    private MartesFragment martesFragment;
    private MiercolesFragment miercolesFragment;
    private String currentTag;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(state is " + (savedInstanceState == null ? "" : "not ") + "null)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = drawerTitle = getTitle();

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        this.textView = (TextView) findViewById(R.id.textView);
        String spanishPhrase = "aá eé ií oó uú nñ ¡qué! ¿cómo?";
        textView.setText(spanishPhrase);
        this.fragmentManager = getSupportFragmentManager();
        this.lunesFragment = (LunesFragment) fragmentManager.findFragmentById(R.id.lunesFragment);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.hide(this.lunesFragment);
        ft.commit();

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.drawerListView = (ListView) findViewById(R.id.left_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        this.drawerListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, dayTitles));
        // Set the list's click listener
        this.drawerListView.setOnItemClickListener(this);

    }

    @Override // OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(position=" + position + ")");
        drawerListView.setItemChecked(position, true);
        this.drawerTitle = dayTitles[position];
        String tag = this.drawerTitle.toString();
        showFragment(tag);
        drawerLayout.closeDrawer(drawerListView);
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
        if (this.currentTag == null) {
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
}
