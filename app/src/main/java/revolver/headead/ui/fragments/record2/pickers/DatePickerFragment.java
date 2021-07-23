package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.Objects;

import revolver.headead.R;
import revolver.headead.core.model.DateTimePickerPreset;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.views.MaterialCalendarView;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;

public class DatePickerFragment extends Fragment {

    public static final String TAG = "datePicker";
    private static final @StringRes int[] futureDateErrors = {
            R.string.fragment_datetime_picker_future_date_1,
            R.string.fragment_datetime_picker_future_date_2,
            R.string.fragment_datetime_picker_future_date_3,
            R.string.fragment_datetime_picker_future_date_4,
            R.string.fragment_datetime_picker_future_date_5
    };

    private MaterialCalendarView calendarView;

    private DateTimePickerPreset preset, originalPreset;
    private Moment startMoment;
    private Moment endMoment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preset = DateTimePickerPreset.fromString(requireArguments().getString("mode"));
        originalPreset = DateTimePickerPreset.fromString(
                requireArguments().getString("originalMode"));
        startMoment = requireArguments().getParcelable("startMoment");
        endMoment = requireArguments().getParcelable("endMoment");

        final TextView titleView =
                view.findViewById(R.id.fragment_date_picker_title);
        if (preset == DateTimePickerPreset.CUSTOM_DAY_OFFSET) {
            titleView.setText(R.string.fragment_date_picker_end_title);
        }

        calendarView = view.findViewById(R.id.fragment_date_picker_calendar);
        if (startMoment != null) {
            calendarView.setDate(startMoment.getDate());
        }

        final TextView todayLabel = view.findViewById(R.id.fragment_date_picker_label);
        todayLabel.setOnClickListener(v -> calendarView.setDate(new Date()));

        final FloatingActionButton fab = requireRecordHeadacheActivity().getBottomFabButton();
        fab.setOnClickListener(v -> checkAndProgress());
        fab.show();
    }

    private void checkAndProgress() {
        final Date current = calendarView.getDate();
        if (current.after(new Date())) {
            Snacks.normal(requireView(), getRandomFutureDateErrorString(), true);
            return;
        }
        if (preset == DateTimePickerPreset.JUST_ENDED) {
            startMoment = new Moment(current, -1, TimeInputMode.CLOCK);
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    TimePickerFragment.forJustEnded(startMoment),
                        TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
        } else if (preset == DateTimePickerPreset.PAST) {
            startMoment = new Moment(current, -1, TimeInputMode.CLOCK);
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    TimePickerFragment.forPastEpisode(startMoment, endMoment),
                        TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
        } else if (preset == DateTimePickerPreset.CUSTOM_DAY_OFFSET) {
            if (current.before(startMoment.getDate())) {
                Snacks.normal(requireView(),
                        R.string.fragment_date_picker_end_before_start, true);
                return;
            }
            final Bundle result = new Bundle();
            result.putSerializable("date", current);
            getParentFragmentManager().setFragmentResult(
                    TimePickerFragment.REQUEST_CUSTOM_DAY_OFFSET, result);
            if (originalPreset == DateTimePickerPreset.JUST_ENDED) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        TimePickerFragment.forJustEnded(startMoment),
                            TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
            } else if (originalPreset == DateTimePickerPreset.PAST) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        TimePickerFragment.forPastEpisode(startMoment, endMoment),
                            TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
            }
        }
    }

    private String getRandomFutureDateErrorString() {
        return getString(futureDateErrors[(int) Math.floor(Math.random() * futureDateErrors.length)]);
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    public static DatePickerFragment forPastEpisode(Moment startMoment, Moment endMoment) {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment forJustEnded(Moment startMoment) {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putString("mode", DateTimePickerPreset.JUST_ENDED.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment forCustomDayOffset(DateTimePickerPreset preset,
                                                        Moment startMoment,
                                                        Moment endMoment) {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("mode", DateTimePickerPreset.CUSTOM_DAY_OFFSET.name());
        args.putString("originalMode", preset.name());
        fragment.setArguments(args);
        return fragment;
    }
}
