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
	private int limit = 0;
	private int offset = 0;
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
		if (columns.length < 1) {
			columns = what;
		} else {
			String[] __columns = new String[columns.length + what.length];
			int i = 0;
			for (String s : columns) {
				__columns[i] = s;
				i++;
			}
			for (String s : what) {
				__columns[i] = s;
				i++;
			}
			columns = __columns;
		}
		return this;
	}

	public DBQuery Select(String what) {
		String[] new_columns;
		type = TYPE_SELECT;
		if (what.contains(",")) {
			new_columns = what.split(",");
			for (int i = 0; i < new_columns.length; i++) {
				new_columns[i] = new_columns[i].trim();
			}
		} else {
			new_columns = new String[] { what };
		}
		if (columns.length < 1) {
			columns = new_columns;
		} else {
			String[] __columns = new String[columns.length + new_columns.length];
			int i = 0;
			for (String s : columns) {
				__columns[i] = s;
				i++;
			}
			for (String s : new_columns) {
				__columns[i] = s;
				i++;
			}
			columns = __columns;
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
	
	public DBQuery From(String[] from) {
		String __from = "";
		boolean first = true;
		for (String s : from) {
			if (first) {
				first = false;
			} else {
				__from += ", ";
			}
			__from += s;
		}
		return From(__from);
	}

	public DBQuery From(String from) {
		if (table == null || table.length() < 1) {
			table = from;
		} else {
			table += ", "+from;
		}
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
		} else if (o_args == null) {
			args = null;
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
		this.Where(where, one_arg == null ? null : new String[] { String.valueOf(one_arg) });
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
	
	public DBQuery Limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public DBQuery Offset(int offset) {
		this.offset = offset;
		return this;
	}

	public DBResult execute() {
		try {
			long insert_id;
			switch (type) {
				case TYPE_SELECT:
					SQLiteDatabase conn = this.db.getReadableDatabase();
					Cursor c = null;
					if (limit != 0) {
						String _limit = String.valueOf(limit);
						if (offset > 0) _limit += " OFFSET "+String.valueOf(offset);
						c = conn.query(table, columns, where_string, where_args, group_by, having, order_by, _limit);
					} else {
						c = conn.query(this.table, columns, where_string, where_args, group_by, having, order_by);
					}
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
