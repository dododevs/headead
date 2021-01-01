package revolver.headead.ui.fragments.record2.pickers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;
import java.util.Date;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.ui.activities.DrugIntakeActivity;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.views.MaterialCalendarView;
import revolver.headead.ui.views.MaterialTimePickerView;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;

public class DrugIntakeDateTimePickerFragment extends BottomSheetDialogFragment {

    private static final @StringRes int[] futureDateErrors = {
            R.string.fragment_datetime_picker_future_date_1,
            R.string.fragment_datetime_picker_future_date_2,
            R.string.fragment_datetime_picker_future_date_3,
            R.string.fragment_datetime_picker_future_date_4,
            R.string.fragment_datetime_picker_future_date_5
    };

    private DateTimePickerFragment.DateTimeMode dateTimeMode =
            DateTimePickerFragment.DateTimeMode.DEFAULT;

    private View collapsedLayoutView;
    private View expandedLayoutView;
    private TextView collapsedDefaultModeView, collapsedCustomModeView;
    private TextView expandedDefaultModeView, expandedCustomModeView;

    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    private final BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                expandedLayoutView.setVisibility(View.GONE);
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                collapsedLayoutView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            collapsedLayoutView.setAlpha(1.f - slideOffset);
            expandedLayoutView.setAlpha(slideOffset);
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog =
                new BottomSheetDialog(requireContext(), R.style.RoundedBottomSheetDialog);
        bottomSheetBehavior = dialog.getBehavior();
        bottomSheetBehavior.setPeekHeight(M.dp(128.f).intValue());
        bottomSheetBehavior.setDraggable(false);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drug_intake_date_time_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final View.OnClickListener defaultModeClickListener, customModeClickListener;

        defaultModeClickListener = v -> setDateTimeMode(DateTimePickerFragment.DateTimeMode.DEFAULT);
        customModeClickListener = v -> setDateTimeMode(DateTimePickerFragment.DateTimeMode.CUSTOM);

        collapsedDefaultModeView = view.findViewById(R.id.fragment_datetime_picker_now);
        collapsedDefaultModeView.setOnClickListener(defaultModeClickListener);
        collapsedCustomModeView = view.findViewById(R.id.fragment_datetime_picker_custom);
        collapsedCustomModeView.setOnClickListener(customModeClickListener);
        expandedDefaultModeView = view.findViewById(R.id.fragment_datetime_picker_expanded_now);
        expandedDefaultModeView.setOnClickListener(defaultModeClickListener);
        expandedCustomModeView = view.findViewById(R.id.fragment_datetime_picker_expanded_custom);
        expandedCustomModeView.setOnClickListener(customModeClickListener);

        collapsedLayoutView = view.findViewById(R.id.fragment_datetime_picker_collapsed);
        expandedLayoutView = view.findViewById(R.id.fragment_datetime_picker_expanded);

        final MaterialCalendarView materialCalendarView =
                view.findViewById(R.id.fragment_datetime_picker_date);
        final MaterialTimePickerView materialTimePickerView =
                view.findViewById(R.id.fragment_datetime_picker_time);
        final View.OnClickListener confirmationListener = (v) -> {
            if (dateTimeMode == DateTimePickerFragment.DateTimeMode.DEFAULT) {
                returnDateToActivity(new Date());
                dismiss();
            } else if (dateTimeMode == DateTimePickerFragment.DateTimeMode.CUSTOM) {
                if (!materialTimePickerView.isTimeSet()) {
                    Snacks.shorter(view, R.string.fragment_datetime_picker_missing_time, false);
                } else {
                    final Date date = joinDateAndTime(materialCalendarView.getDate(),
                            materialTimePickerView.getTimePair());
                    if (date.after(new Date())) {
                        Snacks.normal(view, getRandomFutureDateErrorString(), false);
                    } else {
                        returnDateToActivity(date);
                        dismiss();
                    }
                }
            }
        };
        view.findViewById(R.id.fragment_datetime_picker_collapsed_confirm)
                .setOnClickListener(confirmationListener);
        view.findViewById(R.id.fragment_datetime_picker_expanded_confirm)
                .setOnClickListener(confirmationListener);

        final Date currentDate = getCurrentDateFromActivity();
        if (currentDate != null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            materialCalendarView.setDate(currentDate);
            materialTimePickerView.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            materialTimePickerView.setMinute(calendar.get(Calendar.MINUTE));
            view.post(() -> setDateTimeMode(DateTimePickerFragment.DateTimeMode.CUSTOM));
        }
    }

    private void returnDateToActivity(final Date newDate) {
        requireDrugIntakeActivity().setIntakeDate(newDate);
    }

    private Date getCurrentDateFromActivity() {
        return requireDrugIntakeActivity().getIntakeDate();
    }

    private void setDateTimeMode(final DateTimePickerFragment.DateTimeMode newDateTimeMode) {
        if (newDateTimeMode == DateTimePickerFragment.DateTimeMode.DEFAULT) {
            collapsedLayoutView.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            collapsedDefaultModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            collapsedCustomModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedDefaultModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedCustomModeView.setTypeface(App.Fonts.Montserrat.Regular);
            dateTimeMode = DateTimePickerFragment.DateTimeMode.DEFAULT;
        } else if (newDateTimeMode == DateTimePickerFragment.DateTimeMode.CUSTOM) {
            expandedLayoutView.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            collapsedDefaultModeView.setTypeface(App.Fonts.Montserrat.Regular);
            collapsedCustomModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedDefaultModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedCustomModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            dateTimeMode = DateTimePickerFragment.DateTimeMode.CUSTOM;
        }
    }

    private String getRandomFutureDateErrorString() {
        return getString(futureDateErrors[(int) Math.floor(Math.random() * futureDateErrors.length)]);
    }

    private static Date joinDateAndTime(final Date date, final Pair<Integer, Integer> hourMinutePair) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourMinutePair.first);
        calendar.set(Calendar.MINUTE, hourMinutePair.second);
        return calendar.getTime();
    }

    private DrugIntakeActivity requireDrugIntakeActivity() {
        return (DrugIntakeActivity) requireActivity();
    }
}
