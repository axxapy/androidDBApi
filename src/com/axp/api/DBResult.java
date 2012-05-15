package com.axp.api;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.os.Bundle;

public class DBResult {
	public static final int RESULT_FAIL = 0;
	public static final int RESULT_OK = 1;
	
	private Cursor cursor;
	private boolean buffered = true;
	private ArrayList<HashMap<String, Object>> buffer = new ArrayList<HashMap<String, Object>>();
	private int current_pos = 0;
	
	private int result;
	
	private long insert_id;

	public DBResult(Cursor c) {
		this(c, true);
	}
	
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
	
	public DBResult(Cursor c, boolean buffered) {
		cursor = c;
		if (true) {
			this.buffered = false;
			return;
		}
		this.buffered = buffered;
		if (loadDataToBuffer()) {
			cursor.close();
		} else {
			this.buffered = false;
		}
	}
	
	public DBResult(int result_status) {
		result = result_status;
	}
	
	public DBResult(int result_status, long insert_id) {
		result = result_status;
		this.insert_id = insert_id;
	}
	
	private boolean loadDataToBuffer() {
		try {
		String[] names = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			HashMap<String, Object> values = new HashMap<String, Object>();
			for (int i=0; i<cursor.getColumnCount(); i++) {
				values.put(names[i], cursor.getString(i));
			}
		}
		return true;
		} catch (Exception ex) {
			return false;
		}
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
		if (!buffered) {
			return cursor.moveToNext();
		} else {
			if (current_pos + 1 >= buffer.size()) {
				current_pos = 0;
				return false;
			} else {
				current_pos ++;
				return true;
			}
		}
	}
	
	public Bundle getExtras() {
		return cursor.getExtras();
	}

	public int getInt(String cname) {
		if (buffered) {
			try {
				return Integer.valueOf((String)buffer.get(current_pos).get(cname));
			} catch (Exception ex) {
				return 0;
			}
		} else {
			return cursor.getInt(cursor.getColumnIndex(cname));
		}
	}
	
	public long getLong(String cname) {
		if (buffered) {
			try {
				return Long.valueOf((String)buffer.get(current_pos).get(cname));
			} catch (Exception ex) {
				return 0;
			}
		} else {
			return cursor.getLong(cursor.getColumnIndex(cname));
		}
	}
	
	public double getDouble(String cname) {
		if (buffered) {
			try {
				return Double.valueOf((String)buffer.get(current_pos).get(cname));
			} catch (Exception ex) {
				return 0;
			}
		} else {
			return cursor.getDouble(cursor.getColumnIndex(cname));
		}
	}
	
	public byte[] getBlob(String cname) {
		if (buffered) {
			try {
				return ((String)buffer.get(current_pos).get(cname)).getBytes();
			} catch (Exception ex) {
				return null;
			}
		} else {
			return cursor.getBlob(cursor.getColumnIndex(cname));
		}
	}

	public String getString(String cname) {
		try {
			if (buffered) {
				return (String)buffer.get(current_pos).get(cname);
			} else {
				return cursor.getString(cursor.getColumnIndex(cname));
			}
		} catch (Exception ex) {
			return null;
		}
	}
}
