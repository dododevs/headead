package revolver.headead.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.fragments.record2.pickers.TimeInputMode;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;

public class SingleTimePickerView extends LinearLayout {

    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final ClockView clockView;
    private final PartOfDayPickerView partOfDayPickerView;

    private final TextView timeView;
    private final TextView timeLabel;
    private final ImageView timePartOfDayView;

    private final PartOfDayDrawable partOfDayDrawable;

    private boolean isClockShown = true;

    private int selectedHour = -1;
    private int selectedMinute = -1;
    private int selectedPartOfDay = -1;
    private Moment selectedMoment;

    private OnTimeInputModeChangedListener timeInputModeChangedListener;

    public SingleTimePickerView(Context context) {
        this(context, null);
    }

    public SingleTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleTimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleTimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setOrientation(VERTICAL);
        View.inflate(context, R.layout.mtrl_time_picker_upper_box, this);

        timeView = findViewById(R.id.mtrl_time_picker_box_value);
        timeLabel = findViewById(R.id.mtrl_time_picker_box_label);
        timePartOfDayView = findViewById(R.id.mtrl_time_picker_box_pod);

        timePartOfDayView.setImageDrawable(
                partOfDayDrawable = new PartOfDayDrawable(context, 0));

        View.inflate(context, R.layout.mtrl_time_picker_time_inputs, this);
        clockView = findViewById(R.id.mtrl_time_picker_clock);
        partOfDayPickerView = findViewById(R.id.mtrl_time_picker_part_of_day);

        final View upperTimeBox =
                findViewById(R.id.mtrl_time_picker_box);
        upperTimeBox.setOnClickListener(v -> focusUpperBox(true));

        int clockPadding = M.dp(16.f).intValue();
        clockView.setPadding(clockPadding, clockPadding, clockPadding, clockPadding);
        clockView.setOnTimeChangedListener((hour, minute) -> {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            timeView.setText(timeFormatter.format(c.getTime()));
            selectedHour = hour;
            selectedMinute = minute;
            selectedMoment = new Moment(createDateObjectFromTime(
                    selectedHour, selectedMinute), -1, TimeInputMode.CLOCK);
        });
        clockView.setOnTimeSetListener(() -> {
            selectedMoment = new Moment(createDateObjectFromTime(
                    selectedHour, selectedMinute), -1, TimeInputMode.CLOCK);
            clockView.reset();
        });

        partOfDayPickerView.setOnPartOfDayChangedListener(val -> {
            partOfDayDrawable.setValue(val);
            selectedPartOfDay = val;
        });
        partOfDayPickerView.setOnPartOfDaySetListener(() ->
                selectedMoment = new Moment(null, selectedPartOfDay, TimeInputMode.PART_OF_DAY));
    }

    public void setMoment(Moment moment) {
        this.selectedMoment = moment;
        if (this.selectedMoment != null) {
            if (moment.getTimeInputMode() == TimeInputMode.CLOCK) {
                final Pair<Integer, Integer> timePair =
                        getTimePairFromDateObject(moment.getDate());
                timeView.setText(timeFormatter.format(moment.getDate()));
                selectedHour = timePair.first;
                selectedMinute = timePair.second;
            } else if (moment.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                partOfDayDrawable.setValue(moment.getPartOfDay());
                selectedPartOfDay = moment.getPartOfDay();
            }
        }

        focusUpperBox(false);
    }

    public void setBoxLabelText(@StringRes int resourceId) {
        timeLabel.setText(resourceId);
    }

    public void setOnTimeInputModeChangedListener(OnTimeInputModeChangedListener l) {
        timeInputModeChangedListener = l;
    }

    public void setShowClockInput(boolean reset) {
        Log.d("TimePickerView", "setShowClockInput");
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
        clockView.reset();
        clockView.animate().alpha(1.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withStartAction(() -> {
                    clockView.setAlpha(0.f);
                    clockView.setVisibility(VISIBLE);
                }).setStartDelay(200L).start();
        isClockShown = true;
        if (reset) resetUpperBox();
        timePartOfDayView.setVisibility(GONE);
        timeView.setVisibility(VISIBLE);
        if (timeInputModeChangedListener != null) {
            timeInputModeChangedListener.onTimeInputModeChanged(TimeInputMode.CLOCK);
        }
    }

    public void setShowClockInput() {
        setShowClockInput(false);
    }

    private void setShowPartOfDayInput(boolean reset) {
        Log.d("TimePickerView", "setShowPartOfDayInput");
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
        if (reset) resetUpperBox();
        timePartOfDayView.setVisibility(VISIBLE);
        timeView.setVisibility(GONE);
        if (timeInputModeChangedListener != null) {
            timeInputModeChangedListener.onTimeInputModeChanged(TimeInputMode.PART_OF_DAY);
        }
    }

    public void setShowPartOfDayInput() {
        setShowPartOfDayInput(false);
    }

    public boolean isClockShown() {
        return isClockShown;
    }

    public Moment getSelectedMoment() {
        return selectedMoment;
    }

    private void focusUpperBox(boolean reset) {
        timeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.flameDark));

        if (selectedMoment != null) {
            if (selectedMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
                if (isClockShown) {
                    if (reset) resetUpperBox();
                } else {
                    setShowClockInput();
                }
            } else if (selectedMoment.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                if (!isClockShown) {
                    if (reset) resetUpperBox();
                } else {
                    setShowPartOfDayInput();
                }
            }
        } else {
            setShowClockInput(true);
        }
    }

    private void resetUpperBox() {
        selectedHour = selectedMinute = selectedPartOfDay = -1;
        selectedMoment = null;
        clockView.reset();
        timeView.setText(R.string.mtrl_time_picker_no_start_end_value);
        partOfDayDrawable.setValue(0);
    }

    private static Date createDateObjectFromTime(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTime();
    }

    private static Pair<Integer, Integer> getTimePairFromDateObject(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return new Pair<>(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public interface OnTimeInputModeChangedListener {
        void onTimeInputModeChanged(TimeInputMode mode);
    }
}
