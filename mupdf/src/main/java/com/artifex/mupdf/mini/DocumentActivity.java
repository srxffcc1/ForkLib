package com.artifex.mupdf.mini;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.artifex.mupdf.fitz.Device;
import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Outline;
import com.artifex.mupdf.fitz.Page;
import com.artifex.mupdf.fitz.android.AndroidDrawDevice;
import com.artifex.mupdf.mini.OutlineActivity.Item;
import com.artifex.mupdf.mini.Worker.Task;
import java.util.ArrayList;
import java.util.Stack;

public class DocumentActivity extends Activity {
    private final String APP = "MuPDF";
    public final int NAVIGATE_REQUEST = 1;
    protected View actionBar;
    protected int canvasH;
    protected int canvasW;
    protected int currentPage;
    protected float displayDPI;
    protected Document doc;
    protected ArrayList<Item> flatOutline;
    protected boolean hasLoaded;
    protected Stack<Integer> history;
    protected boolean isReflowable;
    protected View layoutButton;
    protected float layoutEm;
    protected float layoutH;
    protected PopupMenu layoutPopupMenu;
    protected float layoutW;
    protected View navigationBar;
    protected View outlineButton;
    protected int pageCount;
    protected TextView pageLabel;
    protected SeekBar pageSeekbar;
    protected PageView pageView;
    protected String path;
    protected SharedPreferences prefs;
    protected String title;
    protected TextView titleLabel;
    protected boolean wentBack;
    protected Worker worker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().addFlags(1024);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.displayDPI = (float) metrics.densityDpi;
        setContentView(R.layout.document_activity);
        this.actionBar = findViewById(R.id.action_bar);
        this.navigationBar = findViewById(R.id.navigation_bar);
        this.path = getIntent().getData().getPath();
        this.title = this.path.substring(this.path.lastIndexOf(47) + 1);
        this.titleLabel = (TextView) findViewById(R.id.title_label);
        this.titleLabel.setText(this.title);
        this.history = new Stack();
        this.worker = new Worker(this);
        this.worker.start();
        this.prefs = getPreferences(0);
        this.layoutEm = this.prefs.getFloat("layoutEm", 8.0f);
        this.currentPage = this.prefs.getInt(this.path, 0);
        this.hasLoaded = false;
        this.pageView = (PageView) findViewById(R.id.page_view);
        this.pageView.setActionListener(this);
        this.pageLabel = (TextView) findViewById(R.id.page_label);
        this.pageSeekbar = (SeekBar) findViewById(R.id.page_seekbar);
        this.pageSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public int newProgress = -1;

            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (fromUser) {
                    this.newProgress = progress;
                    DocumentActivity.this.pageLabel.setText((progress + 1) + " / " + DocumentActivity.this.pageCount);
                }
            }

            public void onStartTrackingTouch(SeekBar seekbar) {
            }

            public void onStopTrackingTouch(SeekBar seekbar) {
                DocumentActivity.this.gotoPage(this.newProgress);
            }
        });
        this.outlineButton = findViewById(R.id.outline_button);
        this.outlineButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DocumentActivity.this, OutlineActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("POSITION", DocumentActivity.this.currentPage);
                bundle.putSerializable("OUTLINE", DocumentActivity.this.flatOutline);
                intent.putExtras(bundle);
                DocumentActivity.this.startActivityForResult(intent, 1);
            }
        });
        this.layoutButton = findViewById(R.id.layout_button);
        this.layoutPopupMenu = new PopupMenu(this, this.layoutButton);
        this.layoutPopupMenu.getMenuInflater().inflate(R.menu.layout_menu, this.layoutPopupMenu.getMenu());
        this.layoutPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                float oldLayoutEm = DocumentActivity.this.layoutEm;
                int i = item.getItemId();
                if (i == R.id.action_layout_6pt) {
                    DocumentActivity.this.layoutEm = 6.0f;

                } else if (i == R.id.action_layout_7pt) {
                    DocumentActivity.this.layoutEm = 7.0f;

                } else if (i == R.id.action_layout_8pt) {
                    DocumentActivity.this.layoutEm = 8.0f;

                } else if (i == R.id.action_layout_9pt) {
                    DocumentActivity.this.layoutEm = 9.0f;

                } else if (i == R.id.action_layout_10pt) {
                    DocumentActivity.this.layoutEm = 10.0f;

                } else if (i == R.id.action_layout_11pt) {
                    DocumentActivity.this.layoutEm = 11.0f;

                } else if (i == R.id.action_layout_12pt) {
                    DocumentActivity.this.layoutEm = 12.0f;

                } else if (i == R.id.action_layout_13pt) {
                    DocumentActivity.this.layoutEm = 13.0f;

                } else if (i == R.id.action_layout_14pt) {
                    DocumentActivity.this.layoutEm = 14.0f;

                } else if (i == R.id.action_layout_15pt) {
                    DocumentActivity.this.layoutEm = 15.0f;

                } else if (i == R.id.action_layout_16pt) {
                    DocumentActivity.this.layoutEm = 16.0f;

                }
                if (oldLayoutEm != DocumentActivity.this.layoutEm) {
                    DocumentActivity.this.relayoutDocument();
                }
                return true;
            }
        });
        this.layoutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DocumentActivity.this.layoutPopupMenu.show();
            }
        });
    }

    public void onPageViewSizeChanged(int w, int h) {
        this.canvasW = w;
        this.canvasH = h;
        this.layoutW = ((float) (this.canvasW * 72)) / this.displayDPI;
        this.layoutH = ((float) (this.canvasH * 72)) / this.displayDPI;
        if (!this.hasLoaded) {
            this.hasLoaded = true;
            openDocument();
        } else if (this.isReflowable) {
            relayoutDocument();
        } else {
            loadPage();
        }
    }

    protected void openDocument() {
        this.worker.add(new Task() {
            boolean needsPassword;

            public void work() {
                Log.i("MuPDF", "open document");
                DocumentActivity.this.doc = Document.openDocument(DocumentActivity.this.path);
                this.needsPassword = DocumentActivity.this.doc.needsPassword();
            }

            public void run() {
                if (this.needsPassword) {
                    DocumentActivity.this.askPassword(R.string.dlog_password_message);
                } else {
                    DocumentActivity.this.loadDocument();
                }
            }
        });
    }

    protected void askPassword(int message) {
        final EditText passwordView = new EditText(this);
        passwordView.setInputType(Device.FLAG_ENDCAP_UNDEFINED);
        passwordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Builder builder = new Builder(this);
        builder.setTitle(R.string.dlog_password_title);
        builder.setMessage(message);
        builder.setView(passwordView);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DocumentActivity.this.checkPassword(passwordView.getText().toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DocumentActivity.this.finish();
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                DocumentActivity.this.finish();
            }
        });
        builder.create().show();
    }

    protected void checkPassword(final String password) {
        this.worker.add(new Task() {
            boolean passwordOkay;

            public void work() {
                Log.i("MuPDF", "check password");
                this.passwordOkay = DocumentActivity.this.doc.authenticatePassword(password);
            }

            public void run() {
                if (this.passwordOkay) {
                    DocumentActivity.this.loadDocument();
                } else {
                    DocumentActivity.this.askPassword(R.string.dlog_password_retry);
                }
            }
        });
    }

    public void onPause() {
        super.onPause();
        Editor editor = this.prefs.edit();
        editor.putFloat("layoutEm", this.layoutEm);
        editor.putInt(this.path, this.currentPage);
        editor.commit();
    }

