package app.android.weightpredictor.database;


import java.io.IOException;
import java.util.Hashtable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public abstract class SqliteAdapter {

    protected static final String Tag = "SqliteAdapter";

    private static Hashtable<String, TableHandle> m_tableHandle = new Hashtable<String, TableHandle>();

    private String m_databaseName;
    private int m_version;
    private String mAppPath;
    private Context m_context;

    public SqliteAdapter(Context context, String databaseName, int databaseVersion, String appPath) {
        m_context = context;
        m_databaseName = databaseName;
        m_version = databaseVersion;
        mAppPath = appPath;
    }

    public SqliteAdapter(Context context, String databaseName, int version, boolean persistant) {
        m_context = context;
        m_databaseName = databaseName;
        m_version = version;
    }

    public SQLiteDatabase getDatabase() {
        try {
            TableHandle handle;
            synchronized (m_tableHandle) {
                handle = m_tableHandle.get(m_databaseName);
                if (handle == null) {
                    handle = open();
                }
                handle.ActiveConnections += 1;
            }

            return handle.SqliteHelper.getDatabase();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public final void closeDatabase() {
        boolean close = false;

        synchronized (m_tableHandle) {

            try {
                TableHandle handle = m_tableHandle.get(m_databaseName);
                handle.ActiveConnections -= 1;

                if (handle.ActiveConnections == 0) {
                    close = true;
                }

                if (close) {
                    close(handle);
                }
            } catch(Exception ex) {
                Log.e("SqliteAdapter", ex.toString());
            }
        }
    }

    public String getDatabaseName() {
        return m_databaseName;
    }

    public int getVersion() {
        return m_version;
    }

    public Context getContext() {
        return m_context;
    }

    public final void closePersistant() {
        synchronized (m_tableHandle) {
            TableHandle handle = m_tableHandle.get(m_databaseName);
            if (handle != null) {
                close(handle);
            }
        }
    }

    private final TableHandle open() throws IOException {
        synchronized (m_tableHandle) {
            TableHandle handle = new TableHandle();
            handle.ActiveConnections = 0;
            handle.SqliteHelper = new SqliteProvider(m_context, m_databaseName, m_version, mAppPath);

            m_tableHandle.put(m_databaseName, handle);
            return handle;
        }
    }

    private final void close(TableHandle handle) {
        synchronized (m_tableHandle) {
            handle.SqliteHelper.close();
            m_tableHandle.remove(m_databaseName);
        }
    }

    private static class TableHandle {
        public int ActiveConnections;
        public SqliteProvider SqliteHelper;
    }

}