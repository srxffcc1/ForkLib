package com.example.fragmentuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void changeFragment(View view){
        getFragmentManager().beginTransaction().addToBackStack("null").add(android.R.id.content,new MyFragment(),"ss").commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("SRXRESULT",requestCode+":"+resultCode);
        ((Button)findViewById(R.id.settime)).setText("回来了");
    }
}
