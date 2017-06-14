/*
 *
 * Copyright (c) 2010-2014 EVE GROUP PTE. LTD.
 *
 */


package veg.mediaplayer.sdk.testmulti2;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import veg.mediaplayer.sdk.testmulti2.Player.PlayerCallback;

public class MainActivity extends AppCompatActivity implements PlayerCallback
{
    private static final String TAG 	 = "MediaPlayerTest";
    
	public  static AutoCompleteTextView	edtIpAddress;
	public  static ArrayAdapter<String> edtIpAddressAdapter;
	public  static Set<String>			edtIpAddressHistory;
	private Button						btnConnect;
	private Button						btnHistory;
	
	private SharedPreferences 			settings;
    private SharedPreferences.Editor 	editor;

    private boolean 					playing = false;
    private MainActivity 				mthis = null;

	private ArrayList<Player> 			players = null;
	private Player			 			curr_player = null;
//	private Button						btnF1;
//	private Button						btnF2;
//	private Button						btnF3;
//	private Button						btnF4;
	//private LinearLayout 				playerLayoutList = null;
	//private ScrollView					mainScrollView = null;
    
	private String						currentUrl = "";
    private MulticastLock 				multicastLock = null;
    
    private static final String[]	 	urls = 
	{  
    	"rtsp://admin:12345@124.152.9.144:20041/PSIA/streaming/channels/301",
    	"rtsp://flash3.ipercast.net:554/m6boutique.com-live/mp4:m6boutiquecom.stream_high",
    	"http://tv.life.ru/lifetv/480p/index.m3u8",
	};
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
		String  strUrl;

		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);

		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifi.createMulticastLock("multicastLock");
		multicastLock.setReferenceCounted(true);
		multicastLock.acquire();
		
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		
		setContentView(R.layout.main);
		mthis = this;
		
		settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

		SharedSettings.getInstance(this).loadPrefSettings();
		SharedSettings.getInstance().savePrefSettings();
		
		players = new ArrayList<Player>();
		for (int i = 0; i < 4; i++)
		{
			Player player = new Player();
			player.callback = this;
			players.add(player);
		}

		strUrl = settings.getString("connectionUrl", "http://yoyo-v-out.oss-cn-hangzhou.aliyuncs.com/012e47d2eb1e49a7bd1a27e6c0f37d9f/act-m3u8-segment/022a79ae-9bc7-7e1c-3926-e9c87b360f81.mp4");
		
		HashSet<String> tempHistory = new HashSet<String>();
		tempHistory.add("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
		tempHistory.add("rtmp://184.72.239.149/vod/mp4:bigbuckbunny_450.mp4");
		tempHistory.add("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov");
		tempHistory.add("rtsp://rtmp.infomaniak.ch/livecast/latele");
			
		edtIpAddressHistory = settings.getStringSet("connectionHistory", tempHistory);

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	
		edtIpAddress = (AutoCompleteTextView)findViewById(R.id.edit_ipaddress);
		edtIpAddress.setText(strUrl);

		edtIpAddress.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId,	KeyEvent event) 
			{
				if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) 
				{
					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					in.hideSoftInputFromWindow(edtIpAddress.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					return true;
	
				}
				return false;
			}
		});

		btnHistory = (Button)findViewById(R.id.button_history);

		// Array of choices
		btnHistory.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(MainActivity.edtIpAddress.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				
				if (edtIpAddressHistory.size() <= 0)
					return;

				MainActivity.edtIpAddressAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.history_item, new ArrayList<String>(edtIpAddressHistory));
				MainActivity.edtIpAddress.setAdapter(MainActivity.edtIpAddressAdapter);
				MainActivity.edtIpAddress.showDropDown();
			}   
		});
		
