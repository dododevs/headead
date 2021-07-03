package revolver.headead.ui.fragments.record2;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import revolver.headead.R;
import revolver.headead.ui.adapters.DrugTagColorPickerAdapter;
import revolver.headead.ui.fragments.SimpleAlertDialogFragment;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.TextUtils;

public class DrugTagCreationFragment extends SimpleAlertDialogFragment {

    private int currentColor;
    private String currentTag;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final View rootView = View.inflate(requireContext(),
                R.layout.dialog_drug_intake_tag, null);
        final RecyclerView colorsListView =
                rootView.findViewById(R.id.dialog_drug_intake_tag_colors);
        colorsListView.setLayoutManager(
                new GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false));

        final DrugTagColorPickerAdapter adapter =
                new DrugTagColorPickerAdapter(requireContext(), null);
        colorsListView.setAdapter(adapter);
        adapter.setOnColorSelectedListener(color -> currentColor = color);
        currentColor = adapter.getColors()[0];

        final EditText tagEntryView = rootView.findViewById(R.id.dialog_drug_intake_tag_tag);
        tagEntryView.addTextChangedListener(new TextUtils.SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getPositiveButton().setEnabled(s.length() <= 8);
                currentTag = s.toString();
            }
        });

        title(getString((R.string.dialog_drug_intake_tag_title)));
        customView(rootView);
        negativeButton(getString(R.string.dialog_alert_simple_negative), null, true);

        super.onViewCreated(view, savedInstanceState);
    }

    public int getCurrentColor() {
        return currentColor;
    }

    public String getCurrentTag() {
        return currentTag;
    }
}
