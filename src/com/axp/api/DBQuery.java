package com.axp.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBQuery {
	private final int TYPE_NONE = 0;
	private final int TYPE_SELECT = 1;
	private final int TYPE_UPDATE = 2;
	private final int TYPE_DELETE = 3;
	private final int TYPE_INSERT = 4;
	private final int TYPE_REPLACE = 5;

	private int type = TYPE_NONE;

	private SQLiteOpenHelper db;
	private boolean buffered = true;

	private String[] columns = {};
	private String table = null;
	private String where_string = null;
	private String[] where_args = {};
	private String group_by = null;
	private String order_by = null;
	private String having = null;
	private ContentValues data = null;

	public DBQuery(SQLiteOpenHelper db) {
		this.db = db;
	}

	public DBQuery(SQLiteOpenHelper db, boolean buffered) {
		this(db);
		this.buffered = buffered;
	}

	public DBQuery Select(String[] what) {
		type = TYPE_SELECT;
		columns = what;
		return this;
	}

	/*public DBQuery beginTransaction() {
		db.getWritableDatabase().beginTransaction();
		return this;
	}

	public DBQuery commitTransaction() {
		db.getWritableDatabase().setTransactionSuccessful();
		db.getWritableDatabase().endTransaction();
		return this;
	}

	public DBQuery rollbackTransaction() {
		db.getWritableDatabase().endTransaction();
		return this;
	}*/

	public DBQuery Select(String what) {
		type = TYPE_SELECT;
		if (what.contains(",")) {
			columns = what.split(",");
			for (int i = 0; i < columns.length; i++) {
				columns[i] = columns[i].trim();
			}
		} else {
			columns = new String[] { what };
		}
		return this;
	}

	public DBQuery Update(String table) {
		type = TYPE_UPDATE;
		this.table = table;
		return this;
	}

	public DBQuery Delete() {
		type = TYPE_DELETE;
		return this;
	}

	public DBQuery Delete(String table) {
		type = TYPE_DELETE;
		this.table = table;
		return this;
	}

	public DBQuery Insert(ContentValues values) {
		type = TYPE_INSERT;
		data = values;
		return this;
	}

	public DBQuery Replace(ContentValues values) {
		type = TYPE_REPLACE;
		data = values;
		return this;
	}

	public DBQuery Into(String table) {
		this.table = table;
		return this;
	}

	public DBQuery Set(ContentValues values) {
		data = values;
		return this;
	}

	public DBQuery From(String from) {
		table = from;
		return this;
	}

	public DBQuery Where(String where, Object[] o_args) {
		if (where_string == null) {
			where_string = where;
		} else {
			where_string += " AND " + where;
		}

		String[] args;
		if (o_args instanceof String[]) {
			args = (String[])o_args;
		} else {
			args = new String[o_args.length];
			for (int i = 0; i < o_args.length; i++) {
				args[i] = String.valueOf(o_args[i]);
			}
		}

		if (where_args.length < 1) {
			where_args = args;
		} else {
			String[] new_args = new String[where_args.length + args.length];
			int cnt = 0;
			for (String arg : where_args) {
				new_args[cnt++] = arg;
			}
			for (String arg : args) {
				new_args[cnt++] = arg;
			}
			where_args = new_args;
		}
		return this;
	}

	public DBQuery Where(String where, Object one_arg) {
		this.Where(where, new String[] { String.valueOf(one_arg) });
		return this;
	}

	/*
	 * public DBQuery Where(ContentValues values) { Set<Map.Entry<String,
	 * Object>> s=values.valueSet(); foreach (Map.Entry<String, Object> entry :
	 * s) {
	 * 
	 * } return this; }
	 */

	public DBQuery GroupBy(String group) {
		group_by = group;
		return this;
	}

	public DBQuery OrderBy(String order) {
		order_by = order;
		return this;
	}

	public DBQuery Having(String having) {
		this.having = having;
		return this;
	}

	public DBResult execute() {
		try {
			long insert_id;
			switch (type) {
				case TYPE_SELECT:
					SQLiteDatabase conn = this.db.getReadableDatabase();
					Cursor c = conn.query(this.table, columns, where_string, where_args, group_by, having, order_by);
					return new DBResult(c, this.buffered);
				case TYPE_UPDATE:
					this.db.getWritableDatabase().update(table, data, where_string, where_args);
					return new DBResult(DBResult.RESULT_OK);
				case TYPE_DELETE:
					this.db.getWritableDatabase().delete(table, where_string, where_args);
					return new DBResult(DBResult.RESULT_OK);
				case TYPE_INSERT:
					insert_id = this.db.getWritableDatabase().insert(table, null, data);
					return new DBResult(DBResult.RESULT_OK, insert_id);
				case TYPE_REPLACE:
					insert_id = this.db.getWritableDatabase().replace(table, null, data);
					return new DBResult(DBResult.RESULT_OK, insert_id);
			}
		} catch (SQLException ex) {
			Log.e("DEBUG", "SQL ERROR: " + ex.getMessage());
			return null;
		}
		return null;
	}
}
