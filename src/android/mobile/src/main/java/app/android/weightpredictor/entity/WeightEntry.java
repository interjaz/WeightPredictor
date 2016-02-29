package app.android.weightpredictor.entity;

import java.util.Date;

/**
 * Created by inter on 29/02/2016.
 */
public class WeightEntry {

    private String mWeigthEntryId;
    private Date mDate;
    private double mWeight;

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public double getWeight() {
        return mWeight;
    }

    public void setWeight(double weight) {
        this.mWeight = weight;
    }

    public String getWeigthEntryId() {
        return mWeigthEntryId;
    }

    public void setWeigthEntryId(String weigthEntryId) {
        this.mWeigthEntryId = weigthEntryId;
    }
}
