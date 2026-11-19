package net.poisonpulse.poisn.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Fmt {
    private static final String[] SUFFIX = {"", "K", "M", "B", "T"};
    private static final DecimalFormat DF;

    static {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.US);
        DF = new DecimalFormat("#,##0.##", s);
    }

    private Fmt() {}

    public static String money(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "?";
        if (v < 0) return "-" + money(-v);
        if (v < 1000.0) return DF.format(v);

        int idx = 0;
        double n = v;
        while (n >= 1000.0 && idx < SUFFIX.length - 1) {
            n /= 1000.0;
            idx++;
        }
        return DF.format(n) + SUFFIX[idx];
    }
}
