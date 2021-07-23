package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;

import revolver.headead.R;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.util.ui.M;

public class DateTimePickerFragment2 extends Fragment {

    private static final @StringRes int[] futureDateErrors = {
            R.string.fragment_datetime_picker_future_date_1,
            R.string.fragment_datetime_picker_future_date_2,
            R.string.fragment_datetime_picker_future_date_3,
            R.string.fragment_datetime_picker_future_date_4,
            R.string.fragment_datetime_picker_future_date_5
    };

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
        final View justStartedPresetView =
                view.findViewById(R.id.fragment_datetime_picker_2_preset_just_started);
        final View justEndedPresetView =
                view.findViewById(R.id.fragment_datetime_picker_2_preset_just_ended);
        final View pastEpisodePresetView =
                view.findViewById(R.id.fragment_datetime_picker_2_preset_past_episode);

        justStartedPresetView.setOnClickListener(v -> applyJustStartedPreset());
        justEndedPresetView.setOnClickListener(v -> applyJustEndedPreset());
        pastEpisodePresetView.setOnClickListener(v -> applyPastEpisodePreset());
    }

    private void applyJustStartedPreset() {
        returnMomentsToActivity(Moment.now(), null);
    }

    private void applyJustEndedPreset() {
        requireRecordHeadacheActivity().startBottomTransitionToFragment(
                DatePickerFragment.forJustEnded(
                        requireRecordHeadacheActivity().getHeadacheStart()),
                DatePickerFragment.TAG,
                M.dp(448.f).intValue(),
                true
        );
    }

    private void applyPastEpisodePreset() {
        requireRecordHeadacheActivity().startBottomTransitionToFragment(
                DatePickerFragment.forPastEpisode(
                        requireRecordHeadacheActivity().getHeadacheStart(),
                            requireRecordHeadacheActivity().getHeadacheEnd()),
                DatePickerFragment.TAG,
                M.dp(448.f).intValue(),
                true
        );
    }

    private void returnMomentsToActivity(final Moment start, final Moment end) {
        requireRecordHeadacheActivity().setHeadacheStart(start);
        requireRecordHeadacheActivity().setHeadacheEnd(end);
        requireRecordHeadacheActivity().onHeadacheMomentsUpdated();
        requireRecordHeadacheActivity().resetBottomPane();
    }

    private Moment getCurrentStartMomentFromActivity() {
        return requireRecordHeadacheActivity().getHeadacheStart();
    }

    private Moment getCurrentEndMomentFromActivity() {
        return requireRecordHeadacheActivity().getHeadacheEnd();
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }
}
