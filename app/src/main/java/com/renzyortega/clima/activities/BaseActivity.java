package com.renzyortega.clima.activities;

import android.os.Bundle;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.Toolbar;

import android.content.DialogInterface;

import android.view.MenuItem;

import com.renzyortega.clima.R;
import com.renzyortega.clima.dialog.AboutDialog;

public class BaseActivity extends AppCompatActivity {

	private final String TAG = "BaseActivity";
	private Toolbar toolBar;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;

	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
    public void setContentView(@LayoutRes int layoutResID) {
		super.setContentView(layoutResID);
		AppBarLayout appBar = (AppBarLayout)findViewById(R.id.appBar);
		appBar.bringToFront();
		getToolbar();
	}

	@Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
	}
	
	@Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
		}
		else {
            super.onBackPressed();
        }
	}
	
	protected Toolbar getToolbar() {
		if(toolBar == null) {
			toolBar = (Toolbar)findViewById(R.id.toolbar);
			if(toolBar != null) {
				setSupportActionBar(toolBar);
				getSupportActionBar().setDisplayShowTitleEnabled(false);
			}
		}
		return(toolBar);
	}

	private void setupNavDrawer() {
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		if(drawerLayout == null) {
			return;
		}
		toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
		toggle.syncState();
		setupNavigationView();
	}

	private void setupNavigationView() {
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationItemListener);
	}

	private NavigationView.OnNavigationItemSelectedListener navigationItemListener = new NavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.nav_daily) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(BaseActivity.this);
				dialog.setTitle("Daily Forecasts");
				dialog.setMessage("Under Construction");
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
				dialog.show();
			}
			else if (id == R.id.nav_about) {
				AboutDialog dialog = AboutDialog.getInstance();
				dialog.show(getFragmentManager(), "aboutDonationDialog");
			}
			drawerLayout.closeDrawer(GravityCompat.START);
			return true;
		}
	};
}