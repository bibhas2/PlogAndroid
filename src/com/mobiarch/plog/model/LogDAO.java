package com.mobiarch.plog.model;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;

import com.mobiarch.plog.util.Logger;


public class LogDAO {
	LogDataReceiver receiver;
	Context context;
	
	public LogDAO(LogDataReceiver receiver, Context ctx) {
		super();
		this.receiver = receiver;
		this.context = ctx;
	}

	public void getLogListAsync() {
		AsyncTask<Void, Void, ArrayList<LogEntry>> task = new AsyncTask<Void, Void, ArrayList<LogEntry>> () {
			@Override
			protected ArrayList<LogEntry> doInBackground(Void... params) {
				return getLogList();
			}
			@Override
			protected void onPostExecute(ArrayList<LogEntry> result) {
				super.onPostExecute(result);
				
				receiver.onLogListAvailable(result);
			}
		};
		task.execute();
	}

	private ArrayList<LogEntry> getLogList() {
		ArrayList<LogEntry> list = new ArrayList<LogEntry>();
		SQLiteDatabase db = null;
		
		try {
			db = DBHelper.getConnection(context);
			Cursor c = db.rawQuery(
					"select _id, createdOn, content from LogEntry, LogEntryText where LogEntry._id=LogEntryText.docid order by LogEntry.createdOn", null);
			c.moveToFirst();
			while (!c.isAfterLast()) {
				LogEntry l = new LogEntry();
				
				l.setLogId(c.getInt(0));
				l.setCreatedOn(new Date(c.getLong(1)));
				l.setText(c.getString(2));
				
				list.add(l);
				
				c.moveToNext();
			}
			
			c.close();
		} catch (Exception e) {
			Logger.error("Failed to get log list.", e);
			receiver.onError("Failed to get log list.");
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return list;
	}

	public void addLogEntry(LogEntry e) {
		AsyncTask<LogEntry, Void, Void> task = new AsyncTask<LogEntry, Void, Void> () {
			@Override
			protected Void doInBackground(LogEntry... params) {
				SQLiteDatabase db = null;
				LogEntry e = params[0];
				
				try {
					db = DBHelper.getConnection(context);
					db.beginTransaction();
					
					SQLiteStatement stmt = db.compileStatement(
						    "insert into LogEntry (createdOn) values (?)");
					e.setCreatedOn(new Date());
					stmt.bindLong(1, e.getCreatedOn().getTime());
					e.setLogId(stmt.executeInsert());
					Logger.info("Added log entry with ID: %d", e.getLogId());
					
					stmt = db.compileStatement(
							"insert into LogEntryText (docid, content) values (?, ?)");
					stmt.bindLong(1, e.getLogId());
					stmt.bindString(2, e.getText());
					stmt.executeInsert();
					
					db.setTransactionSuccessful();
				} catch (Exception ex) {
					Logger.error("Failed to add log entry.", ex);
					receiver.onError("Failed to add log entry.");
				} finally {
					if (db != null) {
						db.endTransaction();
						db.close();
					}
				}

				return null;
			}
			
		};
		task.execute(e);
	}
	
	public void deleteLogEntry(LogEntry e) {
		AsyncTask<LogEntry, Void, Void> task = new AsyncTask<LogEntry, Void, Void> () {
			@Override
			protected Void doInBackground(LogEntry... params) {
				SQLiteDatabase db = null;
				LogEntry e = params[0];

				Logger.info("Deleting log entry with ID: %d", e.getLogId());
				
				try {
					db = DBHelper.getConnection(context);
					db.beginTransaction();
					
					SQLiteStatement stmt = db.compileStatement(
						    "delete from LogEntry where _id=?");
					stmt.bindLong(1, e.getLogId());
					stmt.executeUpdateDelete();
					
					stmt = db.compileStatement(
						    "delete from LogEntryText where docid=?");
					stmt.bindLong(1, e.getLogId());
					stmt.executeUpdateDelete();
					
					db.setTransactionSuccessful();
				} catch (Exception ex) {
					Logger.error("Failed to delete log entry.", ex);
					receiver.onError("Failed to delete log entry.");
				} finally {
					if (db != null) {
						db.endTransaction();
						db.close();
					}
				}

				return null;
			}
			
		};
		task.execute(e);
	}
}
