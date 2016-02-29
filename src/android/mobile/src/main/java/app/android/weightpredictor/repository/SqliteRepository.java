package app.android.weightpredictor.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.android.weightpredictor.database.SqliteAdapter;

public abstract class SqliteRepository<T> extends SqliteAdapter implements IRepository<T>
{
    private String mTableName;
    private String mPrimaryKey;

    protected SqliteRepository(Context context, String databaseName, int databaseVersion, String appPath, String tableName, String primaryKey) {
        super(context, databaseName, databaseVersion, appPath);

        mTableName = tableName;
        mPrimaryKey = primaryKey;
    }

    @Override
    public T get(String key) {
        String sql = "SELECT * FROM " + mTableName + " WHERE " + mPrimaryKey + " = ?";
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getDatabase();
            cursor = db.rawQuery(sql, new String[] { key });
            if(cursor.moveToNext()) {
                T obj = bind(cursor);
                return obj;
            }

        } finally {
            if(cursor != null) {
                cursor.close();
            }

            closeDatabase();
        }

        return null;
    }

    @Override
    public List<T> get() {
        String sql = "SELECT * FROM " + mTableName;
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getDatabase();
            cursor = db.rawQuery(sql, new String[] {  });
            ArrayList<T> list = new ArrayList<T>();
            while(cursor.moveToNext()) {
                T obj = bind(cursor);
                list.add(obj);
            }

            return list;

        } finally {
            if(cursor != null) {
                cursor.close();
            }

            closeDatabase();
        }
    }

    @Override
    public List<T> get(int take) {
        String sql = "SELECT * FROM " + mTableName + " LIMIT " + take;
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getDatabase();
            cursor = db.rawQuery(sql, new String[] {  });
            ArrayList<T> list = new ArrayList<T>();
            while(cursor.moveToNext()) {
                T obj = bind(cursor);
                list.add(obj);
            }

            return list;

        } finally {
            if(cursor != null) {
                cursor.close();
            }

            closeDatabase();
        }
    }

    @Override
    public List<T> get(int take, int skip) {
        String sql = "SELECT * FROM " + mTableName + " LIMIT " + take + " OFFSET " + skip;
        Cursor cursor = null;

        try {
            SQLiteDatabase db = getDatabase();
            cursor = db.rawQuery(sql, new String[] {  });
            ArrayList<T> list = new ArrayList<T>();
            while(cursor.moveToNext()) {
                T obj = bind(cursor);
                list.add(obj);
            }

            return list;

        } finally {
            if(cursor != null) {
                cursor.close();
            }

            closeDatabase();
        }
    }

    @Override
    public void insert(T item) {
        try {
            SQLiteDatabase db = getDatabase();
            ContentValues values = bind(item);
            String key = getKey(item);
            db.insert(mTableName, null, values);
        }
        finally{
            closeDatabase();
        }
    }

    @Override
    public void update(T item) {
        try {
            SQLiteDatabase db = getDatabase();
            ContentValues values = bind(item);
            String key = getKey(item);
            db.update(mTableName, values, mPrimaryKey + " = ?", new String[]{key});
        }
        finally{
            closeDatabase();
        }
    }

    @Override
    public void delete(String key) {
        try {
            SQLiteDatabase db = getDatabase();
            db.delete(mTableName, mPrimaryKey + " = ?", new String[]{key});
        }
        finally{
            closeDatabase();
        }
    }

    protected abstract T bind(Cursor cursor);

    protected abstract String getKey(T obj);

    protected abstract ContentValues bind(T obj);
}
