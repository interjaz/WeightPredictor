package app.android.weightpredictor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;

import app.android.weightpredictor.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DateActivity extends AppCompatActivity {

    public static final int RESPONSE_SUCCESS = 1;
    public static final String DATA = "DATA";
    public static final String FORMAT_DATE = "yyyy/MM/dd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_date);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final MaterialCalendarView calendar = (MaterialCalendarView)findViewById(R.id.date_cvDate);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateActivity.FORMAT_DATE);

        calendar.setSelectedDate(CalendarDay.today());

        Button btnOk = (Button)findViewById(R.id.date_btnOk);
        btnOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                CalendarDay selectedDate = calendar.getSelectedDate();
                String date = dateFormat.format(selectedDate.getDate());
                intent.putExtra(DateActivity.DATA, date);

                DateActivity.this.setResult(DateActivity.RESPONSE_SUCCESS, intent);
                DateActivity.this.finish();
            }
        });

        Button btnCancel = (Button)findViewById(R.id.date_btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DateActivity.this.setResult(0, new Intent());
                DateActivity.this.finish();
            }
        });
    }

}
