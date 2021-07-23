package revolver.headead.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenteredImageSpan extends ImageSpan {

    public CenteredImageSpan(Drawable drawable) {
        super(drawable);
    }

    public CenteredImageSpan(Context context, @DrawableRes int resourceId) {
        super(context, resourceId);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        Drawable drawable = getDrawable();
        Rect bounds = drawable.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt paintFm = paint.getFontMetricsInt();
            int fontHeight = paintFm.descent - paintFm.ascent;
            int drawableHeight = bounds.bottom - bounds.top;
            int centerY = paintFm.ascent + fontHeight / 2;

            fm.ascent = centerY - drawableHeight / 2;
            fm.top = fm.ascent;
            fm.bottom = centerY + drawableHeight / 2;
            fm.descent = fm.bottom;
        }
        return bounds.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();

        Paint.FontMetricsInt paintFm = paint.getFontMetricsInt();
        int fontHeight = paintFm.descent - paintFm.ascent;
        int centerY = y + paintFm.descent - fontHeight / 2;
        int translationY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
        canvas.translate(x, translationY);

        drawable.draw(canvas);
        canvas.restore();
    }
}
