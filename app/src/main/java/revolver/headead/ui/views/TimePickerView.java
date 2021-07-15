package revolver.headead.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class TimePickerView extends LinearLayout {

    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("kk:mm", Locale.getDefault());
    private final ClockView clockView;

    private boolean startTimeFocused = true;
    private int hour, minute;

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

        final TextView startTimeView =
                findViewById(R.id.mtrl_time_picker_start_box_value);
        final TextView endTimeView =
                findViewById(R.id.mtrl_time_picker_end_box_value);
        final TextView startTimeLabel =
                findViewById(R.id.mtrl_time_picker_start_box_label);
        final TextView endTimeLabel =
                findViewById(R.id.mtrl_time_picker_end_box_label);

        clockView = new ClockView(context);

        final View startTimeBox =
                findViewById(R.id.mtrl_time_picker_start_box);
        final View endTimeBox =
                findViewById(R.id.mtrl_time_picker_end_box);
        startTimeBox.setOnClickListener(v -> {
            clockView.reset();
            startTimeView.setText(R.string.mtrl_time_picker_no_start_end_value);
            if (startTimeFocused) {
                return;
            }
            endTimeLabel.setTextColor(
                    ColorUtils.get(context, R.color.tertiaryText));
            startTimeLabel.setTextColor(
                    ColorUtils.get(context, R.color.flameDark));
            startTimeFocused = true;
        });
        endTimeBox.setOnClickListener(v -> {
            clockView.reset();
            endTimeView.setText(R.string.mtrl_time_picker_no_start_end_value);
            if (!startTimeFocused) {
                return;
            }
            startTimeLabel.setTextColor(
                    ColorUtils.get(context, R.color.tertiaryText));
            endTimeLabel.setTextColor(
                    ColorUtils.get(context, R.color.flameDark));
            startTimeFocused = false;
        });

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
            } else {
                endTimeView.setText(timeFormatter.format(c.getTime()));
            }
            this.hour = hour;
            this.minute = minute;
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
}
