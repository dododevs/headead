package revolver.headead.ui.fragments.record2.pickers;

import android.content.Context;
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

import revolver.headead.R;
import revolver.headead.core.model.DateTimePickerPreset;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.fragments.BackPressAware;
import revolver.headead.ui.views.MaterialCalendarView;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;

public class DatePickerFragment extends Fragment implements BackPressAware {

    public static final String TAG = "datePicker";
    private static final @StringRes int[] futureDateErrors = {
            R.string.fragment_datetime_picker_future_date_1,
            R.string.fragment_datetime_picker_future_date_2,
            R.string.fragment_datetime_picker_future_date_3,
            R.string.fragment_datetime_picker_future_date_4,
            R.string.fragment_datetime_picker_future_date_5
    };

    private MaterialCalendarView calendarView;

    private DateTimePickerPreset preset;
    private Moment startMoment;
    private Moment endMoment;
    private String selecting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preset = DateTimePickerPreset.fromString(requireArguments().getString("mode"));
        startMoment = requireArguments().getParcelable("startMoment");
        endMoment = requireArguments().getParcelable("endMoment");
        selecting = requireArguments().getString("selecting");

        calendarView = view.findViewById(R.id.fragment_date_picker_calendar);
        if ("start".equals(selecting)) {
            if (startMoment != null) {
                calendarView.setDate(startMoment.getDate());
            }
        } else if ("end".equals(selecting)) {
            if (endMoment != null) {
                calendarView.setDate(endMoment.getDate());
            }
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
            Snacks.normal(requireView(),
                    getRandomFutureDateErrorString(
                            requireContext()), true);
            return;
        }
        if ("start".equals(selecting)) {
            startMoment = new Moment(current, -1, TimeInputMode.CLOCK);
        } else if ("end".equals(selecting)) {
            endMoment = new Moment(current, -1, TimeInputMode.CLOCK);
            if (startMoment.after(endMoment)) {
                Snacks.normal(requireView(),
                        R.string.fragment_date_picker_start_after_end, true);
                return;
            }
        }
        if (preset == DateTimePickerPreset.JUST_ENDED) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    TimePickerFragment.forJustEnded(startMoment, endMoment),
                        TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
        } else if (preset == DateTimePickerPreset.PAST) {
            if ("start".equals(selecting)) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        TimePickerFragment.asStartOfPastEpisode(startMoment, endMoment),
                            TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
            } else if ("end".equals(selecting)) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        TimePickerFragment.asEndOfPastEpisode(startMoment, endMoment),
                            TimePickerFragment.TAG, M.dp(480.f).intValue(), true);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (preset == DateTimePickerPreset.PAST) {
            if ("end".equals(selecting)) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        TimePickerFragment.asStartOfPastEpisode(startMoment, endMoment),
                            DatePickerFragment.TAG,
                                M.dp(480.f).intValue(), true);
                return true;
            }
        }
        return false;
    }

    public static String getRandomFutureDateErrorString(Context context) {
        return context.getString(futureDateErrors[(int)
                Math.floor(Math.random() * futureDateErrors.length)]);
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    public static DatePickerFragment asStartOfPastEpisode(Moment startMoment, Moment endMoment) {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("selecting", "start");
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment asEndOfPastEpisode(Moment startMoment, Moment endMoment) {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("selecting", "end");
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment forJustEnded(Moment startMoment) {
        final DatePickerFragment fragment = new DatePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putString("selecting", "start");
        args.putString("mode", DateTimePickerPreset.JUST_ENDED.name());
        fragment.setArguments(args);
        return fragment;
    }
}
