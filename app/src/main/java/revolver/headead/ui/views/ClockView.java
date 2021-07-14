package revolver.headead.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;

public class ClockView extends ConstraintLayout {

    private static final String[] hourLabels = {
            "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
    };
    private static final String[] hour24HLabels = {
            "12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
    };
    private static final String[] minuteLabels = {
            "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
    };

    private final ImageView centerDot;
    private final ImageView minuteDot;
    private final LayoutParams minuteDotLayoutParams;
    private final TextView[] labelViews = new TextView[12];

    private boolean is24H = false;
    private boolean showingMinutes = false;

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

        minuteDot = new ImageView(getContext());
        minuteDot.setImageResource(R.drawable.mtrl_clock_hour_label_background);
        minuteDot.setLayoutParams(minuteDotLayoutParams = new LayoutParams(
                M.dp(36.f).intValue(), M.dp(36.f).intValue()
        ));
        minuteDot.setVisibility(GONE);
        minuteDot.setElevation(-1.f);
        addView(minuteDot);
        minuteDotLayoutParams.circleConstraint = R.id.mtrl_clock_view_dot;
        minuteDotLayoutParams.circleRadius = M.dp(96.f).intValue();
    }

    private void onRotate(double angle) {
        if (showingMinutes) {
            resetAllLabelViews();
            minuteDotLayoutParams.circleAngle = (float) angle;
            minuteDot.setLayoutParams(minuteDotLayoutParams);
            minuteDot.setVisibility(VISIBLE);
        } else {
            int i = (int) Math.floor(angle * (labelViews.length / 360.0));
            resetAllLabelViews();
            labelViews[i].setBackgroundResource(R.drawable.mtrl_clock_hour_label_background);
            labelViews[i].setTextColor(ColorUtils.get(getContext(), R.color.white));
        }
    }

    private void resetAllLabelViews() {
        for (TextView label : labelViews) {
            label.setBackground(null);
            label.setTextColor(ColorUtils.get(getContext(), R.color.blackOlive));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float dx = x - centerDot.getX();
        float dy = y - centerDot.getY();
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            double angle = Math.atan2(dy, dx) * 180 / Math.PI;
            if (angle < 0) {
                angle = 360.0 + angle;
            }
            angle += 90.0;
            if (angle > 360.0) {
                angle -= 360.0;
            }
            onRotate(angle);
        }
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) < M.dp(128.f).intValue();
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
        switchClockLabels(minuteLabels);
        showingMinutes = true;
    }

    public void setShowHours() {
        if (!showingMinutes) {
            return;
        }
        switchClockLabels(is24H ? hour24HLabels : hourLabels);
        showingMinutes = false;
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

    /*@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int angle = 0;
        for (TextView hourView : hourViews) {
            final LayoutParams lp = (LayoutParams) hourView.getLayoutParams();
            lp.circleConstraint = R.id.mtrl_clock_view_dot;
            lp.circleRadius = (right - left) / 2;
            lp.circleAngle = angle;
            angle += 360 / minuteLabels.length;
        }
    }*/
}
