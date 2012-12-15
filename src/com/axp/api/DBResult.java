package com.axp.api;

import android.database.Cursor;
import android.os.Bundle;

public class DBResult {
	public static final int RESULT_FAIL = 0;
	public static final int RESULT_OK = 1;
	
	private Cursor cursor;
	
	private int result;
	
	private long insert_id;
	
	//@debug
	public Cursor getCursor() {
		return cursor;
	}
	
	public void close() {
		try {
			cursor.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public DBResult(Cursor c) {
		cursor = c;
	}
	
	public DBResult(int result_status) {
		result = result_status;
	}
	
	public DBResult(int result_status, long insert_id) {
		result = result_status;
		this.insert_id = insert_id;
	}
	
	public int getNumRows() {
		try {
			return cursor == null ? 0 : cursor.getCount();
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	
	public long getInsertId() {
		return insert_id;
	}
	
	public int getResultStatus() {
		return this.result;
	}

	public boolean moveToNext() {
		try {
			return cursor.moveToNext();
		} catch (NullPointerException ex) {
			return false;
		}
	}
	
	public Bundle getExtras() {
		return cursor.getExtras();
	}

	public int getInt(String cname) {
		return cursor.getInt(cursor.getColumnIndex(cname));
	}
	
	public long getLong(String cname) {
		return cursor.getLong(cursor.getColumnIndex(cname));
	}
	
	public double getDouble(String cname) {
		return cursor.getDouble(cursor.getColumnIndex(cname));
	}
	
	public byte[] getBlob(String cname) {
		return cursor.getBlob(cursor.getColumnIndex(cname));
	}

	public String getString(String cname) {
		try {
			return cursor.getString(cursor.getColumnIndex(cname));
		} catch (Exception ex) {
			return null;
		}
	}
}
