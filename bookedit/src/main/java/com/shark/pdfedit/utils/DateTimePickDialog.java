package com.shark.pdfedit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.shark.pdfedit.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           show=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           show.show(inputDate);
 * 
 *           } });
 * 
 * @author
 */
public class DateTimePickDialog implements OnDateChangedListener,
		OnTimeChangedListener,OnDateSetListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private int syear;
	private int smonth;
	private int sday;
	private int shour;
	private int sminute;
	private int oldhour=0;
	private ModeTimeEnu TimeMode= ModeTimeEnu.NoCange;
	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialog(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	private void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		Calendar minCalendar = Calendar.getInstance();
		Calendar maxCalendar = Calendar.getInstance();
		if(TimeMode== ModeTimeEnu.Before){
//			minCalendar.set(Calendar.DAY_OF_MONTH, minCalendar.get(Calendar.DAY_OF_MONTH));
//			maxCalendar.add(Calendar.YEAR, 2);
			long time = new Date().getTime();
//			datePicker.setMinDate(minCalendar.getTimeInMillis()-1000);//添加范围的最小值
			datePicker.setMaxDate(maxCalendar.getTimeInMillis()-1000);//添加范围的最大值
		}else if(TimeMode== ModeTimeEnu.After){
			minCalendar.set(Calendar.DAY_OF_MONTH, minCalendar.get(Calendar.DAY_OF_MONTH));
			maxCalendar.add(Calendar.YEAR, 2);
			long time = new Date().getTime();
			datePicker.setMinDate(minCalendar.getTimeInMillis()-1000);//添加范围的最小值
			datePicker.setMaxDate(maxCalendar.getTimeInMillis()-1000);//添加范围的最大值
		}else{
			
		}

		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "- "
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}
		syear=calendar.get(Calendar.YEAR);
		smonth=calendar.get(Calendar.MONTH);
		sday=calendar.get(Calendar.DAY_OF_MONTH);
		shour=calendar.get(Calendar.HOUR_OF_DAY);
		sminute=calendar.get(Calendar.MINUTE);
		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
//		try {//反射
//			Field hourSpinnerField = timePicker.getClass().getDeclaredField("mHourSpinner");
//			hourSpinnerField.setAccessible(true);
//			NumberPicker hourSpinner = (NumberPicker) hourSpinnerField.get(timePicker);
//			hourSpinner.setMinValue(10);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
	}
	public DateTimePickDialog setTimeMode(ModeTimeEnu mode){
		this.TimeMode=mode;
		return this;
	}
	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	
	@SuppressLint("NewApi")
	public AlertDialog show(final View inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.time_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);  
		timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);  
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity)
				.setTitle("今日"+initDateTime)
				.setView(dateTimeLayout)
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Calendar calendar = Calendar.getInstance();

						calendar.set(datePicker.getYear(), datePicker.getMonth(),
								datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
								timePicker.getCurrentMinute());
						

						dateTime = sdf.format(calendar.getTime());
						ad.setTitle("今日"+dateTime);
						
						((TextView)inputDate).setText(dateTime);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dateTime=sdf.format(new Date());
						((TextView)inputDate).setText(dateTime);
					}
				})

				.show();

		onDateChanged(null, 0, 0, 0);
		return ad;
	}
	private boolean isDateAfter() {
		boolean isdateafter=true;
		String nowdate=datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();
		String clientdate=syear+"-"+smonth+"-"+sday;
		if(clientdate.equals(nowdate)){
			isdateafter=false;
		}else{
			isdateafter=true;
		}
		//System.out.println("是否当前日期后"+isdateafter);
		return isdateafter;
    }

	private boolean isDateBefore() {
		boolean isdateafter=true;
		String nowdate=datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();
		String clientdate=syear+"-"+smonth+"-"+sday;
		if(clientdate.equals(nowdate)){
			isdateafter=false;
		}else{
			isdateafter=true;
		}
		//System.out.println("是否当前日期前"+isdateafter);
		return isdateafter;
    }

	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		

		dateTime = sdf.format(calendar.getTime());
		ad.setTitle("今日"+dateTime);
		
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		
		
		
		//时间联动有点麻烦 不写了
