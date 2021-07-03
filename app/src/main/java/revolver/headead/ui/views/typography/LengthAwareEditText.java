package revolver.headead.ui.views.typography;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.TextUtils;

public class LengthAwareEditText extends TextInputEditText {

    private TextDrawable textDrawable;
    private int maxLength;

    public LengthAwareEditText(Context context) {
        super(context);
        initialize(null);
    }

    public LengthAwareEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public LengthAwareEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LengthAwareEditText);
        this.maxLength = a.getInt(R.styleable.LengthAwareEditText_maxLength, 0);
        a.recycle();

        addTextChangedListener(new TextUtils.SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                generateAndSetTextDrawable();
            }
        });
    }

    public int getPromptWidth() {
        return textDrawable.getTextWidth();
    }

    private void generateAndSetTextDrawable() {
        textDrawable = new TextDrawable(String.format(
                Locale.getDefault(), "%d/%d", length(), maxLength), getTextSize(),
                    ColorUtils.get(getContext(), R.color.blackOlive));
        textDrawable.setTextY(getHeight() - getLineBounds(0, null) +
                - getPaddingBottom() - M.dp(1.5f).intValue());
        setCompoundDrawablesWithIntrinsicBounds(null, null, textDrawable, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        generateAndSetTextDrawable();
    }

    private static class TextDrawable extends Drawable {
        private final String text;
        private final Paint paint;
        private final int textWidth;
        private int textY;

        TextDrawable(String text, float textSize, @ColorInt int color) {
            this.text = text;
            paint = new TextPaint();
            paint.setTextSize(textSize);
            paint.setAntiAlias(true);
            paint.setColor(color);
            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setTypeface(Typeface.DEFAULT);

            textWidth = (int) paint.measureText(text);
            setBounds(0, 0, textWidth, (int) textSize);
        }

        int getTextWidth() {
            return textWidth;
        }

        void setTextY(int textY) {
            this.textY = textY;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawText(text, 0, textY, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

}