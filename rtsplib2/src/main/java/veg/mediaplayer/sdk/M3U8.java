package veg.mediaplayer.sdk;

import android.os.AsyncTask;
import android.os.Build.VERSION;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3U8 {
    private static final String TAG = "M3U8_Parser";
    private static DefaultHttpClient clientHTTP = null;
    private static DefaultHttpClient clientHTTPS = null;
    List<HLSStream> hls_list = new ArrayList();

    class DownloadDataTask extends AsyncTask<String, Integer, String> {
        DownloadDataTask() {
        }

        protected void onPreExecute() {
        }

        protected String doInBackground(String... urls) {
            downloadData(urls);
            return "";
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
        }

        private int downloadData(String... urls) {
            Exception e;
            if (urls[0].startsWith("file://") || urls[0].startsWith("/storage")) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(new File(urls[0])));
                    try {
                        M3U8.this.M3U8_Parse(br);
                        return 0;
                    } catch (Exception e2) {
                        e = (Exception) e2;
                        BufferedReader bufferedReader = br;
                        e.printStackTrace();
                        return -1;
                    }
                } catch (IOException e3) {
                    e = e3;
                    e.printStackTrace();
                    return -1;
                }
            }
            try {
                String ret = "";
                HttpEntity entity = M3U8.getThreadSafeClient(urls[0].startsWith("https")).execute(new HttpGet(urls[0])).getEntity();
                InputStream is = entity.getContent();
                long length = entity.getContentLength();
                M3U8.this.M3U8_Parse(new BufferedReader(new InputStreamReader(new DataInputStream(is))));
                return 0;
            } catch (Exception e4) {
                e4.printStackTrace();
                return -1;
            }
        }
    }

    public class HLSStream {
        public String BANDWIDTH = "";
        public String CODECS = "";
        public String ID = "";
        public String RESOLUTION = "";
        public String URL = "";
        public String WIDTH;
        public int ext_stream;
        public boolean worked = true;
    }

    public static class HLSStreamComparator implements Comparator<HLSStream> {
        int nOrder = 0;

        public HLSStreamComparator(int order) {
            this.nOrder = order;
        }

        public int compare(HLSStream left, HLSStream right) {
            int ret = 1;
            try {
                if (Integer.parseInt(left.BANDWIDTH) - Integer.parseInt(right.BANDWIDTH) > 0) {
                    ret = -1;
                }
                return ret;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 1;
            }
        }
    }

    public static synchronized DefaultHttpClient getThreadSafeClient(boolean is_https) {
        DefaultHttpClient defaultHttpClient;
        synchronized (M3U8.class) {
            HttpParams httpParameters;
            SchemeRegistry registry;
            if (is_https) {
                if (clientHTTPS == null) {
                    httpParameters = new BasicHttpParams();
                    registry = new SchemeRegistry();
                    SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
                    sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    registry.register(new Scheme("https", sslSocketFactory, 443));
                    clientHTTPS = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry), httpParameters);
                }
                HttpConnectionParams.setSoTimeout(clientHTTPS.getParams(), 30000);
                HttpConnectionParams.setConnectionTimeout(clientHTTPS.getParams(), 30000);
                HttpConnectionParams.setSocketBufferSize(clientHTTPS.getParams(), 524288);
                defaultHttpClient = clientHTTPS;
            } else {
                if (clientHTTP == null) {
                    httpParameters = new BasicHttpParams();
                    registry = new SchemeRegistry();
                    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                    clientHTTP = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParameters, registry), httpParameters);
                }
                HttpConnectionParams.setSoTimeout(clientHTTP.getParams(), 30000);
                HttpConnectionParams.setConnectionTimeout(clientHTTP.getParams(), 30000);
                HttpConnectionParams.setSocketBufferSize(clientHTTP.getParams(), 524288);
                defaultHttpClient = clientHTTP;
            }
        }
        return defaultHttpClient;
    }

    public String getDataAndParse(String url) {
        try {
            String response = (String) new DownloadDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{url}).get();
            if (response == null || response.isEmpty()) {
                return "";
            }
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "";
        } catch (ExecutionException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    private int M3U8_Parse(BufferedReader br) {
        String ret = "";
        try {
            if (!br.readLine().trim().toUpperCase().contains("#EXTM3U")) {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String title = "";
        int pos = 0;
        while (true) {
            String strLine = null;
            try {
                strLine = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (strLine == null) {
                break;
            } else if (1 == 1) {
                if (strLine.startsWith("#EXT-X-STREAM-INF")) {
                    Pattern pattern1 = Pattern.compile("^#EXT-X-STREAM-INF:.*BANDWIDTH=(\\d+).*");
                    Pattern pattern2 = Pattern.compile("^#EXT-X-STREAM-INF:.*RESOLUTION=([\\dx]+).*");
                    Pattern pattern3 = Pattern.compile("^#EXT-X-STREAM-INF:.*PROGRAM-ID=(\\d+).*");
                    Pattern pattern4 = Pattern.compile("^#EXT-X-STREAM-INF:.*CODECS=\"(.*)\".*");
                    HLSStream stream = new HLSStream();
                    Matcher matcher = pattern1.matcher(strLine);
                    if (matcher.find()) {
                        stream.BANDWIDTH = matcher.group(1);
                    }
                    matcher = pattern2.matcher(strLine);
                    if (matcher.find()) {
                        stream.RESOLUTION = matcher.group(1);
                        int idx = stream.RESOLUTION.indexOf("x");
                        if (idx != -1) {
                            stream.WIDTH = stream.RESOLUTION.substring(0, idx);
                        } else {
                            stream.WIDTH = stream.RESOLUTION;
                        }
                    }
                    matcher = pattern3.matcher(strLine);
                    if (matcher.find()) {
                        stream.ID = matcher.group(1);
                    }
                    matcher = pattern4.matcher(strLine);
                    if (matcher.find()) {
                        stream.CODECS = matcher.group(1);
                    }
                    try {
                        strLine = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (strLine == null) {
                        break;
                    }
                    try {
                        stream.URL = strLine;
                        stream.ext_stream = pos;
                        pos++;
                        this.hls_list.add(stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (strLine.startsWith("# ")) {
                }
            }
        }
        Collections.sort(this.hls_list, new HLSStreamComparator(1));
        return 0;
    }

    public List<HLSStream> getChannelList() {
        return this.hls_list;
    }

    public int getDataSynchroAndParse(String url) {
        boolean z;
        if (url == null || !url.startsWith("https")) {
            z = false;
        } else {
            z = true;
        }
        try {
            HttpEntity entity = getThreadSafeClient(z).execute(new HttpGet(url)).getEntity();
            InputStream is = entity.getContent();
            long length = entity.getContentLength();
            M3U8_Parse(new BufferedReader(new InputStreamReader(new DataInputStream(is))));
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static <T> AsyncTask<T, ?, ?> executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) {
        if (VERSION.SDK_INT >= 11) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
        return task.execute(params);
    }
}
