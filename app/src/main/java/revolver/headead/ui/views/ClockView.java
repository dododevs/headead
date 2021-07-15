package revolver.headead.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;

public class ClockView extends ConstraintLayout {

    private static final String[] hourLabels = {
            "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
    };
    private static final String[] hour24HLabels = {
            "00", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
    };
    private static final String[] minuteLabels = {
            "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
    };
    private static final int DEGREES_PER_MINUTE = 6;
    private static final int DEGREES_PER_HOUR = 30;

    private final ImageView centerDot;
    private float centerDotX = -1, centerDotY = -1;

    private final ImageView minuteDot, hourDot;
    private final LayoutParams minuteDotLayoutParams, hourDotLayoutParams;
    private final TextView[] labelViews = new TextView[12];
    private final MaterialButtonToggleGroup amPmSelectorView;
    private final Paint hourHandPaint;
    private final Paint minuteHandPaint;

    private boolean is24H = false;
    private boolean showingMinutes = false;
    private boolean userUpdatedMinutes = false;
    private int selectedHour = -1, selectedMinute = -1;
    private OnTimeChangedListener timeChangedListener;
    private OnTimeSetListener timeSetListener;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        View.inflate(context, R.layout.mtrl_clock_view_dot, this);
        centerDot = findViewById(R.id.mtrl_clock_view_dot);

        View.inflate(context, R.layout.mtrl_clock_ampm_selector, this);
        amPmSelectorView = findViewById(R.id.mtrl_clock_ampm_selector);
        centerDot.animate().scaleX(0.f).scaleY(0.f)
                .setDuration(150L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        for (int i = 0, angle = 0; i < labelViews.length; i++) {
            labelViews[i] = (TextView) View.inflate(
                    context, R.layout.mtrl_clock_view_hour_label, null);
            labelViews[i].setText(hourLabels[i]);

            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.circleConstraint = R.id.mtrl_clock_view_dot;
            lp.circleRadius = M.dp(96.f).intValue();
            lp.circleAngle = angle;
            labelViews[i].setLayoutParams(lp);

            addView(labelViews[i]);
            angle += 360 / labelViews.length;
        }

        minuteDot = new ImageView(context);
        minuteDot.setImageResource(R.drawable.mtrl_clock_view_minute_dot_background);
        minuteDot.setLayoutParams(minuteDotLayoutParams = new LayoutParams(
                M.dp(36.f).intValue(), M.dp(36.f).intValue()
        ));
        minuteDot.setVisibility(GONE);
        minuteDot.setElevation(-1.f);
        addView(minuteDot);
        minuteDotLayoutParams.circleConstraint = R.id.mtrl_clock_view_dot;
        minuteDotLayoutParams.circleRadius = M.dp(96.f).intValue();

        hourDot = new ImageView(context);
        hourDot.setImageResource(R.drawable.mtrl_clock_view_hour_dot_background);
        hourDot.setLayoutParams(hourDotLayoutParams = new LayoutParams(
                M.dp(36.f).intValue(), M.dp(36.f).intValue()
        ));
        hourDot.setVisibility(GONE);
        hourDot.setElevation(-1.f);
        addView(hourDot);
        hourDotLayoutParams.circleConstraint = R.id.mtrl_clock_view_dot;
        hourDotLayoutParams.circleRadius = M.dp(96.f).intValue();

        minuteHandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minuteHandPaint.setStyle(Paint.Style.STROKE);
        minuteHandPaint.setStrokeWidth(M.dp(2.f));
        minuteHandPaint.setStrokeCap(Paint.Cap.ROUND);
        minuteHandPaint.setColor(ColorUtils.get(context, R.color.flame));
        hourHandPaint = new Paint(minuteHandPaint);
        hourHandPaint.setColor(ColorUtils.get(context, R.color.flameTranslucent));

        setWillNotDraw(false);
    }

    private void onRotate(double angle) {
        if (showingMinutes) {
            userUpdatedMinutes = true;
            selectedMinute = updateMinuteDotPosition(angle, false);
            invalidate();
        } else {
            int i = (int) Math.floor(angle * (labelViews.length / 360.0));
            resetAllLabelViews();
            labelViews[i].setBackgroundResource(R.drawable.mtrl_clock_hour_label_background);
            labelViews[i].setTextColor(ColorUtils.get(getContext(), R.color.white));
            selectedHour = i;
        }
        if (timeChangedListener != null) {
            boolean isPm = amPmSelectorView
                    .getCheckedButtonId() == R.id.mtrl_clock_pm_selector;
            timeChangedListener.onTimeChanged(selectedHour != -1 ?
                    (isPm ? selectedHour + 12 : selectedHour) : 0,
                        selectedMinute != -1 ? selectedMinute : 0);
        }
    }

    private void onRotationCompleted(double angle) {
        if (showingMinutes) {
            selectedMinute = updateMinuteDotPosition(angle, true);
            if (timeSetListener != null) {
                timeSetListener.onTimeSet();
            }
        } else {
            hourDotLayoutParams.circleAngle = selectedHour * DEGREES_PER_HOUR;
            hourDot.setLayoutParams(hourDotLayoutParams);
            hourDot.setVisibility(VISIBLE);
            setShowMinutes();
        }
    }

