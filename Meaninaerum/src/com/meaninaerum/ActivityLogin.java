package com.meaninaerum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		new DictsLoaderTask().execute();
		findViewById(R.id.btn_log_in).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_password = (EditText) findViewById(R.id.et_password);
				String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
				String password = et_password.getText().toString();
				if (name.isEmpty() || password.isEmpty()) {
					Toast.makeText(ActivityLogin.this, R.string.name_and_pass_required, Toast.LENGTH_SHORT).show();
					return;
				}
				et_password.setText("");
				long userId = DBHelper.getDBHelper(ActivityLogin.this).checkUser(name, password);
				if (userId >= 0) {
					Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
					intent.putExtra(ActivityMain.EXTRA_USER_ID, userId);
					startActivity(intent);
				} else {
					DialogManager.showErrorDialog(ActivityLogin.this, getString(R.string.wrong_password));
				}
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		DBHelper.getDBHelper(this).close();
		super.onDestroy();
	}

	private class DictsLoaderTask extends AsyncTask<Void, Void, Void> {

		ProgressDialog pd = new ProgressDialog(ActivityLogin.this);
		
		@Override
		protected void onPreExecute() {
			pd.setCancelable(false);
			pd.setTitle(R.string.loading_dictionaries);
			pd.setMessage(getString(R.string.please_wait));
			pd.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			DBHelper.getDBHelper(ActivityLogin.this).checkAndLoadDictsIfNeeded();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
		}
		
	}

}
