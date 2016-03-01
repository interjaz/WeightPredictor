package app.android.weightpredictor.ViewModels;

import app.android.weightpredictor.entity.WeightEntry;

public class WeightEntryViewModel extends WeightEntry {

    private boolean mIsRising;

    public WeightEntryViewModel(WeightEntry weightEntry, boolean isRising) {
        setWeight(weightEntry.getWeight());
        setDate(weightEntry.getDate());
        setWeigthEntryId(weightEntry.getWeigthEntryId());
        mIsRising = isRising;
    }

    public boolean isRising() {
        return mIsRising;
    }

    public void setIsRising(boolean isRising) {
        this.mIsRising = isRising;
    }
}
