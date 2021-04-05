package revolver.headead.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugDosageUnit;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.SavedDrug;
import revolver.headead.ui.activities.DrugIntakeActivity;
import revolver.headead.ui.activities.record.RecordDrugsActivity;
import revolver.headead.util.ui.IconUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class KnownDrugsAdapter extends RecyclerView.Adapter<KnownDrugsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView drugDescriptionView;
        final TextView drugPackagingDescriptionView;
        final TextView activePrincipleView;
        final TextView quantityUnitView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(8.f).get());

            drugDescriptionView = v.findViewById(R.id.item_known_drug_drug_name);
            drugPackagingDescriptionView = v.findViewById(R.id.item_known_drug_drug_packaging_name);
            activePrincipleView = v.findViewById(R.id.item_known_drug_active_principle);
            quantityUnitView = v.findViewById(R.id.item_known_drug_quantity_unit);
        }
    }

    private final List<SavedDrug> savedDrugs;
    private final Activity activity;

    public KnownDrugsAdapter(final Activity activity, final List<SavedDrug> savedDrugs) {
        this.activity = activity;
        this.savedDrugs = savedDrugs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(viewGroup.getContext(), R.layout.item_known_drug, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final DrugIntake drugIntake = savedDrugs.get(i).getDrugIntake();
        final DrugPackaging drugPackaging = drugIntake.getDrugPackaging();
        holder.drugDescriptionView.setText(
                Html.fromHtml(drugPackaging.getDrugDescription()));
        holder.drugPackagingDescriptionView.setText(
                Html.fromHtml(drugPackaging.getPackagingDescription()));
        holder.activePrincipleView.setText(
                Html.fromHtml(drugPackaging.getActivePrinciple()));

        final double quantity = drugIntake.getQuantity();
        final DrugDosageUnit unit = drugIntake.getUnit();
        if (unit != null) {
            final Drawable unitIconDrawable = IconUtils.scaledDrawable(
                    activity, unit.getIconResource(), M.dp(16.f).intValue());
            holder.quantityUnitView.setCompoundDrawables(null, null, unitIconDrawable, null);
            holder.quantityUnitView.setText(String.format(Locale.getDefault(), "%s× ",
                    RecordedDrugsAdapter.buildQuantityString(activity, (int) quantity)));
        }

        holder.itemView.setOnClickListener(v -> activity
                .startActivityForResult(new Intent(activity, DrugIntakeActivity.class)
                    .putExtra("id", drugPackaging.getDrugPackagingId()),
                        RecordDrugsActivity.REQUEST_PICK_DRUG_FROM_SAVED));
    }

    @Override
    public int getItemCount() {
        return this.savedDrugs != null ? this.savedDrugs.size() : 0;
    }
}
