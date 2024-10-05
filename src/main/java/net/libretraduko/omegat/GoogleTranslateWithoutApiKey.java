package net.libretraduko.omegat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
// import org.apache.commons.lang.StringUtils;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IApplicationEventListener;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.core.machinetranslators.BaseCachedTranslate;
import org.omegat.core.machinetranslators.MachineTranslators;
import org.omegat.util.Language;
import org.omegat.util.PatternConsts;
import org.omegat.util.Log;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class GoogleTranslateWithoutApiKey extends BaseCachedTranslate {
    // private static final Logger logger = LoggerFactory.getLogger(GoogleTranslateWithoutApiKey.class);
    protected static final String[] GT_URLS = {
        "https://translate.googleapis.com/translate_a/t?client=gtx",
        "https://translate.google.com/translate_a/t?client=dict-chrome-ex" };
//     protected static final String MARK_BEG = "{\"trans\":\"";
//     protected static final String MARK_END = "\",\"orig\":\"";
    protected static final Pattern RE_UNICODE = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
    protected static final Pattern RE_HTML = Pattern.compile("&#([0-9]+);");
    private static String[] USER_AGENTS = {
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:25.0) Gecko/20100101 Firefox/25.0",
        "Mozilla/5.0 (Windows NT 6.1; rv:28.0) Gecko/20100101 Firefox/28.0",
        "Mozilla/5.0 (X11; Linux i686; rv:30.0) Gecko/20100101 Firefox/30.0",
        "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0",
        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0",
        "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:58.0) Gecko/20100101 Firefox/58.0",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.36 Safari/535.7",
        "Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.66 Safari/535.11",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.45 Safari/535.19",
        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24",
        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1",
        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.15 (KHTML, like Gecko) Chrome/24.0.1295.0 Safari/537.15",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1467.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1623.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.103 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36",
        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)",
        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
        "Mozilla/5.0 (IE 11.0; Windows NT 6.3; Trident/7.0; .NET4.0E; .NET4.0C; rv:11.0) like Gecko",
        "Mozilla/5.0 (IE 11.0; Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko" };

    private Map<String, String> params = new HashMap<String, String>();
    {
        params.put("dt", "t");
        params.put("dj", "1");
    }
    
    private Random rnd = new Random();
    private Map<String, String> headers = new HashMap<>();
    {
        headers.put("Accept", "text/html,application/xhtml+xm…ml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "BL_D_PROV=; BL_T_PROV=Google");
        headers.put("Host", "translate.googleapis.com");
        headers.put("Referer", "https://translate.google.com/");
        headers.put("TE", "Trailers");
        headers.put("Upgrade-Insecure-Requests", "1");
    }

    private Set<String> failures = new HashSet<>();

    protected static final ResourceBundle res = ResourceBundle.getBundle("GoogleTranslateWithoutApiKey", Locale.getDefault());

       // Plugin setup
    public static void loadPlugins() {
        CoreEvents.registerApplicationEventListener(new IApplicationEventListener() {
            @Override
            public void onApplicationStartup() {
                MachineTranslators.add(new GoogleTranslateWithoutApiKey());
            }

            @Override
            public void onApplicationShutdown() {
                /* empty */
            }
        });
    }

    public static void unloadPlugins() {
        /* empty */
    }
    
    public String getName() {
        return res.getString("GoogleTranslateNoAPI");
    }

    @Override
    protected String getPreferenceName() {
        return "allow_google_translate_without_api_key";
    }
    
    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String trText = text.length() > 5000 ? text.substring(0, 4997) + "..." : text;
        String prev = getFromCache(sLang, tLang, trText);
        if (prev != null) {
            return prev;
        }
	
        Log.logErrorRB("trText={}", trText);
        String targetLang = tLang.getLanguageCode();
        // Differentiate in target between simplified and traditional Chinese
        if ((tLang.getLanguage().compareToIgnoreCase("zh-cn") == 0)
            || (tLang.getLanguage().compareToIgnoreCase("zh-tw") == 0))
            targetLang = tLang.getLanguage();
        else if ((tLang.getLanguage().compareToIgnoreCase("zh-hk") == 0))
            targetLang = "ZH-TW"; // Google doesn't recognize ZH-HK
        
        params.put("sl", sLang.getLanguageCode());
        params.put("tl", targetLang);
        params.put("q", trText);
        
        headers.put("User-Agent", USER_AGENTS[rnd.nextInt(USER_AGENTS.length)]);
        
        byte[] answer = null;
        List<String> urls = new ArrayList<>(Arrays.asList(GT_URLS));
        while (failures.size() != GT_URLS.length && answer == null) {
            String url = urls.get(rnd.nextInt(urls.size()));
            try {
                Log.logErrorRB("url={}", url);
                answer = getURLasByteArray(url, params, headers);
            } catch (IOException e) {
                Log.logErrorRB("Exception {} with url={}", e.getMessage(), url);
                failures.add(url);
                urls.remove(url);
            }
        };
        if (failures.size() == GT_URLS.length) {
            return null;
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(answer));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            outStr.append(line);
        }
        String v = outStr.toString();
        Log.logErrorRB("outStr={}", v);
        while (true) {
            Matcher m = RE_UNICODE.matcher(v);
            if (!m.find()) {
                break;
            }
            String g = m.group();
            char c = (char) Integer.parseInt(m.group(1), 16);
            v = v.replace(g, Character.toString(c));
        }
        v = v.replace("\\n", "\n");
        v = v.replace("\\\"", "\"");
        while (true) {
            Matcher m = RE_HTML.matcher(v);
            if (!m.find()) {
                break;
            }
            String g = m.group();
            char c = (char) Integer.parseInt(m.group(1));
            v = v.replace(g, Character.toString(c));
        }
        
        List<String> items = new ArrayList<>();
        v = v.substring(2,v.length());
        v = v.substring(0,v.length()-2);
        items.add(v);
