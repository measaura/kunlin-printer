package cn.westlan.coding.util;

import android.text.TextUtils;
import cn.westlan.coding.exception.HttpException;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;

public class HttpUtil {

    static {
        trustAllHosts();
    }

    private static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws CertificateException {
            }
        } };
        HostnameVerifier TrustAllHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(TrustAllHostnameVerifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 从网络获取json数据,(String byte[})

     * @param path
     * @return
     */
    public static InputStream get(String path) throws IOException {
        URL url = new URL(path);
        //打开连接
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
//        urlConnection.setRequestProperty("Content-type", "application/json");
        urlConnection.setConnectTimeout(10_000);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        if(200 != responseCode) {
            throw new HttpException(responseCode, urlConnection.getResponseMessage());
        }
        //得到输入流
        return urlConnection.getInputStream();
    }



    public static String readStr(InputStream in, String charset) throws IOException {
        if (TextUtils.isEmpty(charset)) charset = "UTF-8";

        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        Reader reader = new InputStreamReader(in, charset);
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf)) >= 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}