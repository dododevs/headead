package revolver.headead.ui.fragments.record2.pickers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.views.MaterialCalendarView;
import revolver.headead.ui.views.MaterialTimePickerView;
import revolver.headead.ui.views.PartOfDayPickerView;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.Snacks;

import static revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment.DateTimeMode;
import static revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment.TimeInputMode;

public class DateTimePickerFragment2 extends Fragment {

    private static final @StringRes int[] futureDateErrors = {
            R.string.fragment_datetime_picker_future_date_1,
            R.string.fragment_datetime_picker_future_date_2,
            R.string.fragment_datetime_picker_future_date_3,
            R.string.fragment_datetime_picker_future_date_4,
            R.string.fragment_datetime_picker_future_date_5
    };

    private DateTimeMode dateTimeMode = DateTimeMode.DEFAULT;
    private TimeInputMode timeInputMode = TimeInputMode.CLOCK;

    private View collapsedLayoutView;
    private View expandedLayoutView;
    private TextView collapsedDefaultModeView, collapsedCustomModeView, collapsedOngoingModeView;
    private TextView expandedDefaultModeView, expandedCustomModeView, expandedOngoingModeView;

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireRecordHeadacheActivity().getBottomSheetBehavior().addBottomSheetCallback(
                bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
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
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        requireRecordHeadacheActivity().getBottomSheetBehavior()
                .setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (bottomSheetCallback != null) {
            requireRecordHeadacheActivity().getBottomSheetBehavior()
                    .removeBottomSheetCallback(bottomSheetCallback);
            bottomSheetCallback = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_datetime_picker_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /*final View.OnClickListener defaultModeClickListener, customModeClickListener, ongoingModeClickListener;

        defaultModeClickListener = v -> setDateTimeMode(DateTimeMode.DEFAULT);
        customModeClickListener = v -> setDateTimeMode(DateTimeMode.CUSTOM);
        ongoingModeClickListener = v -> setDateTimeMode(DateTimeMode.ONGOING);

        collapsedDefaultModeView = view.findViewById(R.id.fragment_datetime_picker_now);
        collapsedDefaultModeView.setOnClickListener(defaultModeClickListener);
        collapsedCustomModeView = view.findViewById(R.id.fragment_datetime_picker_custom);
        collapsedCustomModeView.setOnClickListener(customModeClickListener);
        collapsedOngoingModeView = view.findViewById(R.id.fragment_datetime_picker_ongoing);
        collapsedOngoingModeView.setOnClickListener(ongoingModeClickListener);
        expandedDefaultModeView = view.findViewById(R.id.fragment_datetime_picker_expanded_now);
        expandedDefaultModeView.setOnClickListener(defaultModeClickListener);
        expandedCustomModeView = view.findViewById(R.id.fragment_datetime_picker_expanded_custom);
        expandedCustomModeView.setOnClickListener(customModeClickListener);
        expandedOngoingModeView = view.findViewById(R.id.fragment_datetime_picker_expanded_ongoing);
        expandedOngoingModeView.setOnClickListener(ongoingModeClickListener);

        if ("start".equals(requireArguments().getString("target"))) {
            collapsedOngoingModeView.setVisibility(View.GONE);
            expandedOngoingModeView.setVisibility(View.GONE);
            view.findViewById(R.id.fragment_datetime_picker_ongoing_selector)
                    .setVisibility(View.GONE);
        }

        */
        collapsedLayoutView = view.findViewById(R.id.fragment_datetime_picker_collapsed);
        expandedLayoutView = view.findViewById(R.id.fragment_datetime_picker_expanded);

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        final View justStartedPresetView =
                view.findViewById(R.id.fragment_datetime_picker_2_preset_just_started);
        final View justEndedPresetView =
                view.findViewById(R.id.fragment_datetime_picker_2_preset_just_ended);
        final View pastEpisodePresetView =
                view.findViewById(R.id.fragment_datetime_picker_2_preset_past_episode);

        justStartedPresetView.setOnClickListener(v -> {

        });
        justEndedPresetView.setOnClickListener(v -> {
            toolbar.setTitle(R.string.fragment_datetime_picker_2_preset_just_ended_title);
            expandedLayoutView.setVisibility(View.VISIBLE);
            requireRecordHeadacheActivity().getBottomSheetBehavior()
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        pastEpisodePresetView.setOnClickListener(v -> {
            toolbar.setTitle(R.string.fragment_datetime_picker_2_preset_past_episode_title);
            expandedLayoutView.setVisibility(View.VISIBLE);
            requireRecordHeadacheActivity().getBottomSheetBehavior()
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        toolbar.setNavigationOnClickListener(v -> {
            collapsedLayoutView.setVisibility(View.VISIBLE);
            requireRecordHeadacheActivity().getBottomSheetBehavior()
                    .setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        /*

        final MaterialCalendarView materialCalendarView =
                view.findViewById(R.id.fragment_datetime_picker_date);
        final MaterialTimePickerView materialTimePickerView =
                view.findViewById(R.id.fragment_datetime_picker_time);
        final MaterialRadioButton timePickerSelectorView =
                view.findViewById(R.id.fragment_datetime_picker_time_header_radio);
        final MaterialRadioButton partOfDayPickerSelectorView =
                view.findViewById(R.id.fragment_datetime_picker_part_of_day_header_radio);
        final TextView timePickerSelectorHeaderView =
                view.findViewById(R.id.fragment_datetime_picker_time_header);
        final TextView partOfDayPickerSelectorHeaderView =
                view.findViewById(R.id.fragment_datetime_picker_part_of_day_header);
        final View timePickerDimmerView =
                view.findViewById(R.id.fragment_datetime_picker_time_dimmer);
        final View partOfDayDimmerView =
                view.findViewById(R.id.fragment_datetime_picker_part_of_day_dimmer);
        final PartOfDayPickerView partOfDayPickerView =
                view.findViewById(R.id.fragment_datetime_picker_part_of_day);

        timePickerSelectorView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                timePickerDimmerView.setVisibility(View.GONE);
                partOfDayPickerSelectorView.setChecked(false);
                timeInputMode = TimeInputMode.CLOCK;
            } else {
                timePickerDimmerView.setVisibility(View.VISIBLE);
            }
        });
        partOfDayPickerSelectorView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                partOfDayDimmerView.setVisibility(View.GONE);
                timePickerSelectorView.setChecked(false);
                timeInputMode = TimeInputMode.PART_OF_DAY;
            } else {
                partOfDayDimmerView.setVisibility(View.VISIBLE);
            }
        });
        timePickerSelectorHeaderView.setOnClickListener(v ->
                timePickerSelectorView.setChecked(!timePickerSelectorView.isChecked()));
        partOfDayPickerSelectorHeaderView.setOnClickListener(v ->
                partOfDayPickerSelectorView.setChecked(!partOfDayPickerSelectorView.isChecked()));

        final View.OnClickListener confirmationListener = (v) -> {
            if (dateTimeMode == DateTimeMode.DEFAULT) {
                returnDateToActivity(new Date());
            } else if (dateTimeMode == DateTimeMode.CUSTOM) {
                if (timeInputMode == TimeInputMode.CLOCK) {
                    if (!materialTimePickerView.isTimeSet()) {
                        Snacks.shorter(view, R.string.fragment_datetime_picker_missing_time, false);
                    } else {
                        final Date date = joinDateAndTime(materialCalendarView.getDate(),
                                materialTimePickerView.getTimePair());
                        if (date.after(new Date())) {
                            Snacks.normal(view, getRandomFutureDateErrorString(), false);
                        } else {
                            returnDateToActivity(date);
                        }
                    }
                } else if (timeInputMode == TimeInputMode.PART_OF_DAY) {
                    if (partOfDayPickerView.isLaterToday()) {
                        Snacks.shorter(view, R.string.fragment_datetime_picker_part_of_day_future, false);
                    } else {
                        final Date date = materialCalendarView.getDate();
                        final float partOfDay = partOfDayPickerView.getPartOfDay();
                        returnPartOfDayToActivity(date, partOfDay);
                    }
                }
            } else if (dateTimeMode == DateTimeMode.ONGOING) {
                returnDateToActivity(null);
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
            if (getCurrentTimeInputModeFromActivity() == TimeInputMode.CLOCK) {
                materialTimePickerView.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                materialTimePickerView.setMinute(calendar.get(Calendar.MINUTE));
                timePickerSelectorView.setChecked(true);
                partOfDayPickerSelectorView.setChecked(false);
            } else {
                partOfDayPickerView.setPartOfDay(getCurrentPartOfDayFromActivity());
                partOfDayPickerSelectorView.setChecked(true);
                timePickerSelectorView.setChecked(false);
            }
            setDateTimeMode(DateTimeMode.CUSTOM);
        } else if (getCurrentDateTimeModeFromActivity() == DateTimeMode.ONGOING) {
            setDateTimeMode(DateTimeMode.ONGOING);
        }*/
    }

    private void returnDateToActivity(final Date newDate) {
        final String target = requireArguments().getString("target");
        if ("start".equals(target)) {
            requireRecordHeadacheActivity().setStartHeadacheDate(newDate, dateTimeMode);
        } else if ("end".equals(target)) {
            requireRecordHeadacheActivity().setEndHeadacheDate(newDate, dateTimeMode);
        }
        requireRecordHeadacheActivity().resetBottomPane();
    }

    private void returnPartOfDayToActivity(final Date date, final float partOfDay) {
        final String target = requireArguments().getString("target");
        if ("start".equals(target)) {
            requireRecordHeadacheActivity().setStartHeadacheDate(
                    date, partOfDay, dateTimeMode, TimeInputMode.PART_OF_DAY);
        } else if ("end".equals(target)) {
            requireRecordHeadacheActivity().setEndHeadacheDate(
                    date, partOfDay, dateTimeMode, TimeInputMode.PART_OF_DAY);
        }
        requireRecordHeadacheActivity().resetBottomPane();
    }

    private Date getCurrentDateFromActivity() {
        final String target = requireArguments().getString("target");
        if ("start".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheStartDate();
        } else if ("end".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheEndDate();
        }
        return null;
    }

    private float getCurrentPartOfDayFromActivity() {
        final String target = requireArguments().getString("target");
        if ("start".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheStartPartOfDay();
        } else if ("end".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheEndPartOfDay();
        }
        return -1;
    }

    private DateTimeMode getCurrentDateTimeModeFromActivity() {
        final String target = requireArguments().getString("target");
        if ("start".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheStartDateTimeMode();
        } else if ("end".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheEndDateTimeMode();
        }
        return null;
    }

    private TimeInputMode getCurrentTimeInputModeFromActivity() {
        final String target = requireArguments().getString("target");
        if ("start".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheStartTimeInputMode();
        } else if ("end".equals(target)) {
            return requireRecordHeadacheActivity().getHeadacheEndTimeInputMode();
        }
        return null;
    }

    private void setDateTimeMode(final DateTimeMode newDateTimeMode) {
        if (newDateTimeMode == DateTimeMode.DEFAULT) {
            collapsedLayoutView.setVisibility(View.VISIBLE);
            requireRecordHeadacheActivity().getBottomSheetBehavior()
                    .setState(BottomSheetBehavior.STATE_COLLAPSED);
            collapsedDefaultModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            collapsedCustomModeView.setTypeface(App.Fonts.Montserrat.Regular);
            collapsedOngoingModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedDefaultModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedCustomModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedOngoingModeView.setTypeface(App.Fonts.Montserrat.Regular);
            dateTimeMode = DateTimeMode.DEFAULT;
        } else if (newDateTimeMode == DateTimeMode.CUSTOM) {
            expandedLayoutView.setVisibility(View.VISIBLE);
            requireRecordHeadacheActivity().getBottomSheetBehavior()
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
            collapsedDefaultModeView.setTypeface(App.Fonts.Montserrat.Regular);
            collapsedCustomModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            collapsedOngoingModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedDefaultModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedCustomModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedOngoingModeView.setTypeface(App.Fonts.Montserrat.Regular);
            dateTimeMode = DateTimeMode.CUSTOM;
        } else if (newDateTimeMode == DateTimeMode.ONGOING) {
            if (dateTimeMode != DateTimeMode.DEFAULT) {
                collapsedLayoutView.setVisibility(View.VISIBLE);
                requireRecordHeadacheActivity().getBottomSheetBehavior()
                        .setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            collapsedDefaultModeView.setTypeface(App.Fonts.Montserrat.Regular);
            collapsedCustomModeView.setTypeface(App.Fonts.Montserrat.Regular);
            collapsedOngoingModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedDefaultModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedCustomModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedOngoingModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            dateTimeMode = DateTimeMode.ONGOING;
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

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) Objects.requireNonNull(getActivity());
    }

    public static DateTimePickerFragment2 asStart() {
        final DateTimePickerFragment2 fragment = new DateTimePickerFragment2();
        final Bundle args = new Bundle();
        args.putString("target", "start");
        fragment.setArguments(args);
        return fragment;
    }

    public static DateTimePickerFragment2 asEnd() {
        final DateTimePickerFragment2 fragment = new DateTimePickerFragment2();
        final Bundle args = new Bundle();
        args.putString("target", "end");
        fragment.setArguments(args);
        return fragment;
    }
}
