package app.android.weightpredictor.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import app.android.weightpredictor.R;
import app.android.weightpredictor.ViewModels.WeightEntryViewModel;
import app.android.weightpredictor.adapters.WeightEntryAdapter;
import app.android.weightpredictor.database.SqliteUpdater;
import app.android.weightpredictor.entity.WeightEntry;
import app.android.weightpredictor.helpers.Helper;
import app.android.weightpredictor.repository.IRepository;
import app.android.weightpredictor.repository.SqliteRepositoryFactory;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity  {

    private static final int REQUEST_WEIGHT = 1;

    private LineChartView mLineChart;

    private SqliteRepositoryFactory mRepositoryFactory;
    private IRepository<WeightEntry> mWeightEntryRepository;
    private WeightEntryAdapter mWeightEntryAdapter;

    private SimpleDateFormat mSimpleDateFormat;
    private SimpleDateFormat mSimpleTimeFormat;


    private static final String FORMAT_DATE_UI = "dd MMMM yyyy";
    private static final String FORMAT_TIME_UI = "HH:mm";

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



        SqliteUpdater.update(this, "db", 0, Environment.getExternalStorageDirectory() + "/weight-predictor");

        mRepositoryFactory = new SqliteRepositoryFactory("db", 0, Environment.getExternalStorageDirectory() + "/weight-predictor");

        mSimpleDateFormat = new SimpleDateFormat(MainActivity.FORMAT_DATE_UI);
        mSimpleTimeFormat = new SimpleDateFormat(MainActivity.FORMAT_TIME_UI);

        ListView lstWeights = (ListView)findViewById(R.id.main_lstWeights);
        mWeightEntryAdapter = new WeightEntryAdapter(this, mSimpleDateFormat, mSimpleTimeFormat,
                Helper.getColor(this, R.color.colorAccent),
                Helper.getColor(this, R.color.colorPrimary));

        lstWeights.setAdapter(mWeightEntryAdapter);

        mWeightEntryAdapter.setOnLongClickListener(new WeightEntryAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(WeightEntry item) {
                mWeightEntryRepository.delete(item.getWeigthEntryId());
                bindWeights();
            }
        });

        mWeightEntryRepository = mRepositoryFactory.Create(this, WeightEntry.class);
        bindWeights();
    }

    private void bindWeights() {

        List<WeightEntry> weights = mWeightEntryRepository.get();
        Collections.sort(weights, new Comparator<WeightEntry>() {
            @Override
            public int compare(WeightEntry lhs, WeightEntry rhs) {
                return lhs.getDate().after(rhs.getDate()) ? 1 : -1;
            }
        });

        // Should be sorted ascending
        double previous = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        List<WeightEntryViewModel> viewModels = new ArrayList<WeightEntryViewModel>();
        for (WeightEntry weight : weights){
            double value = weight.getWeight();
            boolean isRising = previous < value;
            previous = value;
            max = max > value ? max : value;
            min = min < value ? min : value;

            WeightEntryViewModel viewModel = new WeightEntryViewModel(weight, isRising);
            viewModels.add(viewModel);
        }

        Collections.sort(viewModels, new Comparator<WeightEntryViewModel>() {
            @Override
            public int compare(WeightEntryViewModel lhs, WeightEntryViewModel rhs) {
                return lhs.getDate().after(rhs.getDate()) ? -1 : 1;
            }
        });

        List<PointValue> yValues = new ArrayList<PointValue>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        SimpleDateFormat formater = new SimpleDateFormat("d");

        String formattedRecordedAt;
        AxisValue axisValue;
        ArrayList<Line> lines = new ArrayList<Line>();
        ArrayList<PointValue> points = new ArrayList<PointValue>();

        for(WeightEntry weight : viewModels){
            Date date = weight.getDate();
            long time = date.getTime();
            int value = (int)weight.getWeight()*100;
            formattedRecordedAt = formater.format(date);
            yValues.add(new PointValue(time, value));
            axisValue = new AxisValue(time);
            axisValue.setLabel(formattedRecordedAt);
            axisValues.add(axisValue);
            points.add(new PointValue(time, value));
        }

        Line line = new Line(points);
        line.setCubic(true);
        line.setColor(Helper.getColor(this, R.color.colorAccent));

        lines.add(line);
        LineChartData lineChartData = new LineChartData();
        Axis axis = new Axis(axisValues);
        axis.setTextSize(10);
        lineChartData.setAxisXBottom(axis);
        lineChartData.setLines(lines);

        mLineChart.setLineChartData(lineChartData);
        Viewport viewport = new Viewport(mLineChart.getMaximumViewport());
        viewport.bottom = (int)(min - 2) * 100;
        viewport.top = (int) (max + 2) * 100;
        mLineChart.setMaximumViewport(viewport);
        mLineChart.setCurrentViewport(viewport);
        mLineChart.setViewportCalculationEnabled(false);

        mLineChart.startDataAnimation();

        mWeightEntryAdapter.clear();
        mWeightEntryAdapter.addAll(viewModels);
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

                bindWeights();

            } catch (Exception e) {

            }
        }

    }
}
