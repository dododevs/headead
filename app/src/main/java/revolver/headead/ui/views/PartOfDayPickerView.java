package revolver.headead.ui.views;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.slider.Slider;

import java.util.Calendar;
import java.util.Date;

import revolver.headead.R;
import revolver.headead.util.ui.ViewUtils;

public class PartOfDayPickerView extends LinearLayout {

    private AnimatedSlider slider;
    private FrameLayout skyContainerView;
    private ImageView sunView;

    private final static Calendar c = Calendar.getInstance();
    private int lastRoundedValue = 0;

    public PartOfDayPickerView(Context context) {
        this(context, null);
    }

    public PartOfDayPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PartOfDayPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PartOfDayPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public void setPartOfDay(float partOfDay) {
        slider.setValue(partOfDay);
        onNewValueConfirmed();
    }

    public float getPartOfDay() {
        return slider.getValue();
    }

    public boolean isLaterToday() {
        final Date now = new Date();
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.SECOND, 86400 * (int) slider.getValue() / 100);
        return c.getTime().after(now);
    }

    private void initialize() {
        setOrientation(VERTICAL);

        final LinearLayout partsOfDayView = (LinearLayout)
                View.inflate(getContext(), R.layout.labels_part_of_day, null);
        skyContainerView = (FrameLayout)
                View.inflate(getContext(), R.layout.sky_part_of_day, null);

        slider = (AnimatedSlider) View.inflate(
                getContext(), R.layout.slider_part_of_day_selector, null);
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                onNewValueConfirmed();
            }
        });

        addView(skyContainerView, ViewUtils.newLayoutParams(LinearLayout.LayoutParams.class)
                .matchParentInWidth().wrapContentInHeight().get());
        addView(slider, ViewUtils.newLayoutParams(LinearLayout.LayoutParams.class)
                .matchParentInWidth().wrapContentInHeight()
                    .horizontalMargin(4.f).bottomMargin(2.f).get());
        addView(partsOfDayView, ViewUtils.newLayoutParams(LinearLayout.LayoutParams.class)
                .matchParentInWidth().wrapContentInHeight()
                    .horizontalMargin(4.f).topMargin(-8.f).bottomMargin(8.f).get());

        sunView = skyContainerView.findViewById(R.id.sun);
        slider.getViewTreeObserver().addOnGlobalLayoutListener(() -> slider.addOnChangeListener((slider1, value, fromUser) -> {
            sunView.setTranslationX((int)
                    ((getSkyContainerViewInnerWidth() - sunView.getWidth()) / 100. * value));
            sunView.setTranslationY(getSunPositionOnEllipticalCurve(
                    getSkyContainerViewInnerWidth() - sunView.getWidth(),
                    getSkyContainerViewInnerHeight(),
                    (int) ((getSkyContainerViewInnerWidth() - sunView.getWidth()) / 100.0 * value)
            ));
            int roundedValue = (int) Math.ceil(Math.round(value) / 25.f);
            switch (roundedValue) {
                case 1:
                    if (lastRoundedValue == 2) {
                        sunView.setImageResource(R.drawable.avd_sun_to_moon);
                        ((AnimatedVectorDrawable) sunView.getDrawable()).start();
                    }
                    break;
                case 2:
                    if (lastRoundedValue == 1) {
                        sunView.setImageResource(R.drawable.avd_moon_to_sun);
                        ((AnimatedVectorDrawable) sunView.getDrawable()).start();
                    }
                    break;
                case 3:
                    if (lastRoundedValue == 4) {
                        sunView.setImageResource(R.drawable.avd_moon_to_sun);
                        ((AnimatedVectorDrawable) sunView.getDrawable()).start();
                    }
                    break;
                case 4:
                    if (lastRoundedValue == 3) {
                        sunView.setImageResource(R.drawable.avd_sun_to_moon);
                        ((AnimatedVectorDrawable) sunView.getDrawable()).start();
                    }
                    break;
            }
            lastRoundedValue = roundedValue;
            Log.d("rounded", "value: " + roundedValue);
        }));
    }

    private void onNewValueConfirmed() {
        if (isLaterToday()) {
            int min = getCurrentMinimumValue();
            Log.d("min", min + "");
            slider.setValue(min);
        }
    }

    private int getCurrentMinimumValue() {
        c.setTime(new Date());
        return (c.get(Calendar.SECOND) + c.get(Calendar.MINUTE) * 60 +
                    c.get(Calendar.HOUR_OF_DAY) * 60 * 60) * 100 / 86400;
    }

    private int getSkyContainerViewInnerWidth() {
        return skyContainerView.getWidth() -
                skyContainerView.getPaddingStart() -
                    skyContainerView.getPaddingEnd();
    }

    private int getSkyContainerViewInnerHeight() {
        return skyContainerView.getHeight() -
                skyContainerView.getPaddingTop() -
                    skyContainerView.getPaddingBottom();
    }

    private static int getSunPositionOnEllipticalCurve(int width, int height, int x) {
        return (int) -Math.round(Math.sqrt((1.0 -
                Math.pow(x - width / 2.0, 2) / Math.pow(width / 2.0, 2)) *
                    Math.pow(height / 2.0, 2)));
    }
}
