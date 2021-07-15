package revolver.headead.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class TimePickerView extends LinearLayout {

    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final ClockView clockView;
    private final TextView startTimeView;
    private final TextView startTimeLabel;
    private final TextView endTimeView;
    private final TextView endTimeLabel;
    private final View nextDayView;

    private boolean startTimeFocused = true;
    private boolean isNextDayVisible = false;

    private int startHour = -1;
    private int startMinute = -1;
    private int endHour = -1;
    private int endMinute = -1;

    public TimePickerView(Context context) {
        this(context, null);
    }

    public TimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        View.inflate(context, R.layout.mtrl_time_picker_start_end_boxes, this);

        startTimeView = findViewById(R.id.mtrl_time_picker_start_box_value);
        endTimeView = findViewById(R.id.mtrl_time_picker_end_box_value);
        startTimeLabel = findViewById(R.id.mtrl_time_picker_start_box_label);
        endTimeLabel = findViewById(R.id.mtrl_time_picker_end_box_label);
        nextDayView = findViewById(R.id.mtrl_time_picker_next_day);
        clockView = new ClockView(context);

        final View startTimeBox =
                findViewById(R.id.mtrl_time_picker_start_box);
        final View endTimeBox =
                findViewById(R.id.mtrl_time_picker_end_box);
        startTimeBox.setOnClickListener(v -> resetStartBox());
        endTimeBox.setOnClickListener(v -> resetEndBox());

        addView(clockView, ViewUtils.newLayoutParams(LinearLayout.LayoutParams.class)
                .matchParentInWidth().height(264.f)
                    .gravity(Gravity.CENTER_HORIZONTAL).verticalMargin(16.f)
                        .get());
        int clockPadding = M.dp(16.f).intValue();
        clockView.setPadding(clockPadding, clockPadding, clockPadding, clockPadding);
        clockView.setOnTimeChangedListener((hour, minute) -> {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            if (startTimeFocused) {
                startTimeView.setText(timeFormatter.format(c.getTime()));
                startHour = hour;
                startMinute = minute;
            } else {
                endTimeView.setText(timeFormatter.format(c.getTime()));
                endHour = hour;
                endMinute = minute;
            }
            updateNextDayView();
        });
        clockView.setOnTimeSetListener(() -> {
            if (startTimeFocused) {
                startTimeFocused = false;
                startTimeLabel.setTextColor(
                        ColorUtils.get(context, R.color.tertiaryText));
                endTimeLabel.setTextColor(
                        ColorUtils.get(context, R.color.flameDark));
                clockView.reset();
            }
        });
    }

    private void resetStartBox() {
        startHour = startMinute = -1;
        clockView.reset();
        updateNextDayView();
        startTimeView.setText(R.string.mtrl_time_picker_no_start_end_value);
        if (startTimeFocused) {
            return;
        }
        endTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.tertiaryText));
        startTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.flameDark));
        startTimeFocused = true;
    }

    private void resetEndBox() {
        endHour = endMinute = -1;
        clockView.reset();
        updateNextDayView();
        endTimeView.setText(R.string.mtrl_time_picker_no_start_end_value);
        if (!startTimeFocused) {
            return;
        }
        startTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.tertiaryText));
        endTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.flameDark));
        startTimeFocused = false;
    }

    private void updateNextDayView() {
        if (startHour < 0 || startMinute < 0 ||
                endHour < 0 || endMinute < 0) {
            return;
        }
        if (endHour < startHour ||
                (endHour == startHour &&
                    endMinute < startMinute)) {
            if (isNextDayVisible) {
                return;
            }
            nextDayView.animate().alpha(1.f).translationY(0.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withStartAction(() -> {
                        nextDayView.setVisibility(GONE);
                        nextDayView.setAlpha(0.f);
                        nextDayView.setTranslationY(M.dp(8.f));
                        nextDayView.setVisibility(VISIBLE);
                    }).start();
            isNextDayVisible = true;
        } else {
            if (!isNextDayVisible) {
                return;
            }
            nextDayView.animate().alpha(0.f).translationY(M.dp(8.f))
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            isNextDayVisible = false;
        }
    }
}
