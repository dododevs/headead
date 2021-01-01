package revolver.headead.ui.adapters;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import revolver.headead.R;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.util.ui.ViewUtils;

public class DrugPackagingResultsAdapter extends RecyclerView.Adapter<DrugPackagingResultsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView packagingNameView;
        final TextView activePrincipleView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(4.f).get());

            packagingNameView = v.findViewById(R.id.item_drug_packaging_name);
            activePrincipleView = v.findViewById(R.id.item_drug_packaging_active_principle);
        }
    }

    private List<DrugPackaging> drugPackagingFormats;
    private OnDrugPackagingSelectedListener drugPackagingSelectedListener;

    public DrugPackagingResultsAdapter(final List<DrugPackaging> drugPackagingFormats) {
        this.drugPackagingFormats = drugPackagingFormats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_drug_packaging, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DrugPackaging packaging = drugPackagingFormats.get(position);
        holder.packagingNameView.setText(Html.fromHtml(packaging.getPackagingDescription()));
        holder.activePrincipleView.setText(Html.fromHtml(packaging.getActivePrinciple()));
        holder.itemView.setOnClickListener((v) -> {
            if (drugPackagingSelectedListener != null) {
                drugPackagingSelectedListener.onDrugPackagingSelected(
                        drugPackagingFormats.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return drugPackagingFormats != null ? drugPackagingFormats.size() : 0;
    }

    public void updateDrugPackagingFormatsList(final List<DrugPackaging> drugPackagingFormats) {
        this.drugPackagingFormats = drugPackagingFormats;
        notifyDataSetChanged();
    }

    public void setOnDrugPackagingSelectedListener(OnDrugPackagingSelectedListener listener) {
        drugPackagingSelectedListener = listener;
    }

    public interface OnDrugPackagingSelectedListener {
        void onDrugPackagingSelected(final DrugPackaging packaging);
    }
}
