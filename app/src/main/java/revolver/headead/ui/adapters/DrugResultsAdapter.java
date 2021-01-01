package revolver.headead.ui.adapters;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import revolver.headead.R;
import revolver.headead.aifa.model.Drug;
import revolver.headead.ui.fragments.DrugPackagingSelectorFragment;
import revolver.headead.util.ui.ViewUtils;

public class DrugResultsAdapter extends RecyclerView.Adapter<DrugResultsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView drugNameView;
        final TextView drugMakerView;
        final TextView drugCodeView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(4.f).get());

            drugNameView = v.findViewById(R.id.item_drug_name);
            drugMakerView = v.findViewById(R.id.item_drug_maker);
            drugCodeView = v.findViewById(R.id.item_drug_code);
        }
    }

    private final FragmentManager fragmentManager;
    private List<Drug> drugs;

    public DrugResultsAdapter(final FragmentManager fm, final List<Drug> drugs) {
        this.fragmentManager = fm;
        this.drugs = drugs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_drug, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Drug drug = drugs.get(position);
        holder.drugNameView.setText(Html.fromHtml(drug.getDrugDescription()));
        holder.drugMakerView.setText(Html.fromHtml(drug.getDrugMaker()));
        holder.drugCodeView.setText(drug.getDrugId());
        holder.itemView.setOnClickListener((v) ->
                (DrugPackagingSelectorFragment.forDrug(
                        drugs.get(holder.getAdapterPosition()))).show(fragmentManager, ""));
    }

    @Override
    public int getItemCount() {
        return drugs != null ? drugs.size() : 0;
    }

    public void updateDrugList(final List<Drug> drugs) {
        this.drugs = drugs;
        notifyDataSetChanged();
    }
}
