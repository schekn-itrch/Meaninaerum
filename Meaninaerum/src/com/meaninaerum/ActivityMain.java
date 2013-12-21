package com.meaninaerum;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityMain extends Activity {
	
	public static final String EXTRA_USER_ID = "user_id";
	private static final int MENU_STATS = 0;
	
	private long userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		userId = getIntent().getLongExtra(EXTRA_USER_ID, 0);
		Bundle args = new Bundle();
		args.putLong(EXTRA_USER_ID, userId);
		Fragment fragment = Fragment.instantiate(this, FragmentDicts.class.getName(), args);
		getFragmentManager().beginTransaction().add(R.id.fl_container, fragment).commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_STATS, Menu.NONE, R.string.statistics)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case MENU_STATS:
			DialogManager.showStatsDialog(this, userId);
		}
		return true;
	}	
}
