package app.android.weightpredictor.repository;

import android.content.Context;

import app.android.weightpredictor.entity.WeightEntry;

/**
 * Created by inter on 29/02/2016.
 */
public class SqliteRepositoryFactory {

    private String mDatabaseName;
    private int mDatabaseVersion;
    private String mAppPath;

    public SqliteRepositoryFactory(String databaseName, int databaseVersion, String appPath) {
        mDatabaseName = databaseName;
        mDatabaseVersion = databaseVersion;
        mAppPath = appPath;
    }

    public <T> IRepository<T> Create(Context context, Class<T> klass) {

        if( WeightEntry.class.equals(klass)) {
            return (IRepository<T>) new WeightEntryRepository(context, mDatabaseName, mDatabaseVersion, mAppPath);
        }

        throw new RuntimeException("Type not supported");
    }

}
