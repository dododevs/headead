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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.fragments.record2.pickers.TimeInputMode;
import revolver.headead.util.misc.TimeFormattingUtils;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;

public class TimePickerView extends LinearLayout {

    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final ClockView clockView;
    private final PartOfDayPickerView partOfDayPickerView;

    private final View endTimeBox;
    private final View boxDividerView;
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
    private boolean isStartOnly = false;

    private int startHour = -1;
    private int startMinute = -1;
    private int startPartOfDay = -1;
    private int endHour = -1;
    private int endMinute = -1;
    private int endPartOfDay = -1;

    private Moment startMoment;
    private Moment endMoment;

    private OnTimeInputModeChangedListener timeInputModeChangedListener;

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

        boxDividerView = findViewById(R.id.mtrl_time_picker_box_divider);
        startTimeView = findViewById(R.id.mtrl_time_picker_box_value);
        endTimeView = findViewById(R.id.mtrl_time_picker_end_box_value);
        startTimeLabel = findViewById(R.id.mtrl_time_picker_box_label);
        endTimeLabel = findViewById(R.id.mtrl_time_picker_end_box_label);
        startTimePartOfDayView = findViewById(R.id.mtrl_time_picker_box_pod);
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
                findViewById(R.id.mtrl_time_picker_box);
        final View endTimeBox =
                findViewById(R.id.mtrl_time_picker_end_box);
        startTimeBox.setOnClickListener(v -> focusStartBox(true));
        endTimeBox.setOnClickListener(v -> focusEndBox(true));
        this.endTimeBox = endTimeBox;

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
                startMoment = new Moment(createDateObjectFromTime(
                        startHour, startMinute), -1, TimeInputMode.CLOCK);
            } else {
                endTimeView.setText(timeFormatter.format(c.getTime()));
                endHour = hour;
                endMinute = minute;
                endMoment = new Moment(createDateObjectFromTime(
                        endHour, endMinute), -1, TimeInputMode.CLOCK);
            }
            updateNextDayView();
        });
        clockView.setOnTimeSetListener(() -> {
            if (startTimeFocused) {
                startMoment = new Moment(createDateObjectFromTime(
                        startHour, startMinute), -1, TimeInputMode.CLOCK);
                focusEndBox(false);
                clockView.reset();
            } else {
                endMoment = new Moment(createDateObjectFromTime(
                        endHour, endMinute), -1, TimeInputMode.CLOCK);
            }
        });

        partOfDayPickerView.setOnPartOfDayChangedListener(val -> {
            if (startTimeFocused) {
                startPartOfDayDrawable.setValue(val);
                startPartOfDay = val;
            } else {
                endPartOfDayDrawable.setValue(val);
                endPartOfDay = val;
            }
            updateNextDayView();
        });
        partOfDayPickerView.setOnPartOfDaySetListener(() -> {
            if (startTimeFocused) {
                startMoment = new Moment(null, startPartOfDay,
                        TimeInputMode.PART_OF_DAY);
                if (!isStartOnly) {
                    focusEndBox(false);
                    clockView.reset();
                }
            } else {
                endMoment = new Moment(null, endPartOfDay,
                        TimeInputMode.PART_OF_DAY);
            }
        });
    }

    public void setMoments(Moment startMoment, Moment endMoment) {
        this.startMoment = startMoment;
        if (this.startMoment != null) {
            if (startMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
                final Pair<Integer, Integer> timePair =
                        getTimePairFromDateObject(startMoment.getDate());
                startTimeView.setText(timeFormatter.format(startMoment.getDate()));
                startHour = timePair.first;
                startMinute = timePair.second;
            } else if (startMoment.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                startPartOfDayDrawable.setValue(startMoment.getPartOfDay());
                startPartOfDay = startMoment.getPartOfDay();
            }
        }

        this.endMoment = endMoment;
        if (this.endMoment != null) {
            if (endMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
                final Pair<Integer, Integer> timePair =
                        getTimePairFromDateObject(endMoment.getDate());
                endTimeView.setText(timeFormatter.format(endMoment.getDate()));
                endHour = timePair.first;
                endMinute = timePair.second;
            } else if (endMoment.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                endPartOfDayDrawable.setValue(endMoment.getPartOfDay());
                endPartOfDay = endMoment.getPartOfDay();
            }
        }

        updateNextDayView();
        focusStartBox(false);
    }

    public void setStartOnly(boolean startOnly) {
        isStartOnly = startOnly;
        endTimeBox.setVisibility(startOnly ? GONE : VISIBLE);
        boxDividerView.setVisibility(startOnly ? GONE : VISIBLE);
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
        if (startTimeFocused) {
            if (reset) resetStartBox();
            startTimePartOfDayView.setVisibility(GONE);
            startTimeView.setVisibility(VISIBLE);
        } else {
            if (reset) resetEndBox();
            endTimePartOfDayView.setVisibility(GONE);
            endTimeView.setVisibility(VISIBLE);
        }
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
        if (startTimeFocused) {
            if (reset) resetStartBox();
            startTimePartOfDayView.setVisibility(VISIBLE);
            startTimeView.setVisibility(GONE);
        } else {
            if (reset) resetEndBox();
            endTimePartOfDayView.setVisibility(VISIBLE);
            endTimeView.setVisibility(GONE);
        }
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

    public Moment getStartMoment() {
        return startMoment;
    }

    public Moment getEndMoment() {
        return endMoment;
    }

    private void focusStartBox(boolean reset) {
        Log.d("TimePickerView", "focusStartBox");
        endTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.tertiaryText));
        startTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.flameDark));
        startTimeFocused = true;

        if (startMoment != null) {
            if (startMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
                if (isClockShown) {
                    if (reset) resetStartBox();
                } else {
                    setShowClockInput();
                }
            } else if (startMoment.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                if (!isClockShown) {
                    if (reset) resetStartBox();
                } else {
                    setShowPartOfDayInput();
                }
            }
        } else {
            setShowClockInput(true);
        }
    }

    private void focusEndBox(boolean reset) {
        Log.d("TimePickerView", "focusEndBox");
        startTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.tertiaryText));
        endTimeLabel.setTextColor(
                ColorUtils.get(getContext(), R.color.flameDark));
        startTimeFocused = false;

        if (endMoment != null) {
            if (endMoment.getTimeInputMode() == TimeInputMode.CLOCK) {
                if (isClockShown) {
                    if (reset) resetEndBox();
                } else {
                    setShowClockInput();
                }
            } else if (endMoment.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                if (!isClockShown) {
                    if (reset) resetEndBox();
                } else {
                    setShowPartOfDayInput();
                }
            }
        } else {
            setShowClockInput(true);
        }
    }

    private void resetStartBox() {
        Log.d("TimePickerView", "resetStartBox");
        startHour = startMinute = startPartOfDay = -1;
        startMoment = null;
        clockView.reset();
        updateNextDayView();
        startTimeView.setText(R.string.mtrl_time_picker_no_start_end_value);
        startPartOfDayDrawable.setValue(0);
    }

    private void resetEndBox() {
        Log.d("TimePickerView", "resetEndBox");
        endHour = endMinute = endPartOfDay = -1;
        endMoment = null;
        clockView.reset();
        updateNextDayView();
        endTimeView.setText(R.string.mtrl_time_picker_no_start_end_value);
        endPartOfDayDrawable.setValue(0);
    }

    private void updateNextDayView() {
        Log.d("updateNextDayView", "startPartOfDay = " + startPartOfDay + " endPartOfDay = " + endPartOfDay);
        boolean userInsertedStart =
                (startHour >= 0 && startMinute >= 0) || startPartOfDay >= 0;
        boolean userInsertedEnd =
                (endHour >= 0 && endMinute >= 0) || endPartOfDay >= 0;
        if (!userInsertedStart || !userInsertedEnd) {
            if (isNextDayVisible) {
                nextDayView.animate().alpha(0.f)
                        .translationY(M.dp(8.f))
                        .setDuration(200L)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                isNextDayVisible = false;
            }
            return;
        }
        if (isStartTimeAfterEndTime()) {
            showNextDayView();
        } else {
            hideNextDayView();
        }
    }

    private boolean isStartTimeAfterEndTime() {
        if (startHour >= 0 && startMinute >= 0 && endHour >= 0 && endMinute >= 0) {
            return endHour < startHour ||
                    (endHour == startHour &&
                            endMinute <= startMinute);
        } else if (startHour >= 0 && startMinute >= 0 && endPartOfDay >= 0) {
            int startPartOfDay = TimeFormattingUtils
                    .getPartOfDayFromTimePair(startHour, startMinute);
            return startPartOfDay >= endPartOfDay;
        } else if (endHour >= 0 && endMinute >= 0 && startPartOfDay >= 0) {
            int endPartOfDay = TimeFormattingUtils
                    .getPartOfDayFromTimePair(endHour, endMinute);
            return startPartOfDay >= endPartOfDay;
        } else if (startPartOfDay >= 0 && endPartOfDay >= 0) {
            return startPartOfDay >= endPartOfDay;
        }
        return false;
    }

    private void showNextDayView() {
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
    }

    private void hideNextDayView() {
        if (!isNextDayVisible) {
            return;
        }
        nextDayView.animate().alpha(0.f).translationY(M.dp(8.f))
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        isNextDayVisible = false;
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
