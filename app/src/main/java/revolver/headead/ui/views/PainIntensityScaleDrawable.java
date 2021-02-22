package revolver.headead.ui.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;

public class PainIntensityScaleDrawable extends LayerDrawable {

    private float value = 0.f;
    private final @ColorInt int startColor, endColor;

    public PainIntensityScaleDrawable(Context context) {
        super(new Drawable[] {
                Objects.requireNonNull(ContextCompat
                        .getDrawable(context, R.drawable.fragment_pain_intensity_picker_base)),
                Objects.requireNonNull(ContextCompat
                        .getDrawable(context, R.drawable.fragment_pain_intensity_picker_overlay)),
        });
        startColor = ContextCompat.getColor(context, R.color.paleSpringBudDark);
        endColor = ContextCompat.getColor(context, R.color.flame);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        getDrawable(0).draw(canvas);
        canvas.clipRect(0, 0, calculateClipEnd(), getBounds().bottom);
        getDrawable(1).draw(canvas);
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        for (int i = 0; i < getNumberOfLayers(); i++) {
            getDrawable(i).setAlpha(alpha);
        }
    }

    @Override
    public void setTint(int tintColor) {
        // no-op
    }

    @Override
    public void setTintList(ColorStateList tint) {
        // no-op
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // no-op
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        for (int i = 0; i < getNumberOfLayers(); i++) {
            getDrawable(i).setBounds(left, top, right, bottom);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setValue(@FloatRange(from = 0.0f, to = 1.0f) float value) {
        this.value = value;
        getDrawable(1).setColorFilter(new PorterDuffColorFilter(ColorUtils
                .interpolateBetweenColors(startColor, endColor, value), PorterDuff.Mode.SRC_ATOP));
        invalidateSelf();
    }

    private float calculateClipEnd() {
        return getBounds().right * value;
    }
}
