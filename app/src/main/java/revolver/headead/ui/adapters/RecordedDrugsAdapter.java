package revolver.headead.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import revolver.headead.R;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugDosageUnit;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.ui.activities.DrugIntakeActivity;
import revolver.headead.util.ui.ViewUtils;

public class RecordedDrugsAdapter extends RecyclerView.Adapter<RecordedDrugsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView drugDosageQuantityView;
        final TextView drugDosageUnitView;
        final ImageView drugDosageUnitIconView;
        final TextView drugNameView;
        final TextView drugPackagingNameView;
        final ImageView removeView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(4.f).get());

            drugDosageQuantityView = v.findViewById(R.id.item_recorded_drug_quantity);
            drugDosageUnitView = v.findViewById(R.id.item_recorded_drug_unit);
            drugDosageUnitIconView = v.findViewById(R.id.item_recorded_drug_unit_icon);
            drugNameView = v.findViewById(R.id.item_recorded_drug_name);
            drugPackagingNameView = v.findViewById(R.id.item_recorded_drug_packaging_name);
            removeView = v.findViewById(R.id.item_recorded_drug_remove);
        }
    }

    private final List<DrugIntake> recordedDrugs;
    private Context context;
    private OnRecordedDrugRemovedListener listener;

    public RecordedDrugsAdapter(final List<DrugIntake> recordedDrugs) {
        this.recordedDrugs = recordedDrugs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context = parent.getContext(), R.layout.item_recorded_drug, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DrugIntake recordedDrug = recordedDrugs.get(position);
        final DrugPackaging drugPackaging = Objects
                .requireNonNull(recordedDrug.getDrugPackaging());
        final int quantity = (int) recordedDrug.getQuantity();
        final DrugDosageUnit unit = recordedDrug.getUnit();

        holder.drugNameView.setText(normalizeText(drugPackaging.getDrugDescription()));
        holder.drugPackagingNameView.setText(normalizeText(drugPackaging.getPackagingDescription()));
        holder.drugDosageUnitIconView.setImageResource(unit.getIconResource());
        holder.drugDosageUnitView.setText(context.getResources()
                .getQuantityString(unit.getNameResource(), quantity));
        holder.removeView.setOnClickListener((v) -> {
            final int pos = holder.getAdapterPosition();
            final DrugIntake drugIntake = recordedDrugs.remove(pos);
            notifyItemRemoved(pos);
            if (listener != null) {
                listener.onRecordedDrugRemoved(drugIntake);
            }
        });

        holder.drugDosageQuantityView.setText(String.format(
                Locale.ITALY, "%s×", buildQuantityString(context, quantity)));
    }

    @Override
    public int getItemCount() {
        return recordedDrugs != null ? recordedDrugs.size() : 0;
    }

    public void setOnRecordedDrugRemovedListener(final OnRecordedDrugRemovedListener listener) {
        this.listener = listener;
    }

    public static String buildQuantityString(Context context, int quantity) {
        if (quantity < 0) {
            final String quantityLabel;
            switch (quantity) {
                case DrugIntakeActivity.FRACTION_12:
                    quantityLabel = context.getString(R.string.fraction12);
                    break;
                case DrugIntakeActivity.FRACTION_13:
                    quantityLabel = context.getString(R.string.fraction13);
                    break;
                case DrugIntakeActivity.FRACTION_14:
                    quantityLabel = context.getString(R.string.fraction14);
                    break;
                case DrugIntakeActivity.ONE:
                    quantityLabel = context.getString(R.string.one);
                    break;
                case DrugIntakeActivity.TWO:
                    quantityLabel = context.getString(R.string.two);
                    break;
                default:
                    quantityLabel = "?";
            }
            return quantityLabel;
        } else {
            return String.valueOf(quantity);
        }
    }

    private static Spanned normalizeText(final String info) {
        if (info != null && !info.isEmpty()) {
            return Html.fromHtml(info);
        }
        return new SpannableString("-");
    }

    public interface OnRecordedDrugRemovedListener {
        void onRecordedDrugRemoved(final DrugIntake bundle);
    }
}
