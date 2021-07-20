package revolver.headead.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.IconUtils;
import revolver.headead.util.ui.M;

public class PartOfDayDrawable extends Drawable {

    private final Drawable iconDrawable;
    private final Paint arcPaint;
    private int value;

    public PartOfDayDrawable(Context context, int initialValue) {
        value = initialValue;
        iconDrawable = IconUtils.scaledDrawable(
                context, R.drawable.ic_sun, M.dp(16.f).intValue());
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(M.dp(2.f));
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setColor(ColorUtils.get(context, R.color.flameDark));
    }

    public void setValue(int val) {
        value = val;
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.scale(0.64f, 0.64f,
                getBounds().width() / 2.f, getBounds().height() / 2.f);
        iconDrawable.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.scale(0.9f, 0.9f,
                getBounds().width() / 2.f, getBounds().height() / 2.f);
        canvas.drawArc(getBounds().left, getBounds().top,
                getBounds().right, getBounds().bottom, 180, value / 100.f * 180.f,
                    false, arcPaint);
        canvas.restore();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        iconDrawable.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        iconDrawable.setBounds(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int alpha) {
        /* no-op */
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
