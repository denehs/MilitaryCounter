package com.kaosh.militarycounter;

import java.util.Calendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class MilitaryCounterWidget extends AppWidgetProvider {
	
	private static final String PREFS_NAME = "MilitaryCounter";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int i=0 ;i<appWidgetIds.length ;i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget (context, appWidgetManager, appWidgetId);
		}
	}
	
	static void updateAppWidget (Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {
		int mYear;
		int mMonth;
		int mDay;
		int mDurationYear;
		int mDurationMonth;
		int mDiscountDay;

    	SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
    	Calendar c = Calendar.getInstance();
		mYear = settings.getInt("sYear", c.get(Calendar.YEAR)); 
		mMonth = settings.getInt("sMonth", c.get(Calendar.MONTH)); 
		mDay = settings.getInt("sDay", c.get(Calendar.DAY_OF_MONTH));
		
		mDurationYear = settings.getInt("dYear", 1);
		mDurationMonth = settings.getInt("dMonth", 0);
		
		mDiscountDay = settings.getInt("dDay", 30);
		

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

    	if (percent<0) {
    		percent = 0;
    	}

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
    	if (percent<=100) {
    		remoteViews.setTextViewText(R.id.day, String.valueOf(daytoend));
    		remoteViews.setProgressBar(R.id.progress, 100, (int)percent, false);
    	}
    	else {
    		remoteViews.setTextViewText(R.id.day, "0");
    		remoteViews.setProgressBar(R.id.progress, 100, 100, false);
    	}
    	appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

}
