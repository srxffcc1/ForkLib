package com.shark.pdfedit.sample;

import android.app.Activity;
import android.os.Bundle;

import com.shark.pdfedit.fragment.BookFragment;
import com.wisdomregulation.data.entitybase.Base_Entity;
import com.wisdomregulation.data.entitybook2017.Entity_Book_2017_0;

public class Sample extends Activity {

    private BookFragment bookFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Base_Entity base_entity=new Entity_Book_2017_0();
        bookFragment = BookFragment.getInstance2017(base_entity,BookFragment.TYPE_ADD);
        getFragmentManager().beginTransaction().replace(1, bookFragment).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if(bookFragment.onBackPressed()){
            return;
        }else{
            super.onBackPressed();
        }
    }
}
