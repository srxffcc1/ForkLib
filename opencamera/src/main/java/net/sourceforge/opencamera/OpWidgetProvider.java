package net.sourceforge.opencamera;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/** Handles the Open Camera lock screen widget. Lock screen widgets are no
 *  longer supported in Android 5 onwards (instead Open Camera can be launched
 *  from the lock screen using the standard camera icon), but this is kept here
 *  for older Android versions.
 */
public class OpWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "OpWidgetProvider";
	
	// from http://developer.android.com/guide/topics/appwidgets/index.html
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	if( OpDebug.LOG )
    		Log.d(TAG, "onUpdate");
    	if( OpDebug.LOG )
    		Log.d(TAG, "length = " + appWidgetIds.length);

        // Perform this loop procedure for each App Widget that belongs to this provider
		for(int appWidgetId : appWidgetIds) {
        	if( OpDebug.LOG )
        		Log.d(TAG, "appWidgetId: " + appWidgetId);

            PendingIntent pendingIntent;
            // for now, always put up the keyguard if the device is PIN locked etc
			/*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			if( sharedPreferences.getBoolean(OpMainActivity.getShowWhenLockedPreferenceKey(), true) ) {
		    	if( OpDebug.LOG )
		    		Log.d(TAG, "do show above lock screen");
	            Intent intent = new Intent(context, OpWidgetProvider.class);
	            intent.setAction("net.sourceforge.opencamera.LAUNCH_OPEN_CAMERA");
	            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			}
			else*/ {
		    	/*if( OpDebug.LOG )
		    		Log.d(TAG, "don't show above lock screen");*/
	            Intent intent = new Intent(context, OpMainActivity.class);
	            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			}

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.opwidget_layout);
            views.setOnClickPendingIntent(R.id.widget_launch_open_camera, pendingIntent);
			/*if( sharedPreferences.getBoolean(OpMainActivity.getShowWhenLockedPreferenceKey(), true) ) {
				views.setTextViewText(R.id.launch_open_camera, "Open Camera (unlocked)");
			}
			else {
				views.setTextViewText(R.id.launch_open_camera, "Open Camera (locked)");
			}*/

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /*@Override
	public void onReceive(Context context, Intent intent) {
    	if( OpDebug.LOG ) {
    		Log.d(TAG, "onReceive " + intent);
    	}
	    if (intent.getAction().equals("net.sourceforge.opencamera.LAUNCH_OPEN_CAMERA")) {
	    	if( OpDebug.LOG )
	    		Log.d(TAG, "Launching OpMainActivity");
	        final Intent activity = new Intent(context, OpMainActivity.class);
	        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(activity);
	    	if( OpDebug.LOG )
	    		Log.d(TAG, "done");
	    }
	    super.onReceive(context, intent);
	}*/
}
