package com.kaosh.militarycounter;

import java.text.NumberFormat;
import java.util.Calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MilitaryCounterActivity extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mDurationYear;
	private int mDurationMonth;
	private int mDiscountDay;
	
	private static final String PREFS_NAME = "MilitaryCounter";
	
	private Handler handler = new Handler();
	
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Find the widget id from the intent. 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
        	Button bOk = (Button) findViewById (R.id.ok);
        	bOk.setVisibility(View.VISIBLE);
        	bOk.setOnClickListener(new OnClickListener(){
                public void onClick(View v) {
                	saveStorage ();
                    final Context context = MilitaryCounterActivity.this;
            		// Push widget update to surface with newly set prefix
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    MilitaryCounterWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
        	});
    	}
        loadStorage ();
        initUI ();
        
        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);
    }
    
    @Override
    public void onPause() {
    	saveStorage ();
    	super.onPause();
    	
    	
    }
    
    private Runnable updateTimer = new Runnable() {
    	public void run() {
    		updateStatus ();
    		handler.postDelayed(this, 1000);
    	}
    };
    
    private void loadStorage () {
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	Calendar c = Calendar.getInstance();
		mYear = settings.getInt("sYear", c.get(Calendar.YEAR)); 
		mMonth = settings.getInt("sMonth", c.get(Calendar.MONTH)); 
		mDay = settings.getInt("sDay", c.get(Calendar.DAY_OF_MONTH));
		
		mDurationYear = settings.getInt("dYear", 1);
		mDurationMonth = settings.getInt("dMonth", 0);
		
		mDiscountDay = settings.getInt("dDay", 30);
    }
    
    private void saveStorage () {
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putInt("sYear", mYear);
    	editor.putInt("sMonth", mMonth);
    	editor.putInt("sDay", mDay);
    	editor.putInt("dYear", mDurationYear);
    	editor.putInt("dMonth", mDurationMonth);
    	editor.putInt("dDay", mDiscountDay);
    	editor.commit();
    }
    
    private void initUI () {
    	Spinner ySpinner = (Spinner) findViewById(R.id.year);
    	Spinner mSpinner = (Spinner) findViewById(R.id.month);
    	Spinner dDaySpinner = (Spinner) findViewById(R.id.discount);
    	
    	ArrayAdapter<CharSequence> yAdapter = ArrayAdapter.createFromResource(
    			this, 
    			R.array.years, 
    			android.R.layout.simple_spinner_item
    			);
    	ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(
    			this, 
    			R.array.months, 
    			android.R.layout.simple_spinner_item
    			);
    	ArrayAdapter<CharSequence> dDayAdapter = ArrayAdapter.createFromResource(
    			this, 
    			R.array.discounts, 
    			android.R.layout.simple_spinner_item
    			);
    	yAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	dDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    	ySpinner.setAdapter(yAdapter);
    	mSpinner.setAdapter(mAdapter);
    	dDaySpinner.setAdapter(dDayAdapter);
    	
    	ySpinner.setSelection(mDurationYear);
    	mSpinner.setSelection(mDurationMonth);
    	dDaySpinner.setSelection(mDiscountDay);
    	
    	ySpinner.setOnItemSelectedListener(new OnItemSelectedListener (){
    		@Override
    		public void onItemSelected(AdapterView<?> parent, View v, int position, long id) 
            {
    			mDurationYear = position;
    			updateStatus ();
            }
            public void onNothingSelected(AdapterView<?> arg0) 
            {
            }
    	});
    	
    	mSpinner.setOnItemSelectedListener(new OnItemSelectedListener (){
    		@Override
    		public void onItemSelected(AdapterView<?> parent, View v, int position, long id) 
            {
    			mDurationMonth = position;
    			updateStatus ();
            }
            public void onNothingSelected(AdapterView<?> arg0) 
            {
            }
    	});

    	dDaySpinner.setOnItemSelectedListener(new OnItemSelectedListener (){
    		@Override
    		public void onItemSelected(AdapterView<?> parent, View v, int position, long id) 
            {
    			mDiscountDay = position;
    			updateStatus ();
            }
            public void onNothingSelected(AdapterView<?> arg0) 
            {
            }
    	});

    	
    	DatePicker datepicker = (DatePicker) findViewById(R.id.startdate);
    	datepicker.init(
    			mYear, 
    			mMonth, 
    			mDay,
    			new DatePicker.OnDateChangedListener(){
    				@Override
    				public void onDateChanged(DatePicker view, int y, int m, int d) {
    					mYear = y;
    					mMonth = m;
    					mDay = d;
    					updateStatus ();
    				}
    			});
    }
    
    private void updateStatus () {
    	int ey = mYear;
    	int em = mMonth;
    	int ed = mDay;
    	
    	ey += mDurationYear;
    	em += mDurationMonth;
    	
    	if (em>Calendar.DECEMBER) {
    		ey++;
    		em -= 12;
    	}
    	
    	Calendar end = Calendar.getInstance();
    	end.set(ey, em, ed, 0, 0, 0);
    	end.add(Calendar.DAY_OF_YEAR, -mDiscountDay);
    	Calendar now = Calendar.getInstance();
    	Calendar start = Calendar.getInstance();
    	start.set (mYear, mMonth, mDay, 0, 0, 0);
    	
    	long toend = end.getTimeInMillis() - now.getTimeInMillis();
    	long sectoend = toend / 1000;
    	long daytoend = sectoend / 86400 + 1;
    	double percent = 100.0 * (now.getTimeInMillis() - start.getTimeInMillis()) / (end.getTimeInMillis() - start.getTimeInMillis());

    	TextView desc = (TextView) findViewById(R.id.desc);
    	TextView day = (TextView) findViewById(R.id.day);
    	TextView sec = (TextView) findViewById(R.id.sec);
    	TextView perc = (TextView) findViewById(R.id.percent);
    	
    	if (percent<0) {
    		percent = 0;
    	}
    	
    	if (percent<=100) {
    		desc.setText (getResources().getString(R.string.to));
	    	day.setText (String.valueOf(daytoend));
	    	sec.setText (String.valueOf(sectoend));
	    	NumberFormat nf = NumberFormat.getInstance();
	    	nf.setMaximumFractionDigits(4);
	    	perc.setText (nf.format(percent));
    	}
    	else {
    		desc.setText (getResources().getString(R.string.cong));
    		day.setText ("0");
    		sec.setText ("0");
    		perc.setText ("100");
    	}
    }
}