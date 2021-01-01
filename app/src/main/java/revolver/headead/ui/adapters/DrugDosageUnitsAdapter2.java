package revolver.headead.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import revolver.headead.R;
import revolver.headead.core.model.DrugDosageUnit;
import revolver.headead.util.ui.ViewUtils;

public class DrugDosageUnitsAdapter2 extends RecyclerView.Adapter<DrugDosageUnitsAdapter2.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final ImageView iconView;
        final ImageView selectedView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(8.f).get());

            nameView = v.findViewById(R.id.item_drug_dosage_unit_name);
            iconView = v.findViewById(R.id.item_drug_dosage_unit_icon);
            selectedView = v.findViewById(R.id.item_drug_dosage_unit_selected);
        }
    }

    private Context context;
    private DrugDosageUnit selectedItem;
    private final int quantity;

    private RecyclerView.LayoutManager layoutManager;

    public DrugDosageUnitsAdapter2(final int quantity, final DrugDosageUnit selectedUnit) {
        this.quantity = quantity;
        this.selectedItem = selectedUnit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context = parent.getContext(), R.layout.item_drug_dosage_unit, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DrugDosageUnit unit = DrugDosageUnit.values()[position];
        holder.nameView.setText(context.getResources()
                .getQuantityString(unit.getNameResource(), quantity));
        holder.iconView.setImageResource(unit.getIconResource());

        if (unit == selectedItem) {
            holder.selectedView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedView.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener((v) -> {
            final DrugDosageUnit newUnit =
                    DrugDosageUnit.values()[holder.getAdapterPosition()];
            if (newUnit == selectedItem) {
                return;
            }
            selectedItem = newUnit;
            for (int i = 0; i < getItemCount(); i++) {
                if (i != holder.getAdapterPosition()) {
                    layoutManager.findViewByPosition(i)
                            .findViewById(R.id.item_drug_dosage_unit_selected)
                            .setVisibility(View.GONE);
                }
            }
            holder.selectedView.setVisibility(View.VISIBLE);
        });
        holder.selectedView.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return DrugDosageUnit.values().length;
    }

    public DrugDosageUnit getSelectedItem() {
        return selectedItem;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
}
