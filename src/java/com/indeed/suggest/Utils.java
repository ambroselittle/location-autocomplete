package com.indeed.suggest;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * A few useful methods for parsing the query log, the main one is
 * getNameValues().
 */
public class Utils {
    private Utils() { }

    /**
     * Returns a Map of name-value pairs from a String, using "&" as a delimiter between each pair,
     * URLDecoding the names and values.
     */
    public static Map<String, String> getNameValues(String in) {
        String delim = "&";
        try {
            Map<String, String> map = new LinkedHashMap<String, String>();
            Set<String> set = split(in, delim);
            for (String s : set) {
                NameValuePair nv = getNameValue(s);
                if (nv.name != null && nv.value != null)
                    map.put(URLDecoder.decode(nv.name, "UTF-8"), URLDecoder.decode(nv.value, "UTF-8"));
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Set<String> split(String in, String delim) {
        StringTokenizer tok = new StringTokenizer(in, delim);
        Set<String> set = new LinkedHashSet<String>();
        while (tok.hasMoreElements()) {
            set.add(tok.nextToken());
        }
        return set;
    }

    public static NameValuePair getNameValue(String nameValuePair) {
        int equalsIndex = nameValuePair.indexOf('=');
        if (equalsIndex == -1) {
            return new NameValuePair(nameValuePair, null);
        } else {
            return new NameValuePair(nameValuePair.substring(0, equalsIndex),
                    nameValuePair.substring(equalsIndex + 1));
        }
    }

    public static class NameValuePair  {
        final private String name;
        final private String value;

        public NameValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
        public String getName() { return name; }
        public String getValue() { return value; }
        public String toString() { return "["+name+"="+value+"]"; }
    }
}
