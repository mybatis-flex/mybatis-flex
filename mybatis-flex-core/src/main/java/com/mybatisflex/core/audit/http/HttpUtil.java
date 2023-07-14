/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.core.audit.http;

import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.logging.LogFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http 工具类。
 */
public class HttpUtil {

    private HttpUtil() {
    }

    private static final String POST = "POST";

    private static String CHARSET = "UTF-8";
    private static int connectTimeout = 15000;    // 连接超时，单位毫秒
    private static int readTimeout = 15000;        // 读取超时，单位毫秒

    private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();

    private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new TrustAnyHostnameVerifier();

    public static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces(); net.hasMoreElements(); ) {
                NetworkInterface networkInterface = net.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                for (Enumeration<InetAddress> addrs = networkInterface.getInetAddresses(); addrs.hasMoreElements(); ) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "127.0.0.1";
    }

    /**
     * https 域名校验
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

    }

    /**
     * https 证书管理
     */
    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

    }


    private static SSLSocketFactory initSSLSocketFactory() {
        try {
            TrustManager[] tm = {new HttpUtil.TrustAnyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS");    // "TLS", "SunJSSE"
            sslContext.init(null, tm, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCharSet(String charSet) {
        if (StringUtil.isBlank(charSet)) {
            throw new IllegalArgumentException("charSet can not be blank.");
        }
        HttpUtil.CHARSET = charSet;
    }

    public static void setConnectTimeout(int connectTimeout) {
        HttpUtil.connectTimeout = connectTimeout;
    }

    public static void setReadTimeout(int readTimeout) {
        HttpUtil.readTimeout = readTimeout;
    }

    private static HttpURLConnection getHttpConnection(String url, String method, Map<String, String> headers) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        URL _url = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
        }

        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        if (headers != null && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn;
    }


    /**
     * Send POST request
     */
    public static String post(String url, Map<String, String> queryParas, String data, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpConnection(buildUrlQuery(url, queryParas), POST, headers);
            conn.connect();

            if (data != null) {
                try (OutputStream out = conn.getOutputStream()) {
                    out.write(data.getBytes(CHARSET));
                    out.flush();
                }
            }
            return readString(conn);
        } catch (IOException e) {
            LogFactory.getLog(HttpUtil.class).error("post error.", e);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String post(String url, Map<String, String> queryParas, String data) {
        return post(url, queryParas, data, null);
    }

    public static String post(String url, String data, Map<String, String> headers) {
        return post(url, null, data, headers);
    }

    public static String post(String url, String data) {
        return post(url, null, data, null);
    }

    private static String readString(HttpURLConnection conn) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), CHARSET)) {
            StringBuilder ret = new StringBuilder();
            char[] buf = new char[1024];
            for (int num; (num = isr.read(buf, 0, buf.length)) != -1; ) {
                ret.append(buf, 0, num);
            }
            return ret.toString();
        }
    }


    private static String buildUrlQuery(String url, Map<String, String> queryParas) {
        if (queryParas == null || queryParas.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url);
        boolean isFirst;
        if (url.indexOf('?') == -1) {
            isFirst = true;
            sb.append('?');
        } else {
            isFirst = false;
        }

        for (Entry<String, String> entry : queryParas.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append('&');
            }

            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtil.isNotBlank(value)) {
                try {
                    value = URLEncoder.encode(value, CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            sb.append(key).append('=').append(value);
        }
        return sb.toString();
    }


}






