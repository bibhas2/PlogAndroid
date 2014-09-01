package com.mobiarch.plog.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	static int THIS_VERSION = 1;
	private static final String DB_NAME = "logdb";
	
	public DBHelper(Context ctx) {
	    super(ctx, DB_NAME, null, THIS_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql;
		
		sql = "create table LogEntry (_id INTEGER PRIMARY KEY AUTOINCREMENT, createdOn INTEGER NOT NULL)";
	    db.execSQL(sql);
	    
		sql = "CREATE VIRTUAL TABLE LogEntryText USING fts4 (content TEXT)";
	    db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	/**
	 * This is a short circuit method that does not attempt to create or upgrade schema. This method should be called
	 * when you are sure that schema is already created. This method will perform faster than getWritableDatabase() since
	 * there is no check performed to see if schema needs to be created or upgraded.
	 * 
	 * @param ctx - Context
	 * 
	 * @return The database connection.
	 */
	public static SQLiteDatabase getConnection(Context ctx) {
		return ctx.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
	}
}
