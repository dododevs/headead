package revolver.headead.ui.fragments;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revolver.headead.R;
import revolver.headead.aifa.Aifa;
import revolver.headead.aifa.model.Drug;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.ui.activities.DrugIntakeActivity;
import revolver.headead.ui.activities.DrugLookupActivity;
import revolver.headead.ui.adapters.DrugPackagingResultsAdapter;
import revolver.headead.util.ui.M;

import static revolver.headead.util.logic.Conditions.checkNotNull;

public class DrugPackagingSelectorFragment extends BottomSheetDialogFragment {

    private Drug drug;

    private AppBarLayout toolbarContainer;

    private View loadingView;
    private RecyclerView resultsView;
    private View noFormatsView;
    private DrugPackagingResultsAdapter resultsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drug = requireArguments().getParcelable("drug");
        if (drug == null) {
            closeFragment();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog =
                (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.getBehavior().addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                /* add elevation iff the dialog gets expanded and spans the whole height of the screen */
                if (newState == BottomSheetBehavior.STATE_EXPANDED &&
                        dialog.getBehavior().getExpandedOffset() == 0) {
                    final ValueAnimator animator = ValueAnimator
                            .ofFloat(0, M.dp(4.f))
                            .setDuration(150L);
                    animator.addUpdateListener(animation ->
                            toolbarContainer.setElevation((Float) animation.getAnimatedValue()));
                    animator.start();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (toolbarContainer.getElevation() > 0) {
                        final ValueAnimator animator = ValueAnimator
                                .ofFloat(M.dp(4.f), 0)
                                .setDuration(150L);
                        animator.addUpdateListener(animation ->
                                toolbarContainer.setElevation((Float) animation.getAnimatedValue()));
                        animator.start();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // no-op
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_drug_packaging_selector, container, false);

        resultsView = v.findViewById(R.id.formats);
        resultsView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        resultsView.setAdapter(resultsAdapter = new DrugPackagingResultsAdapter(new ArrayList<>()));
        resultsAdapter.setOnDrugPackagingSelectedListener((drugPackaging) -> {
            dismiss();
            requireActivity().startActivityForResult(
                    new Intent(requireContext(), DrugIntakeActivity.class)
                        .putExtra("id", drugPackaging.getDrugPackagingId()),
                            DrugLookupActivity.REQUEST_DRUG_INTAKE);
        });

        loadingView = v.findViewById(R.id.wheel);
        noFormatsView = v.findViewById(R.id.fragment_drug_packaging_selector_no_formats);

        toolbarContainer = v.findViewById(R.id.toolbar_container);

        startLoading();
        Aifa.getDrugLookupService().findPackagingFormats(
                Aifa.buildDrugPackagingQueryString(drug.getDrugId())
        ).enqueue(new DrugPackagingFormatsResponseCallback());

        return v;
    }

    private void startLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        loadingView.setVisibility(View.GONE);
    }

    private void closeFragment() {
        requireActivity().onBackPressed();
        Toast.makeText(requireContext(), R.string.fragment_drug_packaging_selector_error, Toast.LENGTH_SHORT).show();
    }

    private void updateDrugPackagingFormatsList(final List<DrugPackaging> drugPackagingFormats) {
        if (drugPackagingFormats == null || drugPackagingFormats.isEmpty()) {
            resultsView.setVisibility(View.GONE);
            noFormatsView.setVisibility(View.VISIBLE);
        } else {
            noFormatsView.setVisibility(View.GONE);
            resultsView.setVisibility(View.VISIBLE);
        }
        resultsAdapter.updateDrugPackagingFormatsList(drugPackagingFormats);
    }

    private class DrugPackagingFormatsResponseCallback implements Callback<List<DrugPackaging>> {
        @Override
        public void onResponse(@NonNull Call<List<DrugPackaging>> call, @NonNull Response<List<DrugPackaging>> response) {
            stopLoading();
            if (response.isSuccessful()) {
                updateDrugPackagingFormatsList(response.body());
            } else {
                closeFragment();
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<DrugPackaging>> call, @NonNull Throwable t) {
            stopLoading();
            closeFragment();
        }
    }

    public static DrugPackagingSelectorFragment forDrug(final Drug drug) {
        final DrugPackagingSelectorFragment fragment = new DrugPackagingSelectorFragment();
        final Bundle args = new Bundle();
        args.putParcelable("drug", drug);
        fragment.setArguments(args);
        return fragment;
    }
}
