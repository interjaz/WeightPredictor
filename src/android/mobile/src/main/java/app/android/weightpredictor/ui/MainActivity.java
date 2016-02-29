package app.android.weightpredictor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import app.android.weightpredictor.R;
import app.android.weightpredictor.database.SqliteUpdater;
import app.android.weightpredictor.entity.WeightEntry;
import app.android.weightpredictor.repository.IRepository;
import app.android.weightpredictor.repository.SqliteRepositoryFactory;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity  {

    private static final int REQUEST_WEIGHT = 1;

    private LineChartView mLineChart;

    private SqliteRepositoryFactory mRepositoryFactory;
    private IRepository<WeightEntry> mWeightEntryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddWeightActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_WEIGHT);

            }
        });

        mLineChart = (LineChartView)findViewById(R.id.main_chProgress);

        List<PointValue> yValues = new ArrayList<PointValue>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        SimpleDateFormat formater = new SimpleDateFormat("MMM dd");

        String formattedRecordedAt;
        Date date;
        AxisValue axisValue;
        ArrayList<Line> lines = new ArrayList<Line>();
        ArrayList<PointValue> points = new ArrayList<PointValue>();

        date = new Date(Date.parse("2012/01/01 10:01"));
        formattedRecordedAt = formater.format(date);
        yValues.add(new PointValue(date.getTime(), 13)); //use recordedAt timestamp/day of the year number or something like that as x value
        axisValue = new AxisValue(date.getTime());
        axisValue.setLabel(formattedRecordedAt);
        axisValues.add(axisValue);
        points.add(new PointValue(date.getTime(), 13));

        date = new Date(Date.parse("2012/01/01 12:01"));
        formattedRecordedAt = formater.format(date);
        yValues.add(new PointValue(date.getTime(), 8)); //use recordedAt timestamp/day of the year number or something like that as x value
        axisValue = new AxisValue(date.getTime());
        axisValue.setLabel(formattedRecordedAt);
        axisValues.add(axisValue);
        points.add(new PointValue(date.getTime(), 8));

        date = new Date(Date.parse("2012/01/01 18:01"));
        formattedRecordedAt = formater.format(date);
        yValues.add(new PointValue(date.getTime(), 20)); //use recordedAt timestamp/day of the year number or something like that as x value
        axisValue = new AxisValue(date.getTime());
        axisValue.setLabel(formattedRecordedAt);
        axisValues.add(axisValue);
        points.add(new PointValue(date.getTime(), 20));

        date = new Date(Date.parse("2012/01/02 18:01"));
        formattedRecordedAt = formater.format(date);
        yValues.add(new PointValue(date.getTime(), 15)); //use recordedAt timestamp/day of the year number or something like that as x value
        axisValue = new AxisValue(date.getTime());
        axisValue.setLabel(formattedRecordedAt);
        axisValues.add(axisValue);
        points.add(new PointValue(date.getTime(), 15));

        Line line = new Line(points);
        lines.add(line);
        LineChartData lineChartData = new LineChartData();
        Axis axis = new Axis(axisValues);
        axis.setTextSize(10);
        lineChartData.setAxisXBottom(axis);
        lineChartData.setLines(lines);

        mLineChart.setLineChartData(lineChartData);
        mLineChart.startDataAnimation();
        mLineChart.invalidate();

        SqliteUpdater.update(this, "db", 0, Environment.getExternalStorageState() + "/weight-predictor");

        mRepositoryFactory = new SqliteRepositoryFactory("db", 0, Environment.getExternalStorageState() + "/weight-predictor");
        mWeightEntryRepository = mRepositoryFactory.Create(this, WeightEntry.class);
        List<WeightEntry> list = mWeightEntryRepository.get();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MainActivity.REQUEST_WEIGHT && resultCode == AddWeightActivity.RESPONSE_SUCCESS) {


            try {
                String strDate = data.getStringExtra(AddWeightActivity.DATA_DATE);
                SimpleDateFormat parseFormat = new SimpleDateFormat(AddWeightActivity.FORMAT_DATE);

                double weight = data.getDoubleExtra(AddWeightActivity.DATA_WEIGHT, 0);
                java.util.Date date = parseFormat.parse(strDate);

                WeightEntry weightEntry = new WeightEntry();
                weightEntry.setDate(date);
                weightEntry.setWeight(weight);
                weightEntry.setWeigthEntryId(UUID.randomUUID().toString());

                mWeightEntryRepository.insert(weightEntry);

            } catch (Exception e) {

            }
        }

    }
}
