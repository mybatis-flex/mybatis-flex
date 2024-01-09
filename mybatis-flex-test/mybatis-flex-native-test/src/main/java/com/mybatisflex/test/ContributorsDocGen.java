package com.mybatisflex.test;

import com.mybatisflex.core.audit.http.HttpUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributorsDocGen {

    public static void main(String[] args) throws Exception {

        //请先配置 cookie 后再运行
        String cookie = "";

        List<String> urls = Arrays.asList(
            "https://gitee.com/mybatis-flex/mybatis-flex/contributors?ref=main"
            , "https://gitee.com/mybatis-flex/mybatis-flex/contributors?page=2&ref=main"
            , "https://gitee.com/mybatis-flex/mybatis-flex/contributors?page=3&ref=main"
        );
        StringBuilder markdown = new StringBuilder();
        markdown.append("|     |     |     |     |     |\n" +
            "|-----|-----|-----|-----|-----|\n");

        int startIndex = 0;
        for (String url : urls) {
            startIndex = getMdContent(markdown, url, cookie, startIndex);
        }
        if (startIndex != 0) {
            markdown.append("|\n");
        }

        System.out.println(markdown);


        String mdDir = System.getProperty("user.dir") + "/docs/zh/intro/parts/";
        writeString(new File(mdDir, "contributors.md"), markdown);
    }

    public static void writeString(File file, StringBuilder markdown) throws IOException {
        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(markdown.toString());
            bw.newLine();
        }
    }


    private static int getMdContent(StringBuilder markdown, String url, String cookie, int startIndex) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        String html = get(url, headers);
        Document document = Jsoup.parse(html);
        Elements userListItems = document.getElementsByClass("user-list-item");

        for (Element userListItem : userListItems) {
            Element img = userListItem.selectFirst("img");
            String src = img.attr("src");
            String userName = userListItem.selectFirst(".username").text();
            if (userName.contains("@")) {
                userName = userName.substring(0, userName.indexOf("@"));
            }
//            if (StringUtil.isBlank(src)) {
//                src = "https://api.dicebear.com/7.x/initials/svg?seed=" + userName;
//            }

            markdown.append("|");

            if (StringUtil.isNotBlank(src)) {
                markdown.append("![](" + src + ")");
            }

            markdown.append(userName);

            startIndex++;

            if (startIndex == 5) {
                markdown.append("|\n");
                startIndex = 0;
            }
        }

        return startIndex;
    }


    public static String get(String url, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpConnection(url, headers);
            conn.connect();

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

    private static String readString(HttpURLConnection conn) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
            StringBuilder ret = new StringBuilder();
            char[] buf = new char[1024];
            for (int num; (num = isr.read(buf, 0, buf.length)) != -1; ) {
                ret.append(buf, 0, num);
            }
            return ret.toString();
        }
    }


    private static HttpURLConnection getHttpConnection(String url, Map<String, String> headers) throws IOException {
        URL _url = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(createSSLSocketFactory());
            ((HttpsURLConnection) conn).setHostnameVerifier((s, sslSession) -> true);
        }

        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        return conn;
    }

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

    private static SSLSocketFactory createSSLSocketFactory() {
        try {
            TrustManager[] tm = {new TrustAnyTrustManager()};
            SSLContext sslContext = SSLContext.getInstance("TLS");    // "TLS", "SunJSSE"
            sslContext.init(null, tm, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
