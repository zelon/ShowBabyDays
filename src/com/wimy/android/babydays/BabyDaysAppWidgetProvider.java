package com.wimy.android.babydays;

import java.util.Calendar;
import java.util.TimeZone;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class BabyDaysAppWidgetProvider extends AppWidgetProvider
{
	static Context sContext = null;
	static AppWidgetManager sAppWidgetManager = null;
	static int[] sAppWidgetIds = null;

	@Override
	public void onEnabled(Context context)
	{
		super.onEnabled(context);
		Log.i("ShowBabyDays","onEnabled");
		
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        context.getApplicationContext().registerReceiver(this, filter);
	}
	
    @Override
	public void onReceive(Context context, Intent intent)
	{
        String action = intent.getAction();
        Log.i("ShowBabyDays", "onReceive() : " + action);
        if (action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)
                )
        {
			Log.i("ShowBabyDays", "on recv DateChangeReceiver.onReceive date_changed");
			updateUI();
        }
        
        super.onReceive(context, intent);
	}

	private void updateUI()
	{
		Log.i("ShowBabyDays", "updateUI()");
		
		if ( sContext == null )
		{
			Log.i("ShowBabyDays", "mContext is null");
			return;
		}
		
		if ( sAppWidgetManager == null )
		{
			Log.i("ShowBabyDays", "mAppWidgetManager is null");
			return;
		}
		
		if ( sAppWidgetIds == null )
		{
			Log.i("ShowBabyDays", "mAppWidgetIds is null");
			return;
		}
		
        final int N = sAppWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++)
        {
            int appWidgetId = sAppWidgetIds[i];

            Intent intent = getURLIntent();
            PendingIntent pendingIntent = PendingIntent.getActivity(sContext, 0,
					intent, 0);
            
            RemoteViews views = new RemoteViews(sContext.getPackageName(), R.layout.baby_days_appwidget);
            views.setTextViewText(R.id.tv, makeString());
            views.setTextViewText(R.id.updated_time, "Updated at " + getCurrentTime());
            views.setOnClickPendingIntent(R.id.ll, pendingIntent);

            sAppWidgetManager.updateAppWidget(appWidgetId, views);
        }
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		Log.i("ShowBabyDays", "onUpdate()");
		
		sContext = context;
		sAppWidgetManager = appWidgetManager;
		sAppWidgetIds = appWidgetIds;
		
		updateUI();
    }

    private String getCurrentTime()
    {
    	Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    	now.setTimeInMillis(System.currentTimeMillis());

    	return now.getTime().toLocaleString();
    }
    
    private Intent getURLIntent()
    {
    	String url = String.format("http://www.google.co.kr/search?q=임신+%d주", getWeek());
    	
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(url));
    	
    	return i;
    }
    
    private long getWeek()
    {
    	Calendar start = getStartCalendar();
    	
    	Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    	now.setTimeInMillis(System.currentTimeMillis());
    	
    	long diffMils = now.getTimeInMillis() - start.getTimeInMillis(); 
    	long diffDays = diffMils / ( 1000 * 60 * 60 * 24 );
    	
    	Log.i("ShowBabyDays", "diffDays : " + diffDays);
    	
    	long diffWeek = diffDays / 7;
    	
    	return diffWeek + 1;
    }
    
    private static Calendar getStartCalendar()
    {
    	Calendar start = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    	start.set(2011, 1, 15, 0, 0, 0);
    	
    	return start;
    }
    
	public static String makeString()
    {
    	Calendar start = getStartCalendar();
    	
    	Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    	now.setTimeInMillis(System.currentTimeMillis());
    	now.set(Calendar.HOUR_OF_DAY, 0);
    	now.set(Calendar.SECOND, 0);
    	now.set(Calendar.MILLISECOND, 0);
    	
    	StringBuilder sb = new StringBuilder();
    	
    	long diffMils = now.getTimeInMillis() - start.getTimeInMillis(); 
    	Log.i("ShowBabyDays", "diffMils : " + diffMils);

    	long diffDays = diffMils / ( 1000 * 60 * 60 * 24 );
    	Log.i("ShowBabyDays", "diffDays : " + diffDays);
    	
    	long remainDiff = diffMils % ( 1000 * 60 * 60 * 24 );
    	Log.i("ShowBabyDays", "remainDays : " + remainDiff);
    	
    	long diffWeek = diffDays / 7;
    	long remainDays = diffDays - (diffWeek * 7);
    	
    	sb.append(String.format(" %d주 %d일 (총 %d일)", (diffWeek+1), remainDays, diffDays));
    	
    	return sb.toString();
    }
}
