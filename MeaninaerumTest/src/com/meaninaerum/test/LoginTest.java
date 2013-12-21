package com.meaninaerum.test;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import com.meaninaerum.ActivityLogin;
import com.meaninaerum.DBHelper;

public class LoginTest extends android.test.ActivityInstrumentationTestCase2<ActivityLogin> {
	
	private static final String NAME = "test_name1";
	private static final String PASS = "test_pass1";

	private Activity activity;
	private EditText et_name;
	private EditText et_password;
	private SQLiteDatabase db;
	private long id = -1;
	
	public LoginTest() {
		super(ActivityLogin.class);
	}

	@Override
	protected void setUp() throws Exception {
 		super.setUp();
		activity = getActivity();
		et_name = (EditText) activity.findViewById(com.meaninaerum.R.id.et_name);
		et_password = (EditText) activity.findViewById(com.meaninaerum.R.id.et_password);
		db = DBHelper.getDBHelper(activity).getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("users_name", NAME);
		cv.put("users_password", PASS);
		id = db.insert("users", null, cv);
	}
	
	public void testActivityCreated() {
	    assertNotNull(activity);
	    assertNotNull(et_name);
	    assertNotNull(et_password);
	}
	
	public void testFieldsEmpty() {
		assertEquals("", et_name.getText().toString());
		assertEquals("", et_password.getText().toString());
	}
	
	public void testDatabaseCreated() {
		assertNotNull(db);
	}
	
	public void testCheckPassword() {
		long result = DBHelper.getDBHelper(activity).checkUser(NAME, PASS + ".");
		assertTrue(result == -1);
		result = DBHelper.getDBHelper(activity).checkUser(NAME, PASS);
		assertTrue(result != -1);
	}

	@Override
	protected void tearDown() throws Exception {
		db.delete("users", "users_id = " + id, null);
		db.close();
		super.tearDown();
	}

}
