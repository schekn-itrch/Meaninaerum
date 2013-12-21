package com.meaninaerum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String NAME = "db_nastia_skinhed";
	private static final byte VERSION = 1;
	
	private static final String TABLE_USERS = "users";
	private static final String TABLE_DICTS = "dicts";
	private static final String TABLE_WORDS = "words";
	private static final String TABLE_STATS = "stats";
	
	private static final String USERS_ID = "users_id";
	private static final String USERS_NAME = "users_name";
	private static final String USERS_PASSWORD = "users_password";
	
	private static final String DICTS_ID = "dicts_id";
	private static final String DICTS_NAME = "dicts_name";

	private static final String WORDS_ID = "words_id";
	private static final String WORDS_DICTS_ID = "words_dicts_id";
	private static final String WORDS_NAME = "words_name";
	private static final String WORDS_OPTION_A = "words_option_a";
	private static final String WORDS_OPTION_B = "words_option_b";
	
	private static final String STATS_ID = "stats_id";
	private static final String STATS_USERS_ID = "stats_users_id";
	private static final String STATS_DICTS_ID = "status_dicts_id";
	private static final String STATS_SCORE = "score";
	
	private static volatile DBHelper instance;
	private Context context;
	
	public static DBHelper getDBHelper(Context context) {
		if (instance == null) {
			synchronized (DBHelper.class) {
				if (instance == null) {
					instance = new DBHelper(context);
					instance.context = context;
				}
			}
		}
		return instance;
	}

	private DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_USERS + " (" + USERS_ID + " INTEGER PRIMARY KEY , " + USERS_NAME + " TEXT NOT NULL, " + USERS_PASSWORD + " TEXT NOT NULL)");
		db.execSQL("CREATE TABLE " + TABLE_DICTS + " (" + DICTS_ID + " INTEGER PRIMARY KEY , " + DICTS_NAME + " TEXT NOT NULL)");
		db.execSQL("CREATE TABLE " + TABLE_WORDS + " (" + WORDS_ID + " INTEGER PRIMARY KEY , " + WORDS_DICTS_ID + " INTEGER NOT NULL, " + WORDS_NAME + " TEXT NOT NULL, " 
				+ WORDS_OPTION_A + " TEXT NOT NULL, " + WORDS_OPTION_B + " TEXT NOT NULL)");
		db.execSQL("CREATE TABLE " + TABLE_STATS + " (" + STATS_ID + " INTEGER PRIMARY KEY , " + STATS_USERS_ID + " INTEGER NOT NULL, " + STATS_DICTS_ID + " INTEGER NOT NULL, "
				+ STATS_SCORE + " INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
	}

	@Override
	public synchronized void close() {
		instance = null;
		super.close();
	}
	
	public long checkUser(String name, String password) {
		Cursor cursor = null;
		try {
			SQLiteDatabase db = getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + USERS_NAME + " = '" + name + "'", null);
			if (cursor.moveToFirst()) {
				int index_password = cursor.getColumnIndex(USERS_PASSWORD);
				if (password.equals(cursor.getString(index_password))) {
					int index_id = cursor.getColumnIndex(USERS_ID);
					return cursor.getLong(index_id);
				}
				return -1;
			} else {
				ContentValues values = new ContentValues();
				values.put(USERS_NAME, name);
				values.put(USERS_PASSWORD, password);
				return db.insert(TABLE_USERS, null, values);
			}
		} finally {
			cursor.close();
		}
	}
	
	public synchronized void checkAndLoadDictsIfNeeded() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + DICTS_ID + " FROM " + TABLE_DICTS, null);
		if (!cursor.moveToFirst()) {
			loadDict(R.raw.dict1, "Dictionary 1");
			loadDict(R.raw.dict2, "Dictionary 2");
		}
		cursor.close();
	}
	
	private void loadDict(int resId, String name) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues values1 = new ContentValues();
			values1.put(DICTS_NAME, name);
			long dictsId = db.insert(TABLE_DICTS, null, values1);
			BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(resId)));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split("//");
				ContentValues values2 = new ContentValues();
				values2.put(WORDS_DICTS_ID, dictsId);
				values2.put(WORDS_NAME, splitted[0]);
				values2.put(WORDS_OPTION_A, splitted[1]);
				values2.put(WORDS_OPTION_B, splitted[2]);
				db.insert(TABLE_WORDS, null, values2);
			}
			br.close();
			db.setTransactionSuccessful();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
	public List<Dictionary> getDictionaries() {
		SQLiteDatabase db = getReadableDatabase();
		List<Dictionary> result = new ArrayList<Dictionary>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DICTS, null);
		while (cursor.moveToNext()) {
			int index_id = cursor.getColumnIndex(DICTS_ID);
			int index_name = cursor.getColumnIndex(DICTS_NAME);
			result.add(new Dictionary(cursor.getLong(index_id), cursor.getString(index_name)));
		}
		cursor.close();
		return result;
	}
	
	public List<Word> getWords(long dictId) {
		SQLiteDatabase db = getReadableDatabase();
		List<Word> result = new ArrayList<Word>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_WORDS + " WHERE " + WORDS_DICTS_ID + " = " + dictId, null);
		while (cursor.moveToNext()) {
			int index_id = cursor.getColumnIndex(WORDS_ID);
			int index_name = cursor.getColumnIndex(WORDS_NAME);
			int index_option_a = cursor.getColumnIndex(WORDS_OPTION_A);
			int index_option_b = cursor.getColumnIndex(WORDS_OPTION_B);
			result.add(new Word(cursor.getLong(index_id), cursor.getString(index_name), cursor.getString(index_option_a), cursor.getString(index_option_b)));
		}
		cursor.close();
		return result;
	}
	
	public synchronized void addResultToStats(long userId, long dictId, int score) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(STATS_SCORE, score);
		int result = db.update(TABLE_STATS, values, STATS_DICTS_ID + " = " + dictId + " AND " + STATS_USERS_ID + " = " + userId, null);
		if (result == 0) {
			values.put(STATS_DICTS_ID, dictId);
			values.put(STATS_USERS_ID, userId);
			db.insert(TABLE_STATS, null, values);
		}
	}
	
	public String getLeaders(long dictId) {
		final int LIMIT = 3;
		StringBuilder sb = new StringBuilder();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + USERS_ID + ", " + USERS_NAME + ", " + STATS_USERS_ID + ", " + STATS_DICTS_ID + ", " + STATS_SCORE + " FROM " + TABLE_USERS
				+ " INNER JOIN " + TABLE_STATS + " ON " + USERS_ID + " = " + STATS_USERS_ID + " WHERE " + STATS_DICTS_ID + " = " + dictId + " ORDER BY " + STATS_SCORE 
				+ " DESC LIMIT " + LIMIT, null);
		for (int i = 1; i <= LIMIT; i++) {
			if (!cursor.moveToNext()) {
				break;
			}
			int index_name = cursor.getColumnIndex(USERS_NAME);
			int index_score = cursor.getColumnIndex(STATS_SCORE);
			sb.append(i + ". " + cursor.getString(index_name) + "  -  " + cursor.getString(index_score) + "\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		cursor.close();
		return sb.toString();
	}
	
	public String getStats(long userId) {
		StringBuilder sb = new StringBuilder();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + DICTS_ID + ", " + DICTS_NAME + ", " + STATS_DICTS_ID + ", " + STATS_USERS_ID + ", " + STATS_SCORE + " FROM " + TABLE_DICTS
				+ " INNER JOIN " + TABLE_STATS + " ON " + DICTS_ID + " = " + STATS_DICTS_ID + " WHERE " + STATS_USERS_ID + " = " + userId + " ORDER BY " + STATS_SCORE + " DESC", null);
		while (cursor.moveToNext()) {
			int index_name = cursor.getColumnIndex(DICTS_NAME);
			int index_score = cursor.getColumnIndex(STATS_SCORE);
			sb.append(cursor.getString(index_name) + "  -  " + cursor.getString(index_score) + "\n");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		} else {
			sb.append(context.getString(R.string.empty));
		}
		cursor.close();
		return sb.toString();
	}
	
}
