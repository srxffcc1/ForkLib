package com.shark.pdfedit.utils;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by King6rf on 2017/7/6.
 */

public class HandlerHelp {
    private static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.v("DebugHandler","分配:"+msg.what+"");
            HandlerListener listener=handlerListenerMap.get(msg.what);
            if(listener!=null){
//                Log.v("DebugHandler","开始:"+msg.what+"");
                listener.hand(msg);
            }
        }
    };
    private static Map<Integer,HandlerListener> handlerListenerMap=new HashMap<>();

    public interface HandlerListener {
        void hand(Message msg);
    }
    private static final HandlerHelp instance=new HandlerHelp();
    private HandlerHelp(){
    }
    public static HandlerHelp instance(){
        return instance;
    }
    public HandlerHelp sendMessage(Message msg){
        handler.sendMessage(msg);
        return instance;
    }
    public HandlerHelp sendEmptyMessage(int what){
        handler.sendEmptyMessage(what);
        return instance;
    }
    public HandlerHelp addListener(int key, HandlerListener value){
//        Log.v("DebugHandler","注册:"+key);
        handlerListenerMap.put(key,value);
        return instance;
    }
    public HandlerHelp removeListener(int... key){
//        Log.v("DebugHandler","移除:"+key);
        for (int i : key) {
            handlerListenerMap.remove(i);
        }

        return instance;
    }
}
