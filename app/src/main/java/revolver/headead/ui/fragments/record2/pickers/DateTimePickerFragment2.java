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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_datetime_picker_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                DatePickerFragment.asStartOfPastEpisode(
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

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }
}
