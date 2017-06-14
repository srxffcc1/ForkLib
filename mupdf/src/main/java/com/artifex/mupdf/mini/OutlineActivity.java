package com.artifex.mupdf.mini;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.Serializable;
import java.util.ArrayList;

public class OutlineActivity extends ListActivity {
    protected ArrayAdapter<Item> adapter;

    public static class Item implements Serializable {
        public int page;
        public String title;

        public Item(String title, int page) {
            this.title = title;
            this.page = page;
        }

        public String toString() {
            return this.title;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().addFlags(1024);
        this.adapter = new ArrayAdapter(this, 17367043);
        setListAdapter(this.adapter);
        Bundle bundle = getIntent().getExtras();
        int currentPage = bundle.getInt("POSITION");
        ArrayList<Item> outline = (ArrayList) bundle.getSerializable("OUTLINE");
        int found = -1;
        for (int i = 0; i < outline.size(); i++) {
            Item item = (Item) outline.get(i);
            if (found < 0 && item.page >= currentPage) {
                found = i;
            }
            this.adapter.add(item);
        }
        if (found >= 0) {
            setSelection(found);
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        setResult(((Item) this.adapter.getItem(position)).page + 1);
        finish();
    }
}
