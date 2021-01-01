package revolver.headead.util.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class IconUtils {

    public static Drawable drawableWithResolvedColor(final Context context,
                                                     @DrawableRes int drawableRes,
                                                     @ColorRes int color) {
        if (context == null) {
            return null;
        }
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable != null) {
            drawable = drawable.mutate();
            if (color != 0) {
                drawable.setColorFilter(ColorUtils.get(
                        context, color), PorterDuff.Mode.SRC_ATOP);
            }
            return drawable;
        }
        return null;
    }

    public static Drawable drawable(final Context context, @DrawableRes int drawableRes) {
        return drawableWithResolvedColor(context, drawableRes, 0);
    }

    public static Drawable scaledDrawable(final Context context, @DrawableRes int drawableRes, int size) {
        return scaledDrawableWithResolvedColor(context, drawableRes, size, 0);
    }

    public static Drawable scaledDrawableWithResolvedColor(final Context context,
                                                           @DrawableRes int drawableRes,
                                                           int size,
                                                           @ColorRes int color) {
        final Drawable drawable = drawableWithResolvedColor(context, drawableRes, color);
        if (drawable == null) {
            return null;
        }
        drawable.mutate().setBounds(0, 0, size, size);
        return drawable;
    }

}
