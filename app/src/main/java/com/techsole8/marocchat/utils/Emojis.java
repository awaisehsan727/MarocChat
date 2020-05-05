
package com.techsole8.marocchat.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Emojis {
    private static final HashMap<String, String> mappings = new HashMap<>();
    private static Pattern pattern;

    static {
        mappings.put(":)", "\uD83D\uDE03");
        mappings.put(":-)", "\uD83D\uDE03");
        mappings.put(":(", "\uD83D\uDE1E");
        mappings.put(":-(", "\uD83D\uDE1E");
        mappings.put(";)", "\uD83D\uDE09");
        mappings.put(";-)", "\uD83D\uDE09");
        mappings.put(":p", "\uD83D\uDE1B");
        mappings.put(":-p", "\uD83D\uDE1B");
        mappings.put(":P", "\uD83D\uDE1B");
        mappings.put(":-P", "\uD83D\uDE1B");
        mappings.put(":D", "\uD83D\uDE04");
        mappings.put(":-D", "\uD83D\uDE04");
        mappings.put(":[", "\uD83D\uDE12");
        mappings.put(":-[", "\uD83D\uDE12");
        mappings.put(":\\", "\uD83D\uDE14");
        mappings.put(":-\\", "\uD83D\uDE14");
        mappings.put(":o", "\uD83D\uDE2E");
        mappings.put(":-o", "\uD83D\uDE2E");
        mappings.put(":O", "\uD83D\uDE32");
        mappings.put(":-O", "\uD83D\uDE32");
        mappings.put(":*", "\uD83D\uDE18");
        mappings.put(":-*", "\uD83D\uDE18");
        mappings.put("8)", "\uD83D\uDE0E");
        mappings.put("8-)", "\uD83D\uDE0E");
        mappings.put(":'(", "\uD83D\uDE22");
        mappings.put(":'-(", "\uD83D\uDE22");
        mappings.put(":X", "\uD83D\uDE2F");
        mappings.put(":-X", "\uD83D\uDE2F");

        StringBuilder regex = new StringBuilder("(");

        for (String emoji : mappings.keySet()) {
            regex.append(Pattern.quote(emoji));
            regex.append("|");
        }

        regex.deleteCharAt(regex.length() - 1);
        regex.append(")");

        pattern = Pattern.compile(regex.toString());
    }

    /**
     * Replace text smileys like :) with Emojis.
     */
    public static String convert(String text) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, mappings.get(matcher.group(1)));
        }

        matcher.appendTail(buffer);

        return buffer.toString();
    }
}
