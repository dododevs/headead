package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.radiobutton.MaterialRadioButton;

import revolver.headead.R;
import revolver.headead.core.model.PainType;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;

public class PainTypePickerFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    private MaterialRadioButton typeAView, typeBView, typeCView;
    private PainType painType;

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
                .setOnClickListener((v) -> typeAView.setChecked(true));
        view.findViewById(R.id.fragment_pain_type_picker_b_label)
                .setOnClickListener((v) -> typeBView.setChecked(true));
        view.findViewById(R.id.fragment_pain_type_picker_c_label)
                .setOnClickListener((v) -> typeCView.setChecked(true));

        view.findViewById(R.id.fragment_pain_type_picker_confirm).setOnClickListener((v) -> {
            if (painType != null) {
                requireRecordHeadacheActivity().animatePainTypeChange(painType);
                requireRecordHeadacheActivity().resetBottomPane();
            }
        });

        final PainType current = requireRecordHeadacheActivity().getPainType();
        if (current != null) {
            switch (current) {
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
            painType = current;
        } else {
            painType = null;
        }
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == typeAView) {
                typeBView.setChecked(false);
                typeCView.setChecked(false);
                painType = PainType.A;
            } else if (buttonView == typeBView) {
                typeAView.setChecked(false);
                typeCView.setChecked(false);
                painType = PainType.B;
            } else if (buttonView == typeCView) {
                typeAView.setChecked(false);
                typeBView.setChecked(false);
                painType = PainType.C;
            }
        }
    }
}