//		try {
//			Field hourSpinnerField = timePicker.getClass().getDeclaredField("mHourSpinner");
//			hourSpinnerField.setAccessible(true);
//			NumberPicker hourSpinner = (NumberPicker) hourSpinnerField.get(timePicker);
//			int nowhour=hourSpinner.getValue();
//			
//			if(oldhour==hourSpinner.getMinValue()){
//				if(isDateAfter()){
//					if(nowhour==hourSpinner.getMaxValue()){
//						
//						//System.out.println("减一天");
//						try {//反射
//							Field daySpinnerField = datePicker.getClass().getDeclaredField("mDaySpinner");
//							daySpinnerField.setAccessible(true);
//							NumberPicker daySpinner = (NumberPicker) daySpinnerField.get(datePicker);
//							daySpinner.setValue(daySpinner.getValue()-1);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}else{
//						
//					}
//				}
//
//			}else if(oldhour==hourSpinner.getMaxValue()){
//				if(nowhour==hourSpinner.getMinValue()){
//					//System.out.println("加一天");
//					try {//反射
//						Field daySpinnerField = datePicker.getClass().getDeclaredField("mDaySpinner");
//						daySpinnerField.setAccessible(true);
//						NumberPicker daySpinner = (NumberPicker) daySpinnerField.get(datePicker);
//						daySpinner.setValue(daySpinner.getValue()+1);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}else{
//					
//				}
//				
//			}
//			oldhour=nowhour;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		onDateChanged(null, 0, 0, 0);
	}
	
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());


		dateTime = sdf.format(calendar.getTime());
		ad.setTitle("今日"+dateTime);
		if(TimeMode== ModeTimeEnu.Before){
			if(isDateBefore()){
			changeTimeTmplete(0, 23,0,59);
			
		}else{
			if(timePicker.getCurrentHour()==shour){
				
				changeTimeTmplete(0, shour,0,sminute);
			}else{
				
				changeTimeTmplete(0, shour,0,59);
			}
			
		}
		}else if(TimeMode== ModeTimeEnu.After){
			if(isDateAfter()){
				changeTimeTmplete(0, 23,0,59);
				
			}else{
				if(timePicker.getCurrentHour()==shour){
					
					changeTimeTmplete(shour, 23,sminute,59);
				}else{
					
					changeTimeTmplete(shour, 23,0,59);
				}
				
			}
		}else{
			
		}


	}
	private void changeTimeTmplete(int minHour,int maxHour,int minMinu,int maxMinu){
		try {//反射
			Field hourSpinnerField = timePicker.getClass().getDeclaredField("mHourSpinner");
			hourSpinnerField.setAccessible(true);
			NumberPicker hourSpinner = (NumberPicker) hourSpinnerField.get(timePicker);
			hourSpinner.setMinValue(minHour);
			hourSpinner.setMaxValue(maxHour);
			Field minuSpinnerField = timePicker.getClass().getDeclaredField("mMinuteSpinner");
			minuSpinnerField.setAccessible(true);
			NumberPicker minuSpinner = (NumberPicker) minuSpinnerField.get(timePicker);
			minuSpinner.setMinValue(minMinu);
			minuSpinner.setMaxValue(maxMinu);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "-", "index", "front"); // 日期
		String time = spliteString(initDateTime, "-", "index", "back"); // 时间

		String yearStr = spliteString(date, "-", "index", "front"); // 年份
		String monthAndDay = spliteString(date, "-", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "-", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "-", "index", "back"); // 日

		String hourStr = spliteString(time, ":", "index", "front"); // 时
		String minuteStr = spliteString(time, ":", "index", "back"); // 分

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();
		int currentHour = Integer.valueOf(hourStr.trim()).intValue();
		int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay, currentHour,
				currentMinute);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	private  String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}
	public enum ModeTimeEnu{
		NoCange,Before,After;
	}

}