//         int beg = -1, end = -1;
//         do {
//             beg = StringUtils.indexOf(v, MARK_BEG, end) + MARK_BEG.length();
//             end = StringUtils.indexOf(v, MARK_END, beg);
//             logger.debug("beg={}, end={}", beg, end);
//             String tr = v.substring(beg, end);
//             items.add(tr);
//         } while (beg != StringUtils.lastIndexOf(v, MARK_BEG) + MARK_BEG.length());
        
        List<String> resultItems = new ArrayList<>();
        for (String tr : items) {
            String newTr = tr;
            Matcher tag = PatternConsts.OMEGAT_TAG_SPACE.matcher(newTr);
            while (tag.find()) {
                String searchTag = tag.group();
                if (text.indexOf(searchTag) == -1) {
                    String replacement = searchTag.substring(0, searchTag.length() - 1);
                    newTr = newTr.replace(searchTag, replacement);
                }
            }
            
            tag = PatternConsts.SPACE_OMEGAT_TAG.matcher(newTr);
            while (tag.find()) {
                String searchTag = tag.group();
                if (text.indexOf(searchTag) == -1) {
                    String replacement = searchTag.substring(1, searchTag.length());
                    newTr = newTr.replace(searchTag, replacement);
                }
            }
            resultItems.add(newTr);
        }
        String result = String.join("", resultItems);
        putToCache(sLang, tLang, trText, result);
        return result;
    }
    
    public static byte[] getURLasByteArray(String address, Map<String, String> params,
                                           Map<String, String> additionalHeaders) throws IOException {
        StringBuilder s = new StringBuilder(address);
        boolean next = false;
        if (!address.contains("?")) {
            s.append('?');
        } else {
            next = true;
        }
        
        for (Map.Entry<String, String> p : params.entrySet()) {
            if (next) {
                s.append('&');
            } else {
                next = true;
            }
            s.append(p.getKey());
            s.append('=');
            s.append(URLEncoder.encode(p.getValue(), StandardCharsets.UTF_8.name()));
        }
        String url = s.toString();
        
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        try {
            conn.setRequestMethod("GET");
            if (additionalHeaders != null) {
                for (Map.Entry<String, String> en : additionalHeaders.entrySet()) {
                    conn.setRequestProperty(en.getKey(), en.getValue());
                }
            }
            return IOUtils.toByteArray(conn.getInputStream());
        } finally {
            conn.disconnect();
        }
    }
}
