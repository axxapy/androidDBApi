package axp.lib.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBBase extends SQLiteOpenHelper {
	public DBBase(Context context, String DBNAME, CursorFactory factory, int DBVER) {
		super(context, DBNAME, factory, DBVER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
	
	public DBQuery BuildQuery() {
		return new DBQuery(this);
	}
	
	public void beginTransaction() {
		getWritableDatabase().beginTransaction();
	}
	
	public void commitTransaction() {
		SQLiteDatabase db = getWritableDatabase();
		db.setTransactionSuccessful();
		db.endTransaction();
		//db.releaseReference();
	}
	
	public void rollbackTransaction() {
		getWritableDatabase().endTransaction();
	}

	public int Insert(String table, ContentValues values) {
		try {
			return (int) this.getWritableDatabase().insert(table, null, values);
		} catch (SQLException ex) {
			Log.e("DEBUG", "SQL INSERT ERROR: " + ex.getMessage());
			return -1;
		}
	}

	public DBResult Select(String table, String[] columns, String where, String[] whereArgs, String groupBy,
			String having, String orderBy) {
		try {
			Cursor c = this.getWritableDatabase().query(table, columns, where, whereArgs, groupBy, having, orderBy);
			return new DBResult(c);
		} catch (SQLException ex) {
			Log.e("DEBUG", "SQL SELECT ERROR: " + ex.getMessage());
			return null;
		}
	}

	public void Delete(String table, String whereClause, String[] whereArgs) {
		try {
			this.getWritableDatabase().delete(table, whereClause, whereArgs);
		} catch (SQLException ex) {
			Log.e("DEBUG", "SQL SELECT ERROR: " + ex.getMessage());
		}
	}

	public void Update(String table, ContentValues values, String where, String[] whereArgs) {
		this.getWritableDatabase().update(table, values, where, whereArgs);
	}
}
