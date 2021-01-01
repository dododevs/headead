package revolver.headead.ui.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;

import revolver.headead.R;
import revolver.headead.core.model.DrugDosageUnit;

public class DrugDosageUnitsAdapter implements ListAdapter {

    private static class ViewHolder {
        private View itemView;
        private TextView nameView;
        private ImageView iconView;
        private MaterialRadioButton radioButton;
    }

    private Context context;
    private int quantity;

    private List<DataSetObserver> observers = new ArrayList<>();
    private DrugDosageUnit selectedItem;

    public DrugDosageUnitsAdapter(Context context, int quantity, @Nullable DrugDosageUnit selected) {
        this.context = context;
        this.quantity = quantity;
        this.selectedItem = selected;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public int getCount() {
        return DrugDosageUnit.values().length;
    }

    @Override
    public DrugDosageUnit getItem(int position) {
        return DrugDosageUnit.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return DrugDosageUnit.values()[position].ordinal();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_drug_dosage_unit, null);
            viewHolder.itemView = convertView;
            viewHolder.nameView = convertView.findViewById(R.id.item_drug_dosage_unit_name);
            viewHolder.iconView = convertView.findViewById(R.id.item_drug_dosage_unit_icon);
            viewHolder.radioButton = convertView.findViewById(R.id.item_drug_dosage_unit_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameView.setText(context.getResources().getQuantityString(
                getItem(position).getNameResource(), quantity));
        viewHolder.iconView.setImageResource(getItem(position).getIconResource());

        if (getItem(position) == selectedItem) {
            viewHolder.radioButton.setChecked(true);
        } else {
            viewHolder.radioButton.setChecked(false);
        }

        viewHolder.itemView.setOnClickListener((v) -> {
            selectedItem = getItem(position);
            for (final DataSetObserver observer : observers) {
                observer.onChanged();
            }
        });
        viewHolder.radioButton.setClickable(false);

        return convertView;
    }

    public DrugDosageUnit getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
