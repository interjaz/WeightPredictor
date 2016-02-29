package app.android.weightpredictor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqliteUpdater {

    private static boolean updateSuccessfully = true;
    private static Boolean firstCall = Boolean.TRUE;

    public static boolean updateSuccessfully() {
        return updateSuccessfully;
    }

    public static void update(Context context, String databaseName, int databaseVersion, String appPath) {
        try {
            new SqliteProvider(context, databaseName, databaseVersion, appPath).close();
            updateSuccessfully = true;
        } catch (Exception ex) {
            Log.e("SqliteUpdater", "update", ex);
            updateSuccessfully = false;
        }
    }

    public static void onUpdate(int expectedVersion, SqliteProvider provider) {

        synchronized (firstCall) {
            if (!firstCall) {
                return;
            }
        }

        SQLiteDatabase database = null;
        try {
            database = provider.getDatabase();
            int version = database.getVersion();
            if (expectedVersion != version) {

                try {
                    database.beginTransaction();

//                    if (version == 0) {
//                        update01(database);
//                        database.setVersion(1);
//                        version = 1;
//                    }

                    database.setTransactionSuccessful();
                } catch (Exception ex) {
                    Log.e("SqliteUpdater", "Failed to update database", ex);
                } finally {
                    database.endTransaction();
                }

            }
        } finally {
            firstCall = Boolean.FALSE;
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }

    // Not used for now but lets leave it as an example
//    private static void update01(SQLiteDatabase database) {
//
//        String createWordOfTheDay = "CREATE  TABLE \"WordOfTheDay\" "
//                + "(\"WordOfTheDayId\" TEXT PRIMARY KEY  NOT NULL , " + "\"MemoBaseId\" TEXT NOT NULL , "
//                + "\"Mode\" INTEGER NOT NULL , " + "\"ProviderId\" INTEGER NOT NULL , " + "\"PreLanguageFrom\" TEXT, "
//                + "\"LanguageTo\" TEXT NOT NULL );";
//
//        String createSchedules = "CREATE  TABLE \"Schedules\" " + "(\"ScheduleId\" TEXT PRIMARY KEY  NOT NULL , "
//                + "\"MemoBaseId\" TEXT NOT NULL , " + "\"Hours\" INTEGER NOT NULL , "
//                + "\"Minutes\" INTEGER NOT NULL , " + "\"Monday\" BOOL NOT NULL , " + "\"Tuesday\" BOOL NOT NULL , "
//                + "\"Wednesday\" BOOL NOT NULL , " + "\"Thursday\" BOOL NOT NULL , " + "\"Friday\" BOOL NOT NULL , "
//                + "\"Saturday\" BOOL NOT NULL , " + "\"Sunday\" BOOL NOT NULL );";
//
//        database.execSQL(createWordOfTheDay);
//        database.execSQL(createSchedules);
//    }

}