//    public void onBackPressed() {
//        if (this.history.empty()) {
//            super.onBackPressed();
//            return;
//        }
//        this.currentPage = ((Integer) this.history.pop()).intValue();
//        loadPage();
//    }

    public void onActivityResult(int request, int result, Intent data) {
        if (request == 1 && result >= 1) {
            gotoPage(result - 1);
        }
    }

    protected void loadDocument() {
        this.worker.add(new Task() {
            public void work() {
                try {
                    Log.i("MuPDF", "load document");
                    String metaTitle = DocumentActivity.this.doc.getMetaData(Document.META_INFO_TITLE);
                    if (metaTitle != null) {
                        DocumentActivity.this.title = metaTitle;
                    }
                    DocumentActivity.this.isReflowable = DocumentActivity.this.doc.isReflowable();
                    if (DocumentActivity.this.isReflowable) {
                        Log.i("MuPDF", "layout document");
                        DocumentActivity.this.doc.layout(DocumentActivity.this.layoutW, DocumentActivity.this.layoutH, DocumentActivity.this.layoutEm);
                    }
                    DocumentActivity.this.pageCount = DocumentActivity.this.doc.countPages();
                } catch (Throwable x) {
                    Log.e("MuPDF", x.getMessage());
                    DocumentActivity.this.doc = null;
                    DocumentActivity.this.pageCount = 1;
                    DocumentActivity.this.currentPage = 0;
                }
            }

            public void run() {
                if (DocumentActivity.this.currentPage < 0 || DocumentActivity.this.currentPage >= DocumentActivity.this.pageCount) {
                    DocumentActivity.this.currentPage = 0;
                }
                DocumentActivity.this.titleLabel.setText(DocumentActivity.this.title);
                if (DocumentActivity.this.isReflowable) {
                    DocumentActivity.this.layoutButton.setVisibility(0);
                }
                DocumentActivity.this.loadPage();
                DocumentActivity.this.loadOutline();
            }
        });
    }

    protected void relayoutDocument() {
        this.worker.add(new Task() {
            public void work() {
                try {
                    long mark = DocumentActivity.this.doc.makeBookmark(DocumentActivity.this.currentPage);
                    Log.i("MuPDF", "relayout document");
                    DocumentActivity.this.doc.layout(DocumentActivity.this.layoutW, DocumentActivity.this.layoutH, DocumentActivity.this.layoutEm);
                    DocumentActivity.this.pageCount = DocumentActivity.this.doc.countPages();
                    DocumentActivity.this.currentPage = DocumentActivity.this.doc.findBookmark(mark);
                } catch (Throwable x) {
                    Log.e("MuPDF", x.getMessage());
                    DocumentActivity.this.pageCount = 1;
                    DocumentActivity.this.currentPage = 0;
                }
            }

            public void run() {
                DocumentActivity.this.loadPage();
                DocumentActivity.this.loadOutline();
            }
        });
    }

    private void loadOutline() {
        this.worker.add(new Task() {
            private void flattenOutline(Outline[] outline, String indent) {
                for (Outline node : outline) {
                    if (node.title != null) {
                        DocumentActivity.this.flatOutline.add(new Item(indent + node.title, node.page));
                    }
                    if (node.down != null) {
                        flattenOutline(node.down, indent + "    ");
                    }
                }
            }

            public void work() {
                Log.i("MuPDF", "load outline");
                Outline[] outline = DocumentActivity.this.doc.loadOutline();
                if (outline != null) {
                    DocumentActivity.this.flatOutline = new ArrayList();
                    flattenOutline(outline, "");
                    return;
                }
                DocumentActivity.this.flatOutline = null;
            }

            public void run() {
                if (DocumentActivity.this.flatOutline != null) {
                    DocumentActivity.this.outlineButton.setVisibility(0);
                }
            }
        });
    }

    protected void loadPage() {
        final int pageNumber = this.currentPage;
        this.worker.add(new Task() {
            public Bitmap bitmap;
            public Link[] links;

            public void work() {
                try {
                    Log.i("MuPDF", "load page " + pageNumber);
                    Page page = DocumentActivity.this.doc.loadPage(pageNumber);
                    Log.i("MuPDF", "draw page " + pageNumber);
                    Matrix ctm = AndroidDrawDevice.fitPageWidth(page, DocumentActivity.this.canvasW);
                    this.bitmap = AndroidDrawDevice.drawPage(page, ctm);
                    this.links = page.getLinks();
                    if (this.links != null) {
                        for (Link link : this.links) {
                            link.bounds.transform(ctm);
                        }
                    }
                } catch (Throwable x) {
                    Log.e("MuPDF", x.getMessage());
                }
            }

            public void run() {
                if (this.bitmap != null) {
                    DocumentActivity.this.pageView.setBitmap(this.bitmap, DocumentActivity.this.wentBack, this.links);
                } else {
                    DocumentActivity.this.pageView.setError();
                }
                DocumentActivity.this.pageLabel.setText((DocumentActivity.this.currentPage + 1) + " / " + DocumentActivity.this.pageCount);
                DocumentActivity.this.pageSeekbar.setMax(DocumentActivity.this.pageCount - 1);
                DocumentActivity.this.pageSeekbar.setProgress(pageNumber);
                DocumentActivity.this.wentBack = false;
            }
        });
    }

    public void toggleUI() {
        if (this.actionBar.getVisibility() == 0) {
            this.actionBar.setVisibility(8);
            this.navigationBar.setVisibility(8);
            return;
        }
        this.actionBar.setVisibility(0);
        this.navigationBar.setVisibility(0);
    }

    public void goBackward() {
        if (this.currentPage > 0) {
            this.wentBack = true;
            this.currentPage--;
            loadPage();
        }
    }

    public void goForward() {
        if (this.currentPage < this.pageCount - 1) {
            this.currentPage++;
            loadPage();
        }
    }

    public void gotoPage(int p) {
        if (p >= 0 && p < this.pageCount && p != this.currentPage) {
            this.history.push(Integer.valueOf(this.currentPage));
            this.currentPage = p;
            loadPage();
        }
    }

    public void gotoURI(String uri) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(uri)));
        } catch (Throwable x) {
            Log.e("MuPDF", x.getMessage());
        }
    }
}
