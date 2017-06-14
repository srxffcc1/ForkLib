package com.artifex.mupdf.mini;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class LibraryActivity extends ListActivity {
    protected final int UPDATE_DELAY = 5000;
    protected ArrayAdapter<Item> adapter;
    protected File currentDirectory;
    protected File topDirectory;
    protected Timer updateTimer;

    protected static class Item {
        public File file;
        public String string;

        public Item(File file) {
            this.file = file;
            if (file.isDirectory()) {
                this.string = file.getName() + "/";
            } else {
                this.string = file.getName();
            }
        }

        public Item(File file, String string) {
            this.file = file;
            this.string = string;
        }

        public String toString() {
            return this.string;
        }
    }

    protected boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state) || "mounted_ro".equals(state)) {
            return true;
        }
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        this.topDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        this.currentDirectory = this.topDirectory;
        this.adapter = new ArrayAdapter(this, 17367043);
        setListAdapter(this.adapter);
    }

    public void onResume() {
        super.onResume();
        TimerTask updateTask = new TimerTask() {
            public void run() {
                LibraryActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        LibraryActivity.this.updateFileList();
                    }
                });
            }
        };
        this.updateTimer = new Timer();
        this.updateTimer.scheduleAtFixedRate(updateTask, 0, 5000);
    }

    public void onPause() {
        super.onPause();
        this.updateTimer.cancel();
        this.updateTimer = null;
    }

    protected void updateFileList() {
        this.adapter.clear();
        if (!isExternalStorageReadable()) {
            setTitle(R.string.app_name);
            this.adapter.add(new Item(this.topDirectory, getString(R.string.library_no_external_storage)));
        } else if (this.currentDirectory.isDirectory()) {
            String curPath = this.currentDirectory.getAbsolutePath();
            String topPath = this.topDirectory.getParentFile().getAbsolutePath();
            if (curPath.startsWith(topPath)) {
                curPath = curPath.substring(topPath.length() + 1);
            }
            setTitle(curPath + "/");
            File parent = this.currentDirectory.getParentFile();
            if (!(parent == null || this.currentDirectory.equals(this.topDirectory))) {
                this.adapter.add(new Item(parent, "../"));
            }
            File[] files = this.currentDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    if (file.isDirectory()) {
                        return true;
                    }
                    String suffix = file.getName().toLowerCase();
                    if (suffix.endsWith(".pdf") || suffix.endsWith(".xps") || suffix.endsWith(".cbz") || suffix.endsWith(".epub") || suffix.endsWith(".fb2")) {
                        return true;
                    }
                    return false;
                }
            });
            if (files == null) {
                this.adapter.add(new Item(this.topDirectory, getString(R.string.library_permission_denied)));
            } else {
                for (File file : files) {
                    this.adapter.add(new Item(file));
                }
            }
            this.adapter.sort(new Comparator<Item>() {
                public int compare(Item a, Item b) {
                    boolean ad = a.file.isDirectory();
                    boolean bd = b.file.isDirectory();
                    if (ad && !bd) {
                        return -1;
                    }
                    if (bd && !ad) {
                        return 1;
                    }
                    if (a.string.equals("../")) {
                        return -1;
                    }
                    if (b.string.equals("../")) {
                        return 1;
                    }
                    return a.string.compareTo(b.string);
                }
            });
        } else {
            setTitle(R.string.app_name);
            this.adapter.add(new Item(this.topDirectory, getString(R.string.library_not_a_directory)));
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Item item = (Item) this.adapter.getItem(position);
        if (item.file.isDirectory()) {
            this.currentDirectory = item.file;
            updateFileList();
        } else if (item.file.isFile()) {
            Intent intent = new Intent(this, DocumentActivity.class);
            intent.addFlags(524288);
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.fromFile(item.file));
            startActivity(intent);
        }
    }
}