//		btnF1 = (Button)findViewById(R.id.button_f1);
//		btnF1.setOnClickListener(new View.OnClickListener()
//		{
//			public void onClick(View v)
//	    	{
//				curr_player.Close();
//
//		        FragmentManager fm = getFragmentManager();
//		        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//		        fragmentTransaction.replace(R.id.fragment_place, players.get(0));
//		        fragmentTransaction.commit();
//
//		        curr_player = players.get(0);
//	        }
//		});
//
//		btnF2 = (Button)findViewById(R.id.button_f2);
//		btnF2.setOnClickListener(new View.OnClickListener()
//		{
//			public void onClick(View v)
//	    	{
//				curr_player.Close();
//
//		        FragmentManager fm = getFragmentManager();
//		        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//		        fragmentTransaction.replace(R.id.fragment_place, players.get(1));
//		        fragmentTransaction.commit();
//
//		        curr_player = players.get(1);
//	        }
//		});
//
//		btnF3 = (Button)findViewById(R.id.button_f3);
//		btnF3.setOnClickListener(new View.OnClickListener()
//		{
//			public void onClick(View v)
//	    	{
//				curr_player.Close();
//
//		        FragmentManager fm = getFragmentManager();
//		        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//		        fragmentTransaction.replace(R.id.fragment_place, players.get(2));
//		        fragmentTransaction.commit();
//
//		        curr_player = players.get(2);
//	        }
//		});
//
//		btnF4 = (Button)findViewById(R.id.button_f4);
//		btnF4.setOnClickListener(new View.OnClickListener()
//		{
//			public void onClick(View v)
//	    	{
//				curr_player.Close();
//
//		        FragmentManager fm = getFragmentManager();
//		        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//		        fragmentTransaction.replace(R.id.fragment_place, players.get(3));
//		        fragmentTransaction.commit();
//
//		        curr_player = players.get(3);
//	        }
//		});
        
		btnConnect = (Button)findViewById(R.id.button_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
	    	{
	    		String url  = edtIpAddress.getText().toString();
	    		if (url.isEmpty())
	    			return;
	
	    		if (!edtIpAddressHistory.contains(url))
	    			edtIpAddressHistory.add(url);
	    		
	    		SharedSettings.getInstance().loadPrefSettings();
	
	    		currentUrl = url;
	    		playerUpdate();            
	        }
		});
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.main_view);
        layout.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getWindow() != null && getWindow().getCurrentFocus() != null && getWindow().getCurrentFocus().getWindowToken() != null)
					inputManager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				return true;
			}
		});
        
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, players.get(0));
        fragmentTransaction.commit();
        curr_player = players.get(0);
        
		setShowControls();
        
    }

    private void playerUpdateInFragment()
    {
		if (playing)
		{
			curr_player.Open(currentUrl);
		}
		else
		{
			curr_player.Close();
		}
		
		updateFragmentsButtons();
    }
    
    private void playerUpdate()
    {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!playing)
				{
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							curr_player.Open(currentUrl);
							btnConnect.setText("Disconnect");
						}
					});
					playing = true;
				}
				else
				{
					curr_player.Close();
					setUIDisconnected();
				}
			}
		}).start();

    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged(newConfig);
    }

    protected void onPause()
	{
		Log.e("SDL", "onPause()");
		super.onPause();
		new Thread(new Runnable() {
			@Override
			public void run() {
						{
			curr_player.Close();
			setUIDisconnected();
		}
			}
		}).start();

		editor = settings.edit();
		editor.putString("connectionUrl", edtIpAddress.getText().toString());

		editor.putStringSet("connectionHistory", edtIpAddressHistory);
		editor.commit();
	}

	@Override
    public void onBackPressed() 
    {

	    super.onBackPressed();
    }

  	@Override
  	protected void onDestroy() 
  	{
  		Log.e("SDL", "onDestroy()");


		super.onDestroy();
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (multicastLock != null) {
					multicastLock.release();
					multicastLock = null;
				}
			}
		}).start();
   	}	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)  
	{
		switch (item.getItemId())  
		{
			case R.id.main_opt_settings:   
		
				SharedSettings.getInstance().loadPrefSettings();

				Intent intentSettings = new Intent(MainActivity.this, PreferencesActivity.class);     
				startActivity(intentSettings);

				break;
			case R.id.main_opt_clearhistory:     
			
				new AlertDialog.Builder(this)
				.setTitle("Clear History")
				.setMessage("Do you really want to delete the history?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						HashSet<String> tempHistory = new HashSet<String>(); 
						tempHistory.add("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov");
						tempHistory.add("rtp://224.0.0.10:20000/");
						tempHistory.add("http://tv.life.ru/lifetv/480p/index.m3u8");
						edtIpAddressHistory.clear();
						edtIpAddressHistory = tempHistory;  
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						// do nothing
					}
				}).show();
				break;
			case R.id.main_opt_about:     
				AboutDialog about = new AboutDialog(this);  
				about.show();
				break;
			case R.id.main_opt_exit:     
				finish();
				break;

		}
		return true;
	}

	protected void updateFragmentsButtons()
	{
//		btnF1.setPressed(curr_player == players.get(0));
//		btnF2.setPressed(curr_player == players.get(1));
//		btnF3.setPressed(curr_player == players.get(2));
//		btnF4.setPressed(curr_player == players.get(3));
	}
	
	protected void setUIDisconnected()
	{

		try {
			if(!isFinishing()){
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTitle(R.string.app_name);
						btnConnect.setText("Connect");
					}
				});
			}

		} catch (Exception e) {

		}

		playing = false;
	}

	protected void setHideControls()
	{
		ActionBar actionBar = getActionBar();
		actionBar.hide(); // slides out

		edtIpAddress.setVisibility(View.GONE);
		btnHistory.setVisibility(View.GONE);
		btnConnect.setVisibility(View.GONE);
	}

	protected void setShowControls()
	{

		try {
			ActionBar actionBar = getActionBar();
			actionBar.show(); // slides out
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle(R.string.app_name);
		
		edtIpAddress.setVisibility(View.VISIBLE);
		btnHistory.setVisibility(View.VISIBLE);
		btnConnect.setVisibility(View.VISIBLE);
		
//		mainScrollView.post(new Runnable() {            
//		    @Override
//		    public void run() {
//		    	mainScrollView.fullScroll(View.FOCUS_DOWN);              
//		    }
//		});
		
	}

	@Override
	public void ViewReady() 
	{
        playerUpdateInFragment();           
	}
	
}
