package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import revolver.headead.R;
import revolver.headead.core.model.PainType;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;

public class PainTypePickerFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    private MaterialCheckBox typeAView, typeBView, typeCView;
    private final List<PainType> painTypes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pain_type_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        typeAView = view.findViewById(R.id.fragment_pain_type_picker_a);
        typeAView.setOnCheckedChangeListener(this);
        typeBView = view.findViewById(R.id.fragment_pain_type_picker_b);
        typeBView.setOnCheckedChangeListener(this);
        typeCView = view.findViewById(R.id.fragment_pain_type_picker_c);
        typeCView.setOnCheckedChangeListener(this);
        view.findViewById(R.id.fragment_pain_type_picker_a_label)
                .setOnClickListener((v) -> typeAView.setChecked(!typeAView.isChecked()));
        view.findViewById(R.id.fragment_pain_type_picker_b_label)
                .setOnClickListener((v) -> typeBView.setChecked(!typeBView.isChecked()));
        view.findViewById(R.id.fragment_pain_type_picker_c_label)
                .setOnClickListener((v) -> typeCView.setChecked(!typeCView.isChecked()));

        view.findViewById(R.id.fragment_pain_type_picker_confirm).setOnClickListener((v) -> {
            if (!painTypes.isEmpty()) {
                requireRecordHeadacheActivity().animatePainTypeChange(painTypes);
                requireRecordHeadacheActivity().resetBottomPane();
            }
        });

        final List<PainType> current = requireRecordHeadacheActivity().getPainTypes();
        if (current != null) {
            for (final PainType painType : current) {
                switch (painType) {
                    case A:
                        typeAView.setChecked(true);
                        break;
                    case B:
                        typeBView.setChecked(true);
                        break;
                    case C:
                        typeCView.setChecked(true);
                        break;
                }
            }
        }
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == typeAView) {
            if (painTypes.contains(PainType.A) && !isChecked) {
                painTypes.remove(PainType.A);
            } else if (!painTypes.contains(PainType.A) && isChecked) {
                painTypes.add(PainType.A);
            }
        } else if (buttonView == typeBView) {
            if (painTypes.contains(PainType.B) && !isChecked) {
                painTypes.remove(PainType.B);
            } else if (!painTypes.contains(PainType.B) && isChecked) {
                painTypes.add(PainType.B);
            }
        } else if (buttonView == typeCView) {
            if (painTypes.contains(PainType.C) && !isChecked) {
                painTypes.remove(PainType.C);
            } else if (!painTypes.contains(PainType.C) && isChecked) {
                painTypes.add(PainType.C);
            }
        }
        Collections.sort(painTypes, (p1, p2) -> p1.ordinal() - p2.ordinal());
    }
}
