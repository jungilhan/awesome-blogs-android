package org.petabytes.awesomeblogs.util;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;

public final class Strings {

    private Strings() {
    }

    public static String EMPTY = "";
    public static String NEW_LINE = "\n";

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    public static SpannableStringBuilder colorizeBackground(@NonNull String text, @NonNull String matchText, @ColorInt int color, boolean ignoreCase) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        boolean isContains = ignoreCase ? text.toUpperCase().contains(matchText.toUpperCase()) : text.contains(matchText);
        if (!matchText.isEmpty() && isContains) {
            int start = ignoreCase ? TextUtils.indexOf(text.toUpperCase(), matchText.toUpperCase()) : TextUtils.indexOf(text, matchText);
            int end = start + matchText.length();
            builder.setSpan(new BackgroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            return builder;
        } else {
            return builder;
        }
    }
}
