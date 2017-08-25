package com.shark.pdfedit.utils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HttpUrlConnectUtil {
    public static int TIMEOUT = 2 * 1000; // 超时时间
    public static final String CHARSET = "utf-8"; // 设置编码

    private static final String SERVLET_POST = "POST";
    private static final String SERVLET_GET = "GET";
    private static final String SERVLET_DELETE = "DELETE";
    private static final String SERVLET_PUT = "PUT";

    /**
     * 检查url
     *
     * @param orgurl
     * @return
     */
    private static String checkUrl(String orgurl) {
        String url = orgurl.split("\\?")[0];
        return url;
    }

    /**
     * 检查map
     *
     * @param orgmap
     * @param orgurl
     * @return
     */
    private static Map<String, Object> checkMap(Map<String, Object> orgmap, String orgurl) {
        if (orgmap == null) {
            orgmap = new HashMap<String, Object>();
        }
        String[] array = orgurl.split("\\?");
        if (array.length > 1) {
            String split = array[1];
            String[] splits = split.split("&");
            for (int i = 0; i < splits.length; i++) {
                orgmap.put(splits[i].split("=")[0],splits[i].split("=").length<2?"":splits[i].split("=")[1]);
            }
        } else {

        }
        for (String key : orgmap.keySet()) {
            if (orgmap.get(key) == null) {
                orgmap.put(key, "");
            }

        }
        return orgmap;
    }

    /**
     * 获得返回
     *
     * @param urlConn
     * @return
     * @throws IOException
     */
    private static String getRespone(HttpURLConnection urlConn) throws IOException {
        InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
        StringBuilder jsonResults = new StringBuilder();
        int read;
        char[] buff = new char[512];
        while ((read = in.read(buff)) != -1) {
            jsonResults.append(buff, 0, read);
        }
        in.close();
        return jsonResults.toString();
    }

    /**
     * 获得返回的文件
     *
     * @param urlConn
     * @param outputstring
     * @return
     * @throws IOException
     */
    private static String getResponeFile(HttpURLConnection urlConn, String outputstring) throws IOException {
        BufferedInputStream bin = new BufferedInputStream(urlConn.getInputStream());

        String path = outputstring;
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        OutputStream out = new FileOutputStream(file);
        int size = 0;
        int len = 0;
        byte[] buf = new byte[1024];
        while ((size = bin.read(buf)) != -1) {
            len += size;
            out.write(buf, 0, size);
        }
        bin.close();
        out.close();
        return outputstring;
    }

    /**
     * 准备参数
     *
     * @param paramMap
     * @return
     */
    private static String prepareParam(Map<String, Object> paramMap) {
        StringBuffer sb = new StringBuffer();
        if (paramMap.isEmpty()) {
            return "";
        } else {
            for (String key : paramMap.keySet()) {
                if (paramMap.get(key) != null) {
                    String value = (String) paramMap.get(key);
                    if (sb.length() < 1) {
                        sb.append(key).append("=").append(value);
                    } else {
                        sb.append("&").append(key).append("=").append(value);
                    }
                }

            }
            return sb.toString();
        }
    }


    public static String doGet(String urlString, String cookie) throws Exception {
        return doGet(urlString, null, cookie);
    }

    public synchronized static String doPost(String urlStr, Map<String, Object> paramMap, String cookie) throws Exception {
        paramMap = checkMap(paramMap, urlStr);
        urlStr = checkUrl(urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        if (!"".equals(cookie)) {
            conn.setRequestProperty("Cookie", "PHPSESSID=" + cookie);
            conn.setRequestProperty("Cookie", "JSESSIONID=" + cookie);

        }
        conn.setRequestMethod(SERVLET_POST);

        String paramStr = prepareParam(paramMap);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(paramStr.toString().getBytes("utf-8"));
        os.close();

        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            result = getRespone(conn);
        }
        System.out.println(result);
        return result;

    }

    public synchronized static String doGet(String urlStr, Map<String, Object> paramMap, String cookie) throws Exception {
        paramMap = checkMap(paramMap, urlStr);
        urlStr = checkUrl(urlStr);
        String paramStr = prepareParam(paramMap);
        if (paramStr == null || paramStr.trim().length() < 1) {

        } else {
            urlStr += "?" + paramStr;
        }

        System.out.println(urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        if (!"".equals(cookie)) {
            conn.setRequestProperty("Cookie", "PHPSESSID=" + cookie);
            conn.setRequestProperty("Cookie", "JSESSIONID=" + cookie);

        }
        conn.setRequestMethod(SERVLET_PUT);
        conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");

        conn.connect();
        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            result = getRespone(conn);
        }
        System.out.println(result);
        return result;
    }

    public synchronized static String doPut(String urlStr, Map<String, Object> paramMap, String cookie) throws Exception {
        paramMap = checkMap(paramMap, urlStr);
        urlStr = checkUrl(urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        if (!"".equals(cookie)) {
            conn.setRequestProperty("Cookie", "PHPSESSID=" + cookie);
            conn.setRequestProperty("Cookie", "JSESSIONID=" + cookie);

        }
        conn.setRequestMethod(SERVLET_PUT);
        String paramStr = prepareParam(paramMap);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.write(paramStr.toString().getBytes("utf-8"));
        os.close();
        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            result = getRespone(conn);
        }
        System.out.println(result);
        return result;

    }

    public synchronized static String doDelete(String urlStr, Map<String, Object> paramMap, String cookie) throws Exception {
        paramMap = checkMap(paramMap, urlStr);
        urlStr = checkUrl(urlStr);
        String paramStr = prepareParam(paramMap);
        if (paramStr == null || paramStr.trim().length() < 1) {

        } else {
            urlStr += "?" + paramStr;
        }
        System.out.println(urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        if (!"".equals(cookie)) {
            conn.setRequestProperty("Cookie", "PHPSESSID=" + cookie);
            conn.setRequestProperty("Cookie", "JSESSIONID=" + cookie);

        }
        conn.setDoOutput(true);
        conn.setRequestMethod(SERVLET_DELETE);

        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

            result = getRespone(conn);
        }
        System.out.println(result);
        return result;
    }

    public synchronized static String downloadFile(String urlStr, Map<String, Object> paramMap, String cookie, String outputstring) throws IOException {
        paramMap = checkMap(paramMap, urlStr);
        urlStr = checkUrl(urlStr);
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        if (!"".equals(cookie)) {
            conn.setRequestProperty("Cookie", "PHPSESSID=" + cookie);
            conn.setRequestProperty("Cookie", "JSESSIONID=" + cookie);

        }
        conn.setRequestMethod(SERVLET_POST);

        String paramStr = prepareParam(paramMap);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(paramStr.toString().getBytes("utf-8"));
        os.close();
        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            result = getResponeFile(conn, outputstring);
        }
        System.out.println(result);
        return result;
    }

    public synchronized static String uploadFile(String urlStr, Map<String, Object> paramMap, String cookie) throws IOException {
        paramMap = checkMap(paramMap, urlStr);
        urlStr = checkUrl(urlStr);
        String boundry = UUID.randomUUID().toString(); // 边界标识 随机生成
        String prefix = "--", end = "\r\n";
        String contentType = "multipart/form-data"; // 内容类型
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        conn.setDoInput(true); // 允许输入流
        conn.setDoOutput(true); // 允许输出流
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setChunkedStreamingMode(0);//启用没有进行内部缓存的http请求正文流
        conn.setRequestMethod("POST"); // 请求方式
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Cookie", "PHPSESSID=" + cookie);
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Content-Type", contentType + ";boundary=" + boundry);
        /**
         * 当文件不为空，把文件包装并且上传
         */
        DataOutputStream dos = new DataOutputStream(
                conn.getOutputStream());
        StringBuilder sb = new StringBuilder();
        for (String key : paramMap.keySet()) {
            if (paramMap.get(key) != null) {
                if (paramMap.get(key) instanceof File) {
                    File file = (File) paramMap.get(key);
                    sb.append(prefix);
                    sb.append(boundry);
                    sb.append(end);
                    sb.append("Content-Disposition: form-data; name=\"Filename\""
                            + end);
                    sb.append(end);
                    sb.append(file.getName() + end);

                    sb.append(prefix);
                    sb.append(boundry);
                    sb.append(end);
                    sb.append("Content-Disposition: form-data; name=\"Filedata\"; filename=\""
                            + file.getName() + "\"" + end);
                    sb.append("Content-Type: application/octet-stream"
                            + end);
                    sb.append(end);
                    dos.write(sb.toString().getBytes());
                    sb.delete(0, sb.length());
                    FileInputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.write(end.getBytes());

                } else {
                    sb.append(prefix);
                    sb.append(boundry);
                    sb.append(end);
                    sb.append("Content-Disposition: form-data; name=\"" + key + "\""
                            + end);
                    sb.append(end);
                    sb.append(paramMap.get(key).toString() + end);
                    dos.write(sb.toString().getBytes());
                    sb.delete(0, sb.length());
                }
            }

        }
        byte[] endData = (prefix + boundry + prefix + end).getBytes();
        dos.write(endData);
        dos.flush();
        String result = "";
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            result = getRespone(conn);
        }
        System.out.println(result);
        return result;

    }

}
