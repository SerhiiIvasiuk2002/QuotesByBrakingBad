package com.botquotes.botquotesbybreakingbad.util;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TranslatorUtil {
    private static final String GOOGLE_TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single";
    private static final String TARGET_LANGUAGE = "uk";
    private final Logger logger;

    public String translate(String text) {
        try {
            return translate("auto", TARGET_LANGUAGE, text);
        } catch (IOException e) {
            logger.warn("cannot translate {}", text);
        }
        return text;
    }

    public String translate(String sourceLanguage,
                            String targetLanguage,
                            String text) throws IOException {
        String urlText = generateUrl(sourceLanguage, targetLanguage, text);
        URL url = new URL(urlText);
        String rawData = urlToText(url);
        if (rawData == null) {
            return null;
        }
        String[] raw = rawData.split("\"");
        if (raw.length < 2) {
            return null;
        }
        JSONArray arr = new JSONArray(rawData);
        JSONArray arr1 = arr.getJSONArray(0);
        StringBuilder trans = new StringBuilder();
        for (int i = 0; i < arr1.length(); i++) {
            JSONArray arr2 = arr1.getJSONArray(i);
            trans.append(arr2.get(0).toString());
        }
        return trans.toString();
    }

    private String generateUrl(String sourceLanguage,
                               String targetLanguage,
                               String text) throws UnsupportedEncodingException {
        String encoded = URLEncoder.encode(text, "UTF-8");
        StringBuilder sb = new StringBuilder();
        sb.append(GOOGLE_TRANSLATE_URL);
        sb.append("?client=webapp");
        sb.append("&hl=en");
        sb.append("&sl=");
        sb.append(sourceLanguage);
        sb.append("&tl=");
        sb.append(targetLanguage);
        sb.append("&q=");
        sb.append(encoded);
        sb.append("&multires=1");
        sb.append("&otf=0");
        sb.append("&pc=0");
        sb.append("&trs=1");
        sb.append("&ssel=0");
        sb.append("&tsel=0");
        sb.append("&kc=1");
        sb.append("&dt=t");
        sb.append("&ie=UTF-8");
        sb.append("&oe=UTF-8");
        sb.append("&tk=");
        sb.append(generateToken(text));
        return sb.toString();
    }

    private String generateToken(String text) {
        int[] tkk = tkk();
        int b = tkk[0];
        int e = 0;
        int f = 0;
        List<Integer> d = new ArrayList<Integer>();
        for (; f < text.length(); f++) {
            int g = text.charAt(f);
            if (0x80 > g) {
                d.add(e++, g);
            } else {
                if (0x800 > g) {
                    d.add(e++, g >> 6 | 0xC0);
                } else {
                    if (0xD800 == (g & 0xFC00)
                            && f + 1 < text.length()
                            && 0xDC00 == (text.charAt(f + 1) & 0xFC00)) {
                        g = 0x10000 + ((g & 0x3FF) << 10) + (text.charAt(++f) & 0x3FF);
                        d.add(e++, g >> 18 | 0xF0);
                        d.add(e++, g >> 12 & 0x3F | 0x80);
                    } else {
                        d.add(e++, g >> 12 | 0xE0);
                        d.add(e++, g >> 6 & 0x3F | 0x80);
                    }
                }
                d.add(e++, g & 63 | 128);
            }
        }

        int ai = b;
        for (e = 0; e < d.size(); e++) {
            ai += d.get(e);
            ai = rl(ai, "+-a^+6");
        }
        ai = rl(ai, "+-3^+b+-f");
        ai ^= tkk[1];
        long al;
        if (0 > ai) {
            al = 0x80000000L + (ai & 0x7FFFFFFF);
        } else {
            al = ai;
        }
        al %= Math.pow(10, 6);
        return String.format(Locale.US, "%d.%d", al, al ^ b);
    }

    private int rl(int a, String b) {
        for (int c = 0; c < b.length() - 2; c += 3) {
            int d = b.charAt(c + 2);
            d = d >= 65 ? d - 87 : d - 48;
            d = b.charAt(c + 1) == '+' ? shr32(a, d) : (a << d);
            a = b.charAt(c) == '+' ? (a + (d & 0xFFFFFFFF)) : a ^ d;
        }
        return a;
    }

    private int shr32(int x, int bits) {
        if (x < 0) {
            long xl = 0xffffffffL + x + 1;
            return (int) (xl >> bits);
        }
        return x >> bits;
    }

    private int[] tkk() {
        return new int[]{ 0x6337E, 0x217A58DC + 0x5AF91132 };
    }

    private String urlToText(URL url) throws IOException {
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0");
        Reader r = new java.io.InputStreamReader(urlConn.getInputStream(),
                Charset.forName("UTF-8"));
        StringBuilder buf = new StringBuilder();
        while (true) {
            int ch = r.read();
            if (ch < 0) {
                break;
            }
            buf.append((char) ch);
        }
        return buf.toString();
    }
}
