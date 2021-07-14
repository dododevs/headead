package revolver.headead.ui.fragments.record2;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.core.model.Trigger;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.adapters.TriggerChipsAdapter;
import revolver.headead.util.ui.M;

public class RecordHeadacheExtrasFragment extends Fragment {

    private Map<Trigger, Boolean> triggersStatus;
    private boolean isAuraEnabled, getsWorseWithMovement;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_headache_extras, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Chip selectedTriggersCountView =
                view.findViewById(R.id.fragment_record_headache_extras_trigger_count);
        final RecyclerView chipsContainerView =
                view.findViewById(R.id.fragment_record_headache_extras_triggers);
        chipsContainerView.setLayoutManager(new StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.HORIZONTAL));

        final TriggerChipsAdapter adapter = new TriggerChipsAdapter(
                App.getAllTriggers(), triggersStatus =
                    requireRecordHeadacheActivity().getTriggersStatus());
        if (triggersStatus != null) {
            int activeCount = Collections.frequency(triggersStatus.values(), true);
            selectedTriggersCountView.setText(String.valueOf(activeCount));
            if (activeCount > 0) {
                selectedTriggersCountView.animate()
                        .translationX(0.f)
                        .alpha(1.f)
                        .setDuration(200L)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            }
        }
        adapter.setOnSelectedTriggersChangedListener(new TriggerChipsAdapter.OnSelectedTriggersChangedListener() {
            @Override
            public void onTriggerSelected(@Nullable Trigger trigger, Map<Trigger, Boolean> triggersStatus, int currentlySelected) {
                RecordHeadacheExtrasFragment.this.triggersStatus = triggersStatus;
                selectedTriggersCountView.setText(String.valueOf(currentlySelected));
                if (currentlySelected == 1) {
                    selectedTriggersCountView.animate()
                            .translationX(0.f)
                            .alpha(1.f)
                            .setDuration(200L)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                }
            }

            @Override
            public void onTriggerUnselected(@Nullable Trigger trigger, Map<Trigger, Boolean> triggersStatus, int currentlySelected) {
                RecordHeadacheExtrasFragment.this.triggersStatus = triggersStatus;
                selectedTriggersCountView.setText(String.valueOf(currentlySelected));
                if (currentlySelected == 0) {
                    selectedTriggersCountView.animate()
                            .translationX(M.dp(-16.f))
                            .alpha(0.f)
                            .setDuration(200L)
                            .setInterpolator(new AccelerateInterpolator())
                            .start();
                }
            }
        });
        chipsContainerView.setAdapter(adapter);
        selectedTriggersCountView.setOnCloseIconClickListener(v -> adapter.resetAllTriggers());

        final MaterialCheckBox auraCheckbox =
                view.findViewById(R.id.fragment_record_headache_extras_aura_);
        final MaterialCheckBox movementCheckbox =
                view.findViewById(R.id.fragment_record_headache_extras_movement);
        final ImageView auraIconView =
                view.findViewById(R.id.fragment_record_headache_extras_aura_icon);
        final ImageView movementIconView =
                view.findViewById(R.id.fragment_record_headache_extras_movement_icon);

        auraCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAuraEnabled = isChecked;
            if (isChecked) {
                auraIconView.setImageResource(R.drawable.avd_aura_in);
            } else {
                auraIconView.setImageResource(R.drawable.avd_aura_out);
            }
            ((AnimatedVectorDrawable) auraIconView.getDrawable()).start();
        });
        movementCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getsWorseWithMovement = isChecked;
            if (isChecked) {
                movementIconView.setImageResource(R.drawable.avd_movement_in);
            } else {
                movementIconView.setImageResource(R.drawable.avd_movement_out);
            }
            ((AnimatedVectorDrawable) movementIconView.getDrawable()).start();
        });

        isAuraEnabled = requireRecordHeadacheActivity().isAuraEnabled();
        getsWorseWithMovement = requireRecordHeadacheActivity().getsWorseWithMovement();
        if (isAuraEnabled) {
            auraCheckbox.setChecked(true);
        }
        if (getsWorseWithMovement) {
            movementCheckbox.setChecked(true);
        }
        ((AnimatedVectorDrawable) auraIconView.getDrawable()).start();
        ((AnimatedVectorDrawable) movementIconView.getDrawable()).start();

        view.findViewById(R.id.fragment_record_headache_extras_confirm).setOnClickListener((v) -> {
            requireRecordHeadacheActivity().setTriggersStatus(triggersStatus);
            requireRecordHeadacheActivity().setAuraEnabled(isAuraEnabled);
            requireRecordHeadacheActivity().setGetsWorseWithMovement(getsWorseWithMovement);
            requireRecordHeadacheActivity().resetBottomPane();
        });
    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }
}
