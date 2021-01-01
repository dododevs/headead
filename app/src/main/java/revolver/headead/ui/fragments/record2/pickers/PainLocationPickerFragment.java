package revolver.headead.ui.fragments.record2.pickers;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.Objects;

import revolver.headead.R;
import revolver.headead.core.model.PainLocation;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;

public class PainLocationPickerFragment extends Fragment {

    private Chip painLocationSxView, painLocationDxView,
            painLocationBilateralView, painLocationBackView;
    private static ArrayMap<PainLocation, Integer> painLocationToViewMap = new ArrayMap<>();
    private PainLocation painLocation;

    static {
        painLocationToViewMap.put(PainLocation.SX, R.id.fragment_pain_location_picker_sx);
        painLocationToViewMap.put(PainLocation.DX, R.id.fragment_pain_location_picker_dx);
        painLocationToViewMap.put(PainLocation.BILATERAL, R.id.fragment_pain_location_picker_bilateral);
        painLocationToViewMap.put(PainLocation.BACK, R.id.fragment_pain_location_picker_back);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pain_location_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        painLocationSxView = view.findViewById(R.id.fragment_pain_location_picker_sx);
        painLocationSxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resetAllChips();
                painLocationSxView.setChecked(true);
                painLocation = PainLocation.SX;
            } else {
                painLocation = null;
            }
        });
        painLocationDxView = view.findViewById(R.id.fragment_pain_location_picker_dx);
        painLocationDxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resetAllChips();
                painLocationDxView.setChecked(true);
                painLocation = PainLocation.DX;
            } else {
                painLocation = null;
            }
        });
        painLocationBilateralView = view.findViewById(R.id.fragment_pain_location_picker_bilateral);
        painLocationBilateralView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resetAllChips();
                painLocationBilateralView.setChecked(true);
                painLocation = PainLocation.BILATERAL;
            } else {
                painLocation = null;
            }
        });
        painLocationBackView = view.findViewById(R.id.fragment_pain_location_picker_back);
        painLocationBackView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resetAllChips();
                painLocationBackView.setChecked(true);
                painLocation = PainLocation.BACK;
            } else {
                painLocation = null;
            }
        });
        view.findViewById(R.id.fragment_pain_location_picker_confirm).setOnClickListener((v) -> {
            if (painLocation != null) {
                requireRecordHeadacheActivity().animatePainLocationChange(painLocation);
                requireRecordHeadacheActivity().resetBottomPane();
            }
        });

        final Chip current = getChipForCurrentSelection();
        if (current != null) {
            current.setChecked(true);
            painLocation = requireRecordHeadacheActivity().getPainLocation();
        } else {
            painLocation = null;
        }
    }

    private void resetAllChips() {
        painLocationSxView.setChecked(false);
        painLocationDxView.setChecked(false);
        painLocationBilateralView.setChecked(false);
        painLocationBackView.setChecked(false);
    }

    private Chip getChipForCurrentSelection() {
        final PainLocation painLocation = requireRecordHeadacheActivity().getPainLocation();
        if (painLocation != null && painLocationToViewMap.containsKey(painLocation)) {
            return (Chip) requireView().findViewById(
                    Objects.requireNonNull(painLocationToViewMap.get(painLocation)));
        }
        return null;
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) Objects.requireNonNull(getActivity());
    }
}
