package revolver.headead.ui.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.Locale;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.util.ui.Keyboards;
import revolver.headead.util.ui.TextUtils;
import revolver.headead.util.ui.ViewUtils;

public class MaterialTimePickerView extends FrameLayout {

    private EditText hourView, minuteView;
    private Integer hour, minute;

    public MaterialTimePickerView(Context context) {
        this(context, null);
    }

    public MaterialTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialTimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaterialTimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    public boolean isTimeSet() {
        return minute != null && hour != null;
    }

    public Pair<Integer, Integer> getTimePair() {
        return new Pair<>(hour, minute);
    }

    public void setHour(Integer hour) {
        this.hourView.setText(String.format(Locale.getDefault(), "%02d", hour));
    }

    public void setMinute(Integer minute) {
        this.minuteView.setText(String.format(Locale.getDefault(), "%02d", minute));
    }

    private void initialize() {
        final View rootView = View.inflate(getContext(), R.layout.mtrl_time_picker, this);
        rootView.setLayoutParams(ViewUtils.newLayoutParams()
                .matchParentInWidth().wrapContentInHeight().get());
        hourView = rootView.findViewById(R.id.mtrl_time_picker_hour);
        hourView.setTypeface(App.Fonts.Lekton.Bold);
        hourView.addTextChangedListener(new TimeInputWatcher(Field.HOUR));
        minuteView = rootView.findViewById(R.id.mtrl_time_picker_minute);
        minuteView.setTypeface(App.Fonts.Lekton.Bold);
        minuteView.addTextChangedListener(new TimeInputWatcher(Field.MINUTE));
        minuteView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                minuteView.clearFocus();
                Keyboards.hideOnWindowAttached(minuteView);
            }
            return false;
        });
    }

    private class TimeInputWatcher extends TextUtils.SimpleTextWatcher {

        private static final int HOUR_MIN_VALUE = 0, HOUR_MAX_VALUE = 23;
        private static final int MINUTE_MIN_VALUE = 0, MINUTE_MAX_VALUE = 59;

        private final Field fieldType;

        TimeInputWatcher(final Field field) {
            fieldType = field;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                if (fieldType == Field.MINUTE) {
                    hourView.setSelection(hourView.getText().length());
                    hourView.requestFocus();
                }
                return;
            } else if (s.length() > 2) {
                if (fieldType == Field.HOUR) {
                    final CharSequence overflowing = s.subSequence(2, s.length());
                    if (minuteView.getText().length() > 0) {
                        minuteView.getText().replace(0, 1, overflowing);
                    } else {
                        minuteView.getText().append(overflowing);
                    }
                    minuteView.setSelection(1);
                }
                s.delete(2, s.length());
            } else if (s.length() == 2) {
                if (fieldType == Field.HOUR) {
                    minuteView.requestFocus();
                }
            }
            try {
                if (fieldType == Field.MINUTE) {
                    minute = Integer.parseInt(s.toString());
                } else if (fieldType == Field.HOUR) {
                    hour = Integer.parseInt(s.toString());
                }
                if (fieldType == Field.MINUTE && minute > MINUTE_MAX_VALUE) {
                    s.replace(0, s.length(), String.valueOf(minute = MINUTE_MAX_VALUE));
                } else if (fieldType == Field.HOUR && hour > HOUR_MAX_VALUE) {
                    s.replace(0, s.length(), String.valueOf(hour = HOUR_MAX_VALUE));
                }
            } catch (NumberFormatException e) {
                if (fieldType == Field.MINUTE) {
                    s.replace(0, s.length(), String.valueOf(MINUTE_MIN_VALUE));
                } else /* if (fieldType == Field.HOUR) */ {
                    s.replace(0, s.length(), String.valueOf(HOUR_MIN_VALUE));
                }
            }
        }
    }

    private enum Field {
        HOUR, MINUTE
    }
}
