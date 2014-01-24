package com.platforms.utils;

/**
 * Created by AGalkin on 9/26/13.
 */

import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class JsonParser {
    public JSONArray parseArray(String endPoint){
        JSONArray root = null;
        InputStream is = null;
        Log.v("pofta", "endPoint Array" + endPoint);
        try {
            JsonParser JP = new JsonParser();
            is = JP.getHttpGETInputStream(endPoint);
            String tmp = JP.getHttpInputString(is);
            root = new JSONArray(tmp);

        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return root;
    }

    public JSONObject parseObject(String endPoint){
        JSONObject root = null;
        InputStream is = null;
        Log.v("pofta", "endPoint Object"+		endPoint);
        try {
            JsonParser JP = new JsonParser();
            is = JP.getHttpGETInputStream(endPoint);
            String tmp = JP.getHttpInputString(is);
            root = new JSONObject(tmp);

        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return root;
    }
    public InputStream getHttpGETInputStream(String urlStr)	throws Exception {
        InputStream is = null;
        URLConnection conn = null;

        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }


        if (urlStr.startsWith("https://")) {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        }

        try {
            URL url = new URL(urlStr);
            conn =  url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);

            is = conn.getInputStream();

            return is;
        } catch (Exception ex) {
            try {
                is.close();
            } catch (Exception ignore) {
            }
            try {
                if(conn instanceof HttpURLConnection)
                    ((HttpURLConnection)conn).disconnect();
            } catch (Exception ignore) {
            }

            throw ex;

        }
    }
    public String getHttpInputString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
