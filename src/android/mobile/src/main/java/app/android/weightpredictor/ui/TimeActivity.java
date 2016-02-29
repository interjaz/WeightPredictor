package app.android.weightpredictor.ui;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import app.android.weightpredictor.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TimeActivity extends AppCompatActivity {

    public static final int RESPONSE_SUCCESS = 1;
    public static final String DATA = "DATA";

    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mTimePicker = (TimePicker)findViewById(R.id.time_tpTime);

        Button btnOk = (Button)findViewById(R.id.time_btnOk);
        btnOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String date = GetTime();
                intent.putExtra(TimeActivity.DATA, date);
                TimeActivity.this.setResult(TimeActivity.RESPONSE_SUCCESS, intent);
                TimeActivity.this.finish();
            }
        });

        Button btnCancel = (Button)findViewById(R.id.time_btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                TimeActivity.this.setResult(0, new Intent());
                TimeActivity.this.finish();
            }
        });

    }

    private String GetTime(){
        if (Build.VERSION.SDK_INT >= 23) {
            String date = String.format("%02d:%02d", mTimePicker.getHour(), mTimePicker.getMinute());
            return date;
        } else {
            String date = String.format("%02d:%02d", mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
            return date;
        }
    }

}
