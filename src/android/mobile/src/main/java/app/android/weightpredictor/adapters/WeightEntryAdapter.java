package app.android.weightpredictor.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import app.android.weightpredictor.R;
import app.android.weightpredictor.ViewModels.WeightEntryViewModel;
import app.android.weightpredictor.entity.WeightEntry;


public class WeightEntryAdapter extends ModifiableAdapter<WeightEntryViewModel> {

    private final SimpleDateFormat mSimpleDateFormat;
    private final SimpleDateFormat mSimpleTimeFormat;
    private final int mUpColor;
    private final int mDownColor;
    private OnLongClickListener mLongClickListener;

    public WeightEntryAdapter(Context context, SimpleDateFormat simpleDateFormat, SimpleDateFormat simpleTimeFormat, int upColor, int downColor) {
        super(context, R.layout.item_weight);
        mSimpleDateFormat = simpleDateFormat;
        mSimpleTimeFormat = simpleTimeFormat;
        mUpColor = upColor;
        mDownColor = downColor;
    }

    @Override
    protected View getViewDefinition(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = m_inflater.inflate(m_resourceId, parent, false);
        } else {
            view = convertView;
        }

        final WeightEntryViewModel object = getItem(position);

        TextView txtDate = (TextView) view.findViewById(R.id.itemWeight_txtDate);
        TextView txtTime = (TextView) view.findViewById(R.id.itemWeight_txtTime);
        TextView txtWeight = (TextView) view.findViewById(R.id.itemWeight_txtWeight);

        txtWeight.setText(Double.toString(object.getWeight()));
        txtDate.setText(mSimpleDateFormat.format(object.getDate()));
        txtTime.setText(mSimpleTimeFormat.format(object.getDate()));

        if(object.isRising()) {
            txtWeight.setTextColor(mUpColor);
        } else {
            txtWeight.setTextColor(mDownColor);
        }

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mLongClickListener != null){
                    mLongClickListener.onLongClick(object);
                }

                return true;
            }
        });

        return view;
    }

    public void setOnLongClickListener(OnLongClickListener listener){
        mLongClickListener = listener;
    }

    public interface OnLongClickListener {
        void onLongClick(WeightEntry item);
    }
}
