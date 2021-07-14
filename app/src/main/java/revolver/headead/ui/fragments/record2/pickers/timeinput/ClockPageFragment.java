package revolver.headead.ui.fragments.record2.pickers.timeinput;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import revolver.headead.R;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;
import revolver.headead.ui.fragments.record2.pickers.TimePickerFragment;

public class ClockPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker_clock_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.fragment_time_picker_mode_switch).setOnClickListener(v -> {
            final Bundle b = new Bundle();
            b.putString("mode", DateTimePickerFragment.TimeInputMode.PART_OF_DAY.name());
            requireParentFragment().getChildFragmentManager()
                    .setFragmentResult(TimePickerFragment.REQUEST_MODE_CHANGE, b);
        });
    }

}
