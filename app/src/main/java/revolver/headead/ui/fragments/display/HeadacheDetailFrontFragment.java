package revolver.headead.ui.fragments.display;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.PainLocation;
import revolver.headead.core.model.Trigger;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.adapters.RecordedDrugsAdapter;
import revolver.headead.ui.fragments.SimpleAlertDialogFragment;
import revolver.headead.util.ui.IconUtils;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.TextUtils;
import revolver.headead.util.ui.ViewUtils;

public class HeadacheDetailFrontFragment extends Fragment {

    private static SimpleDateFormat dateFormatter =
            new SimpleDateFormat("dd MMMM", Locale.getDefault());
    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public void onDetach() {
        super.onDetach();
        requireRecordHeadacheActivity().deleteBackdropFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headache_detail_front, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Headache headache = requireArguments().getParcelable("headache");
        if (headache == null) {
            return;
        }

        ((TextView) view.findViewById(R.id.fragment_headache_detail_front_pain_location))
                .setText(PainLocation.joinMultiple(requireContext(), headache.getPainLocations()));
        ((TextView) view.findViewById(R.id.fragment_headache_detail_front_pain_intensity))
                .setText(headache.getPainIntensity().getShortStringLabel());
        ((TextView) view.findViewById(R.id.fragment_headache_detail_front_pain_type))
                .setText(headache.getPainType().getShortStringLabel());

        final ChipGroup triggerGroup =
                view.findViewById(R.id.fragment_headache_detail_front_triggers);
        final TextView noTriggersView =
                view.findViewById(R.id.fragment_headache_detail_front_no_triggers);
        final List<Trigger> triggers = headache.getSelectedTriggers();
        if (triggers != null && !triggers.isEmpty()) {
            for (final Trigger trigger : triggers) {
                triggerGroup.addView(createChipForTrigger(trigger), ViewUtils
                        .newLayoutParams().wrapContentInWidth().wrapContentInHeight().get());
            }
            noTriggersView.setVisibility(View.GONE);
            triggerGroup.setVisibility(View.VISIBLE);
        } else {
            triggerGroup.setVisibility(View.GONE);
            noTriggersView.setVisibility(View.VISIBLE);
        }

        final TextView auraLabelView =
                view.findViewById(R.id.fragment_headache_detail_front_aura);
        final ImageView auraIconView =
                view.findViewById(R.id.fragment_headache_detail_front_aura_icon);
        if (headache.isAuraPresent()) {
            auraLabelView.setText(R.string.fragment_headache_detail_front_aura_on);
            auraIconView.setImageResource(R.drawable.avd_aura_in);
        } else {
            auraLabelView.setText(R.string.fragment_headache_detail_front_aura_off);
            auraIconView.setImageResource(R.drawable.avd_aura_out);
        }
        ((AnimatedVectorDrawable) auraIconView.getDrawable()).start();

        final LinearLayout drugIntakesContainer =
                view.findViewById(R.id.fragment_headache_detail_front_drugs_container);
        final TextView noDrugIntakesView =
                view.findViewById(R.id.fragment_headache_detail_front_no_drugs);
        final List<DrugIntake> drugIntakes = headache.getDrugIntakes();
        if (drugIntakes != null && !drugIntakes.isEmpty()) {
            for (final DrugIntake drugIntake : drugIntakes) {
                drugIntakesContainer.addView(createViewForDrugIntake(drugIntake),
                        ViewUtils.newLayoutParams().matchParentInWidth()
                                .wrapContentInHeight().get());
            }
            noDrugIntakesView.setVisibility(View.GONE);
            drugIntakesContainer.setVisibility(View.VISIBLE);
        } else {
            drugIntakesContainer.setVisibility(View.GONE);
            noDrugIntakesView.setVisibility(View.VISIBLE);
        }

        final LinearLayout locationContainer =
                view.findViewById(R.id.fragment_headache_detail_front_geo_container);
        final TextView locationView =
                view.findViewById(R.id.fragment_headache_detail_front_geo);
        final TextView noLocationView =
                view.findViewById(R.id.fragment_headache_detail_front_no_geo);
        if (headache.getLocation() != null) {
            locationView.setText(TextUtils.formatLocationString(headache.getLocation()));
            noLocationView.setVisibility(View.GONE);
            locationContainer.setVisibility(View.VISIBLE);
        } else {
            noLocationView.setVisibility(View.VISIBLE);
            locationContainer.setVisibility(View.GONE);
        }
    }

    private Chip createChipForTrigger(final Trigger trigger) {
        final Chip chip = (Chip) View.inflate(
                requireContext(), R.layout.item_display_trigger_chip, null);
        chip.setText(trigger.getName());
        chip.setChipIcon(IconUtils.drawable(requireContext(), trigger.getIconResource()));
        chip.setOnClickListener((v) -> Snacks.normal(chip, trigger.getDescription(), true));
        return chip;
    }

    private View createViewForDrugIntake(final DrugIntake drugIntake) {
        final View rootView = View.inflate(
                requireContext(), R.layout.item_display_recorded_drug, null);
        final TextView quantityView =
                rootView.findViewById(R.id.item_display_recorded_drug_quantity);
        final TextView unitView =
                rootView.findViewById(R.id.item_display_recorded_drug_unit);
        final TextView nameView =
                rootView.findViewById(R.id.item_display_recorded_drug_name);
        final TextView activePrincipleView =
                rootView.findViewById(R.id.item_display_recorded_drug_active_principle);
        final LinearLayout otherContainer =
                rootView.findViewById(R.id.item_display_recorded_drug_other);
        final TextView dateView =
                rootView.findViewById(R.id.item_display_recorded_drug_date);
        final TextView commentView =
                rootView.findViewById(R.id.item_display_recorded_drug_comment);
        quantityView.setText(getString(
                R.string.fragment_headache_detail_front_drug_intake_quantity,
                    RecordedDrugsAdapter.buildQuantityString(requireContext(),
                            (int) drugIntake.getQuantity())));
        unitView.setText(requireContext().getResources()
                .getQuantityText(drugIntake.getUnit().getNameResource(), 1));
        nameView.setText(new SpannableStringBuilder(Html
                .fromHtml(drugIntake.getDrugPackaging().getDrugDescription()))
                    .append(" • ").append(Html
                        .fromHtml(drugIntake.getDrugPackaging().getPackagingDescription())));
        activePrincipleView.setText(drugIntake.getDrugPackaging().getActivePrinciple());
        dateView.setText(getString(R.string.item_display_recorded_drug_date,
                dateFormatter.format(drugIntake.getIntakeDate()),
                    timeFormatter.format(drugIntake.getIntakeDate())));
        if (drugIntake.getComment() != null && !drugIntake.getComment().isEmpty()) {
            commentView.setText(drugIntake.getComment());
        } else {
            commentView.setText(R.string.item_display_recorded_drug_comment);
        }
        rootView.setOnClickListener((v) -> {
            if (otherContainer.getVisibility() == View.VISIBLE) {
                otherContainer.setVisibility(View.GONE);
            } else if (otherContainer.getVisibility() == View.GONE) {
                otherContainer.setVisibility(View.VISIBLE);
            }
        });
        return rootView;
    }

    public RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    public static HeadacheDetailFrontFragment of(final Headache headache) {
        final HeadacheDetailFrontFragment fragment = new HeadacheDetailFrontFragment();
        final Bundle args = new Bundle();
        args.putParcelable("headache", headache);
        fragment.setArguments(args);
        return fragment;
    }
}
