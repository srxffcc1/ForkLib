package com.shark.pdfedit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shark.pdfedit.R;
import com.shark.pdfedit.utils.TextColorUtil;
import com.wisdomregulation.data.entitybase.Base_Entity;

public class BookTipDetailBuilder {
    private Activity context;
    private Base_Entity detailMapData;
    private LinearLayout content;

    private boolean editState = false;

    public BookTipDetailBuilder(Activity context,
                                Base_Entity detailMapData, LinearLayout content) {
        super();
        this.context = context;
        this.detailMapData = detailMapData;
        this.content = content;
    }


    public BookTipDetailBuilder build() {

        content.removeAllViews();
        for (int i = 0; i < getCount(); i++) {
            View add = getView(i, content);
            if (add != null) {
                if (add.getVisibility() == View.VISIBLE) {
                    content.addView(add);
                }

            }
        }
        return this;
    }


    public int getCount() {
        // TODO Auto-generated method stub
        return detailMapData.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return detailMapData.getFieldChinese(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    public View getView(int position, ViewGroup parent) {
        View convertView = null;
        String fieldtext = getItem(position).toString();
        convertView = LayoutInflater.from(context).inflate(R.layout.item_book_tipitem, null);
        ((TextView) convertView.findViewById(R.id.bookcontentName2)).setText(fieldtext);
        final EditText valueedit = ((EditText) convertView.findViewById(R.id.bookcontentValue2));

        valueedit.setText(detailMapData.getValue(position));


        if (editState) {
//			valueedit.setEnabled(true);
        } else {
            valueedit.setOnClickListener(null);
            valueedit.setOnTouchListener(null);
            valueedit.setClickable(false);
            valueedit.setKeyListener(null);
        }
        TextColorUtil.fixTextColor(convertView);
        return convertView;
    }


}
