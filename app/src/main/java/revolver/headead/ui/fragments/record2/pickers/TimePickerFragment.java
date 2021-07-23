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

import revolver.headead.R;
import revolver.headead.core.model.DateTimePickerPreset;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.fragments.BackPressAware;
import revolver.headead.ui.views.SingleTimePickerView;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;

public class TimePickerFragment extends Fragment implements BackPressAware {

    public static final String TAG = "timePicker";

    private SingleTimePickerView timePickerView;

    private DateTimePickerPreset preset;
    private Moment startMoment;
    private Moment endMoment;
    private String selecting;

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
        selecting = requireArguments().getString("selecting");

        final FloatingActionButton fab = requireRecordHeadacheActivity().getBottomFabButton();
        fab.setOnClickListener(v -> checkAndProgress());
        fab.show();

        timePickerView = view.findViewById(R.id.time_picker);

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

        final TextView titleView =
                view.findViewById(R.id.fragment_time_picker_title);
        if ("start".equals(selecting)) {
            titleView.setText(R.string.fragment_time_picker_start_title);
            timePickerView.setBoxLabelText(R.string.mtrl_time_picker_start_label);
            timePickerView.setMoment(startMoment);
        } else if ("end".equals(selecting)) {
            titleView.setText(R.string.fragment_time_picker_end_title);
            timePickerView.setBoxLabelText(R.string.mtrl_time_picker_end_label);
            timePickerView.setMoment(endMoment);
        }
    }

    private void checkAndProgress() {
        if ("start".equals(selecting)) {
            startMoment = startMoment.withTime(
                    timePickerView.getSelectedMoment());
            if (startMoment.after(Moment.now())) {
                Snacks.normal(requireView(),
                        DatePickerFragment.getRandomFutureDateErrorString(
                                requireContext()), true);
                return;
            }
            if (preset == DateTimePickerPreset.JUST_ENDED) {
                requireRecordHeadacheActivity().setHeadacheStart(startMoment);
                requireRecordHeadacheActivity().setHeadacheEnd(Moment.now());
                requireRecordHeadacheActivity().onHeadacheMomentsUpdated();
                requireRecordHeadacheActivity().resetBottomPane();
            } else if (preset == DateTimePickerPreset.PAST) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        DatePickerFragment.asEndOfPastEpisode(startMoment, endMoment),
                            DatePickerFragment.TAG,
                                M.dp(448.f).intValue(), true);
            }
        } else if ("end".equals(selecting)) {
            endMoment = endMoment.withTime(
                    timePickerView.getSelectedMoment());
            if (endMoment.after(Moment.now())) {
                Snacks.normal(requireView(),
                        DatePickerFragment.getRandomFutureDateErrorString(
                                requireContext()), true);
                return;
            }
            if (startMoment.after(endMoment)) {
                Snacks.normal(requireView(),
                        R.string.fragment_date_picker_start_after_end, true);
                return;
            }
            requireRecordHeadacheActivity().setHeadacheStart(startMoment);
            requireRecordHeadacheActivity().setHeadacheEnd(endMoment);
            requireRecordHeadacheActivity().onHeadacheMomentsUpdated();
            requireRecordHeadacheActivity().resetBottomPane();
        }
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    @Override
    public boolean onBackPressed() {
        if (preset == DateTimePickerPreset.JUST_ENDED) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    DatePickerFragment.forJustEnded(startMoment),
                        DatePickerFragment.TAG,
                            M.dp(448.f).intValue(), true);
            return true;
        } else if (preset == DateTimePickerPreset.PAST) {
            if ("start".equals(selecting)) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        DatePickerFragment.asStartOfPastEpisode(startMoment, endMoment),
                            DatePickerFragment.TAG,
                                M.dp(448.f).intValue(), true);
            } else if ("end".equals(selecting)) {
                requireRecordHeadacheActivity().startBottomTransitionToFragment(
                        DatePickerFragment.asEndOfPastEpisode(startMoment, endMoment),
                            DatePickerFragment.TAG,
                                M.dp(448.f).intValue(), true);
            }
            return true;
        }
        return false;
    }

    public static TimePickerFragment asStartOfPastEpisode(Moment startMoment, Moment endMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("selecting", "start");
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static TimePickerFragment asEndOfPastEpisode(Moment startMoment, Moment endMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("selecting", "end");
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static TimePickerFragment forJustEnded(Moment startMoment, Moment endMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("selecting", "start");
        args.putString("mode", DateTimePickerPreset.JUST_ENDED.name());
        fragment.setArguments(args);
        return fragment;
    }
}