    private int updateMinuteDotPosition(double angle, boolean stickToNearestMinute) {
        final int minute = (int) Math.ceil(angle / DEGREES_PER_MINUTE) % 60;
        resetAllLabelViews();
        if (stickToNearestMinute) {
            minuteDotLayoutParams.circleAngle = minute * DEGREES_PER_MINUTE;
        } else {
            minuteDotLayoutParams.circleAngle = (float) angle;
        }
        minuteDot.setLayoutParams(minuteDotLayoutParams);
        minuteDot.setVisibility(VISIBLE);
        return minute;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float dx = x - centerDot.getX();
        float dy = y - centerDot.getY();
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            onRotate(getAngleFromCoordinates(dx, dy));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onRotationCompleted(getAngleFromCoordinates(dx, dy));
        }
        final double centerDistance =
                Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return centerDistance < M.dp(128.f).intValue() &&
                centerDistance > M.dp(24.f).intValue();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (centerDotX < 0 || centerDotY < 0) {
            centerDotX = centerDot.getX() + centerDot.getWidth() / 2.f;
            centerDotY = centerDot.getY() + centerDot.getHeight() / 2.f;
        }
        if (showingMinutes) {
            float hx = (hourDot.getX() + hourDot.getWidth() / 2.f + centerDotX) / 2.f;
            float hy = (hourDot.getY() + hourDot.getHeight() / 2.f + centerDotY) / 2.f;
            canvas.drawLine(centerDotX, centerDotY,
                    hx, hy, hourHandPaint);
            if (!userUpdatedMinutes) {
                return;
            }
            float mx = minuteDot.getX() + minuteDot.getWidth() / 2.f;
            float my = minuteDot.getY() + minuteDot.getHeight() / 2.f;
            canvas.drawLine(centerDotX, centerDotY,
                    mx, my, minuteHandPaint);
        }
    }

    public void set24HourClockMode(boolean is24H) {
        if (this.is24H == is24H) {
            return;
        }
        if (is24H) {
            switchClockLabels(hour24HLabels);
        } else {
            switchClockLabels(hourLabels);
        }
        this.is24H = is24H;
    }

    public void setShowMinutes() {
        if (showingMinutes) {
            return;
        }
        centerDot.animate().scaleX(1.f).scaleY(1.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        amPmSelectorView.animate().alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    amPmSelectorView.setVisibility(GONE);
                    amPmSelectorView.setAlpha(1.f);
                }).start();
        switchClockLabels(minuteLabels);
        showingMinutes = true;
    }

    public void setShowHours() {
        if (!showingMinutes) {
            return;
        }
        centerDot.animate().scaleX(0.f).scaleY(0.f)
                .setDuration(150L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        amPmSelectorView.animate().alpha(1.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withStartAction(() -> {
                    amPmSelectorView.setAlpha(0.f);
                    amPmSelectorView.setVisibility(VISIBLE);
                }).start();
        switchClockLabels(is24H ? hour24HLabels : hourLabels);
        showingMinutes = false;
    }

    public void reset() {
        userUpdatedMinutes = false;
        resetAllLabelViews();
        minuteDot.animate().alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    minuteDot.setVisibility(GONE);
                    minuteDot.setAlpha(1.f);
                }).start();
        hourDot.animate().alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    hourDot.setVisibility(GONE);
                    hourDot.setAlpha(1.f);
                }).start();
        setShowHours();
    }

    public void setOnTimeChangedListener(OnTimeChangedListener l) {
        timeChangedListener = l;
    }

    public void setOnTimeSetListener(OnTimeSetListener l) {
        timeSetListener = l;
    }

    private void switchClockLabels(String[] newLabels) {
        for (int i = 0; i < labelViews.length; i++) {
            TextView label = labelViews[i];
            int finalI = i;
            label.animate().alpha(0.f)
                    .setDuration(150L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        label.setVisibility(GONE);
                        label.setAlpha(1.f);
                        label.setText(newLabels[finalI]);
                        label.setScaleX(0.f);
                        label.setScaleY(0.f);
                        label.setVisibility(VISIBLE);
                        label.animate().scaleX(1.f).scaleY(1.f)
                                .setDuration(200L)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .start();
                    }).start();
        }
    }

    private void resetAllLabelViews() {
        for (TextView label : labelViews) {
            label.setBackground(null);
            label.setTextColor(ColorUtils.get(getContext(), R.color.blackOlive));
        }
    }

    private double getAngleFromCoordinates(float dx, float dy) {
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;
        if (angle < 0) {
            angle = 360.0 + angle;
        }
        angle += 90.0;
        if (angle > 360.0) {
            angle -= 360.0;
        }
        return angle;
    }

    public interface OnTimeChangedListener {
        void onTimeChanged(int hour, int minute);
    }

    public interface OnTimeSetListener {
        void onTimeSet();
    }
}
