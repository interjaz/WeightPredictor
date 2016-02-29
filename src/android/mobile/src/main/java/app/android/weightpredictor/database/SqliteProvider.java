package app.android.weightpredictor.database;

import java.io.OutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import app.android.weightpredictor.helpers.Helper;

public class SqliteProvider {

    private SQLiteDatabase m_database;
    private Context mContext;
    private String mDatabaseName;
    private String mDatabasePath;
    private String mAppPath;
    private static final String Tag = SqliteProvider.class.toString();

    public SqliteProvider(Context context, String databaseName, int databaseVersion, String appPath) throws IOException {

        mContext = context;
        mDatabaseName = databaseName;
        mAppPath = appPath;

        if (!databaseExists()) {
            copyDatabaseFromAssets();
        } else {
            SqliteUpdater.onUpdate(databaseVersion, this);
        }
    }

    private String getDatabasePath() {
        if (mDatabasePath != null) {
            return mDatabasePath;
        }

        // Internal storage is not reliable use sdcard instead

        File dbDir = new File(mAppPath + "/db");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }

        mDatabasePath = dbDir.getAbsolutePath() + "/" + mDatabaseName + ".sqlite";
        return mDatabasePath;
    }

    private boolean databaseExists() {
        SQLiteDatabase database = null;

        try {
            database = SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (SQLiteException ex) {
            Log.v(Tag, "DatabaseNotFound ", ex);
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return database != null;
    }

    private void copyDatabaseFromAssets() throws IOException {
        InputStream input = mContext.getAssets().open(mDatabaseName + ".sqlite");
        OutputStream output = new FileOutputStream(getDatabasePath());
        Helper.copyFile(input, output);
    }

    public synchronized void close() {
        if (m_database != null) {
            m_database.close();
            m_database = null;
            Log.w("DB", "Close");
        }
    }

    // Allows to use earlier API version
    public String getDbName() {
        return mDatabaseName;
    }

    public Context getContext() {
        return mContext;
    }

    public String createBackup() throws IOException {
        String inputPath = getDatabasePath();
        String outputPath = inputPath + ".bak";
        File outputFile = new File(outputPath);

        if(outputFile.exists()) {
            outputFile.delete();
        }

        InputStream input = new FileInputStream(getDatabasePath());
        OutputStream output = new FileOutputStream(outputPath);
        Helper.copyFile(input, output);

        return outputPath;
    }

    public synchronized SQLiteDatabase getDatabase() {
        if (m_database != null && !m_database.isOpen()) {
            m_database = null;
        }

        if (m_database == null) {
            m_database = SQLiteDatabase.openDatabase(getDatabasePath(), null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            Log.w("DB", "Open");
        }

        return m_database;
    }

}