package org.petabytes.awesomeblogs.util;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

public class LineHeightSpan implements android.text.style.LineHeightSpan.WithDensity {

    private int mSize;
    private static float sProportion = 0;

    public LineHeightSpan(int size) {
        mSize = size;
    }

    public void chooseHeight(CharSequence text, int start, int end,
                             int spanstartv, int v,
                             Paint.FontMetricsInt fm) {
        // Should not get called, at least not by StaticLayout.
        chooseHeight(text, start, end, spanstartv, v, fm, null);
    }

    public void chooseHeight(CharSequence text, int start, int end,
                             int spanstartv, int v,
                             Paint.FontMetricsInt fm, TextPaint paint) {
        int size = mSize;
        if (paint != null) {
            size *= paint.density;
        }

        if (fm.bottom - fm.top < size) {
            fm.top = fm.bottom - size;
            fm.ascent = fm.ascent - size;
        } else {
            if (sProportion == 0) {
                /*
                 * Calculate what fraction of the nominal ascent
                 * the height of a capital letter actually is,
                 * so that we won't reduce the ascent to less than
                 * that unless we absolutely have to.
                 */

                Paint p = new Paint();
                p.setTextSize(100);
                Rect r = new Rect();
                p.getTextBounds("ABCDEFG", 0, 7, r);

                sProportion = (r.top) / p.ascent();
            }

            int need = (int) Math.ceil(-fm.top * sProportion);

            if (size - fm.descent >= need) {
                /*
                 * It is safe to shrink the ascent this much.
                 */

                fm.top = fm.bottom - size;
                fm.ascent = fm.descent - size;
            } else if (size >= need) {
                /*
                 * We can't show all the descent, but we can at least
                 * show all the ascent.
                 */

                fm.top = fm.ascent = -need;
                fm.bottom = fm.descent = fm.top + size;
            } else {
                /*
                 * Show as much of the ascent as we can, and no descent.
                 */

                fm.top = fm.ascent = -size;
                fm.bottom = fm.descent = 0;
            }
        }
    }
}