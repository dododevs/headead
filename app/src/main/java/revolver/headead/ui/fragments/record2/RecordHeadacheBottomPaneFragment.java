package revolver.headead.ui.fragments.record2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import revolver.headead.R;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.util.ui.M;

public class RecordHeadacheBottomPaneFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_headache_bottom_pane, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.fragment_record_headache_bottom_pane_more).setOnClickListener((v) ->
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    new RecordHeadacheExtrasFragment(), RecordHeadacheActivity2.EXTRAS_TAG,
                        M.dp(584.f).intValue(), true, false)
        );
        view.findViewById(R.id.fragment_record_headache_bottom_pane_next)
                .setOnClickListener((v) -> requireRecordHeadacheActivity().onNextButtonPressed());
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) Objects.requireNonNull(getActivity());
    }
}
