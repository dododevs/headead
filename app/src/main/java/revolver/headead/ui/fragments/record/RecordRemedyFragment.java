package revolver.headead.ui.fragments.record;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.ui.activities.record.RecordDrugsActivity;

public class RecordRemedyFragment extends BaseRecordFragment {

    private static final int REQUEST_DRUGS = "thePusherHasComeBack".hashCode() & 0xff;

    private List<Bundle> recordedDrugs = new ArrayList<>();
    private MaterialButton drugsSetView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_record_remedy, container, false);

        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> requireActivity().onBackPressed());

        drugsSetView = rootView.findViewById(R.id.fragment_record_remedy_drug_set);
        drugsSetView.setOnClickListener((v) ->
                startActivityForResult(
                        new Intent(requireContext(), RecordDrugsActivity.class)
                                .putParcelableArrayListExtra("drugs",
                                        new ArrayList<>(recordedDrugs)), REQUEST_DRUGS));

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_DRUGS && resultCode == Activity.RESULT_OK && data != null) {
            recordedDrugs = data.getParcelableArrayListExtra("drugs");
            onRecordedDrugsListChanged();
        }
    }

    private void onRecordedDrugsListChanged() {
        if (recordedDrugs != null && !recordedDrugs.isEmpty()) {
            drugsSetView.setBackgroundColor(App.colorPrimary());
            drugsSetView.setTextColor(Color.WHITE);
        } else {
            drugsSetView.setBackgroundColor(Color.WHITE);
            drugsSetView.setTextColor(App.colorPrimary());
        }
    }
}
