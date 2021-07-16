package revolver.headead.ui.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.ui.fragments.record2.pickers.timeinput.ClockPageFragment;
import revolver.headead.ui.fragments.record2.pickers.timeinput.PartOfDayPageFragment;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class TimePickerView extends LinearLayout {

    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final ClockView clockView;
    private final PartOfDayPickerView partOfDayPickerView;

    private final TextView startTimeView;
    private final TextView startTimeLabel;
    private final ImageView startTimePartOfDayView;
    private final TextView endTimeView;
    private final TextView endTimeLabel;
    private final ImageView endTimePartOfDayView;
    private final View nextDayView;

    private final PartOfDayDrawable startPartOfDayDrawable;
    private final PartOfDayDrawable endPartOfDayDrawable;

    private boolean startTimeFocused = true;
    private boolean isNextDayVisible = false;
    private boolean isClockShown = true;

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
        startTimePartOfDayView = findViewById(R.id.mtrl_time_picker_start_box_pod);
        endTimePartOfDayView = findViewById(R.id.mtrl_time_picker_end_box_pod);
        nextDayView = findViewById(R.id.mtrl_time_picker_next_day);

        startTimePartOfDayView.setImageDrawable(
                startPartOfDayDrawable = new PartOfDayDrawable(context, 0));
        endTimePartOfDayView.setImageDrawable(
                endPartOfDayDrawable = new PartOfDayDrawable(context, 0));

        View.inflate(context, R.layout.mtrl_time_picker_time_inputs, this);
        clockView = findViewById(R.id.mtrl_time_picker_clock);
        partOfDayPickerView = findViewById(R.id.mtrl_time_picker_part_of_day);

        final View startTimeBox =
                findViewById(R.id.mtrl_time_picker_start_box);
        final View endTimeBox =
                findViewById(R.id.mtrl_time_picker_end_box);
        startTimeBox.setOnClickListener(v -> resetStartBox());
        endTimeBox.setOnClickListener(v -> resetEndBox());

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

        partOfDayPickerView.setOnPartOfDayChangedListener(val -> {
            if (startTimeFocused) {
                startPartOfDayDrawable.setValue(val);
            } else {
                endPartOfDayDrawable.setValue(val);
            }
        });
        partOfDayPickerView.setOnPartOfDaySetListener(() -> {

        });
    }

    public void setShowClockInput() {
        if (isClockShown) {
            return;
        }
        partOfDayPickerView.animate().alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    partOfDayPickerView.setVisibility(GONE);
                    partOfDayPickerView.setAlpha(1.f);
                }).start();
        clockView.animate().alpha(1.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withStartAction(() -> {
                    clockView.setAlpha(0.f);
                    clockView.setVisibility(VISIBLE);
                }).setStartDelay(200L).start();
        isClockShown = true;
        if (startTimeFocused) {
            resetStartBox();
            startTimePartOfDayView.setVisibility(GONE);
            startTimeView.setVisibility(VISIBLE);
        } else {
            resetEndBox();
            endTimePartOfDayView.setVisibility(GONE);
            endTimeView.setVisibility(VISIBLE);
        }
    }

    public void setShowPartOfDayInput() {
        if (!isClockShown) {
            return;
        }
        clockView.animate().alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    clockView.setVisibility(GONE);
                    clockView.setAlpha(1.f);
                }).start();
        partOfDayPickerView.animate().alpha(1.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withStartAction(() -> {
                    partOfDayPickerView.setAlpha(0.f);
                    partOfDayPickerView.setVisibility(VISIBLE);
                }).setStartDelay(200L).start();
        isClockShown = false;
        if (startTimeFocused) {
            resetStartBox();
            startTimePartOfDayView.setVisibility(VISIBLE);
            startTimeView.setVisibility(GONE);
        } else {
            resetEndBox();
            endTimePartOfDayView.setVisibility(VISIBLE);
            endTimeView.setVisibility(GONE);
        }
    }

    public boolean isClockShown() {
        return isClockShown;
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
                    endMinute <= startMinute)) {
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
