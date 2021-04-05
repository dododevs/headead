package revolver.headead.ui.fragments.record2.pickers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.core.model.SavedDrug;
import revolver.headead.ui.activities.record.RecordDrugsActivity;
import revolver.headead.ui.adapters.KnownDrugsAdapter;
import revolver.headead.util.ui.M;

import static revolver.headead.ui.activities.record.RecordHeadacheActivity2.createBottomPaneRoundedBackground;

public class KnownDrugsPickerFragment extends Fragment {

    private View collapsedLayoutView;
    private View expandedLayoutView;
    private TextView collapsedSearchModeView, collapsedSavedModeView;
    private TextView expandedSearchModeView, expandedSavedModeView;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bottomSheetBehavior = requireRecordDrugsActivity().getBottomSheetBehavior();
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    expandedLayoutView.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheet.setBackground(createBottomPaneRoundedBackground());
                    collapsedLayoutView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                collapsedLayoutView.setAlpha(1.f - slideOffset);
                expandedLayoutView.setAlpha(slideOffset);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_known_drugs_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final View.OnClickListener searchModeClickListener, savedModeClickListener;

        searchModeClickListener = v -> setShowSaved(false);
        savedModeClickListener = v -> setShowSaved(true);

        collapsedSearchModeView = view.findViewById(R.id.fragment_known_drugs_picker_search);
        collapsedSearchModeView.setOnClickListener(searchModeClickListener);
        collapsedSavedModeView = view.findViewById(R.id.fragment_known_drugs_picker_saved);
        collapsedSavedModeView.setOnClickListener(savedModeClickListener);
        expandedSearchModeView = view.findViewById(R.id.fragment_known_drugs_picker_expanded_search);
        expandedSearchModeView.setOnClickListener(searchModeClickListener);
        expandedSavedModeView = view.findViewById(R.id.fragment_known_drugs_picker_expanded_saved);
        expandedSavedModeView.setOnClickListener(savedModeClickListener);

        collapsedLayoutView = view.findViewById(R.id.fragment_known_drugs_picker_collapsed);
        expandedLayoutView = view.findViewById(R.id.fragment_known_drugs_picker_expanded);
        expandedLayoutView.getLayoutParams().height = M.screenHeight();

        final List<SavedDrug> savedDrugs = App.getDefaultRealm()
                .copyFromRealm(App.getDefaultRealm().where(SavedDrug.class).findAll());
        final RecyclerView savedListView =
                view.findViewById(R.id.fragment_known_drugs_picker_expanded_list);
        savedListView.setLayoutManager(new LinearLayoutManager(
                requireContext(), RecyclerView.VERTICAL, false));
        savedListView.setAdapter(new KnownDrugsAdapter(requireActivity(), savedDrugs));

        if (savedDrugs.isEmpty()) {
            savedListView.setVisibility(View.GONE);
            view.findViewById(R.id.fragment_known_drugs_picker_expanded_no_items)
                    .setVisibility(View.VISIBLE);
        } else {
            savedListView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.fragment_known_drugs_picker_expanded_no_items)
                    .setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("showingSaved",
                bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                        bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            setShowSaved(savedInstanceState.getBoolean("showingSaved"));
        }
    }

    public void setShowSaved(boolean show) {
        if (show) {
            expandedLayoutView.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            collapsedSearchModeView.setTypeface(App.Fonts.Montserrat.Regular);
            collapsedSavedModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedSearchModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedSavedModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
        } else {
            collapsedLayoutView.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            collapsedSearchModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            collapsedSavedModeView.setTypeface(App.Fonts.Montserrat.Regular);
            expandedSearchModeView.setTypeface(App.Fonts.Montserrat.SemiBold);
            expandedSavedModeView.setTypeface(App.Fonts.Montserrat.Regular);
        }
    }

    private RecordDrugsActivity requireRecordDrugsActivity() {
        return (RecordDrugsActivity) requireActivity();
    }
}
