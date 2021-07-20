package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import revolver.headead.R;
import revolver.headead.core.model.DateTimePickerPreset;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.fragments.BackPressAware;
import revolver.headead.ui.views.TimePickerView;
import revolver.headead.util.ui.M;

public class TimePickerFragment extends Fragment implements BackPressAware {

    public static final String TAG = "timePicker";

    private DateTimePickerPreset preset;
    private Moment startMoment;
    private Moment endMoment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preset = DateTimePickerPreset.fromString(requireArguments().getString("mode"));
        startMoment = requireArguments().getParcelable("startMoment");
        endMoment = requireArguments().getParcelable("endMoment");

        final FloatingActionButton fab = requireRecordHeadacheActivity().getBottomFabButton();
        fab.setOnClickListener(v -> checkAndProgress());
        fab.show();

        final TimePickerView timePickerView =
                view.findViewById(R.id.time_picker);
        final TextView inputSwitchView =
                view.findViewById(R.id.fragment_time_picker_input_switch);
        inputSwitchView.setOnClickListener(v -> {
            if (timePickerView.isClockShown()) {
                timePickerView.setShowPartOfDayInput();
            } else {
                timePickerView.setShowClockInput();
            }
        });
        timePickerView.setOnTimeInputModeChangedListener(mode -> {
            if (mode == TimeInputMode.CLOCK) {
                inputSwitchView.setText(R.string.fragment_time_picker_use_part_of_day);
            } else if (mode == TimeInputMode.PART_OF_DAY) {
                inputSwitchView.setText(R.string.fragment_time_picker_use_clock);
            }
        });
        timePickerView.setMoments(startMoment, endMoment);
        if (preset == DateTimePickerPreset.JUST_ENDED) {
            timePickerView.setStartOnly(true);
        } else if (preset == DateTimePickerPreset.PAST) {
            timePickerView.setStartOnly(false);
        }
    }

    private void checkAndProgress() {

    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    @Override
    public void onBackPressed() {
        if (preset == DateTimePickerPreset.JUST_ENDED) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    DatePickerFragment.forJustEnded(startMoment),
                        DatePickerFragment.TAG,
                            M.dp(448.f).intValue(), true);
        } else if (preset == DateTimePickerPreset.PAST) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    DatePickerFragment.forPastEpisode(startMoment, endMoment),
                        DatePickerFragment.TAG,
                            M.dp(448.f).intValue(), true);
        }
    }

    public static TimePickerFragment forPastEpisode(Moment startMoment, Moment endMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static TimePickerFragment forJustEnded(Moment startMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putString("mode", DateTimePickerPreset.JUST_ENDED.name());
        fragment.setArguments(args);
        return fragment;
    }
}
