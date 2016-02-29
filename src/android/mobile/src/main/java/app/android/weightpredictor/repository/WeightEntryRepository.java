package app.android.weightpredictor.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import app.android.weightpredictor.entity.WeightEntry;
import app.android.weightpredictor.helpers.DatabaseHelper;
import app.android.weightpredictor.helpers.DateHelper;

public class WeightEntryRepository extends SqliteRepository<WeightEntry> {

    protected WeightEntryRepository(Context context, String databaseName, int databaseVersion, String appPath) {
        super(context, databaseName, databaseVersion, appPath, "WeightEntry", "WeightEntryId");
    }

    @Override
    protected WeightEntry bind(Cursor cursor) {
        WeightEntry obj = new WeightEntry();

        obj.setWeigthEntryId(DatabaseHelper.getString(cursor, "WeightEntryId"));
        obj.setDate(DatabaseHelper.getDate(cursor, "Date"));
        obj.setWeight(DatabaseHelper.getDouble(cursor, "Weight"));

        return obj;
    }

    @Override
    protected String getKey(WeightEntry obj) {
        return obj.getWeigthEntryId();
    }

    @Override
    protected ContentValues bind(WeightEntry obj) {
        ContentValues values = new ContentValues();

        values.put("WeightEntryId", obj.getWeigthEntryId());
        values.put("Date", DateHelper.toNormalizedString(obj.getDate()));
        values.put("Weight", obj.getWeight());

        return values;
    }
}
