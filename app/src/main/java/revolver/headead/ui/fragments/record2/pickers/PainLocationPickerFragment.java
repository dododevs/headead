package revolver.headead.ui.fragments.record2.pickers;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.RealmList;
import revolver.headead.R;
import revolver.headead.core.model.PainLocation;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;

public class PainLocationPickerFragment extends Fragment {

    private Chip painLocationSxView, painLocationDxView,
            painLocationBilateralView, painLocationBackView;
    private static final ArrayMap<PainLocation, Integer> painLocationToViewMap = new ArrayMap<>();
    private List<PainLocation> painLocations;

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
                if (!painLocations.contains(PainLocation.SX)) {
                    painLocations.add(PainLocation.SX);
                }
            } else {
                painLocations.remove(PainLocation.SX);
            }
        });
        painLocationDxView = view.findViewById(R.id.fragment_pain_location_picker_dx);
        painLocationDxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!painLocations.contains(PainLocation.DX)) {
                    painLocations.add(PainLocation.DX);
                }
            } else {
                painLocations.remove(PainLocation.DX);
            }
        });
        painLocationBilateralView = view.findViewById(R.id.fragment_pain_location_picker_bilateral);
        painLocationBilateralView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!painLocations.contains(PainLocation.BILATERAL)) {
                    painLocations.add(PainLocation.BILATERAL);
                }
            } else {
                painLocations.remove(PainLocation.BILATERAL);
            }
        });
        painLocationBackView = view.findViewById(R.id.fragment_pain_location_picker_back);
        painLocationBackView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!painLocations.contains(PainLocation.BACK)) {
                    painLocations.add(PainLocation.BACK);
                }
            } else {
                painLocations.remove(PainLocation.BACK);
            }
        });
        view.findViewById(R.id.fragment_pain_location_picker_confirm).setOnClickListener((v) -> {
            if (painLocations != null && !painLocations.isEmpty()) {
                requireRecordHeadacheActivity().animatePainLocationChange(painLocations);
                requireRecordHeadacheActivity().resetBottomPane();
            }
        });

        final Chip[] current = getChipsForCurrentSelections();
        if (current != null) {
            painLocations = requireRecordHeadacheActivity().getPainLocations();
            for (final Chip chip : current) {
                chip.setChecked(true);
            }
        } else {
            painLocations = new ArrayList<>();
        }
    }

    private void resetAllChips() {
        painLocationSxView.setChecked(false);
        painLocationDxView.setChecked(false);
        painLocationBilateralView.setChecked(false);
        painLocationBackView.setChecked(false);
    }

    private Chip[] getChipsForCurrentSelections() {
        final List<PainLocation> painLocations =
                requireRecordHeadacheActivity().getPainLocations();
        if (painLocations != null && !painLocations.isEmpty()) {
            final Chip[] chips = new Chip[painLocations.size()];
            for (int i = 0; i < chips.length; i++) {
                if (painLocationToViewMap.containsKey(painLocations.get(i))) {
                    chips[i] = requireView().findViewById(Objects
                            .requireNonNull(painLocationToViewMap.get(painLocations.get(i))));
                }
            }
            return chips;
        }
        return null;
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) Objects.requireNonNull(getActivity());
    }
}
