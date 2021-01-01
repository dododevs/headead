package revolver.headead.ui.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

import revolver.headead.R;
import revolver.headead.core.model.Trigger;
import revolver.headead.util.ui.ViewUtils;

public class TriggersAdapter extends RecyclerView.Adapter<TriggersAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView nameView;
        final TextView descriptionView;
        final MaterialCheckBox checkBoxView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight().verticalMargin(4.f).get());

            iconView = v.findViewById(R.id.item_trigger_icon);
            nameView = v.findViewById(R.id.item_trigger_name);
            descriptionView = v.findViewById(R.id.item_trigger_description);
            checkBoxView = v.findViewById(R.id.item_trigger_check);
        }
    }

    private List<Trigger> triggers;
    private List<Trigger> selectedTriggers;
    private OnTriggersChangedListener triggersChangedListener;

    public TriggersAdapter(final List<Trigger> triggers) {
        this.triggers = triggers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_trigger, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Trigger trigger = triggers.get(position);
        holder.iconView.setImageResource(trigger.getIconResource());
        holder.nameView.setText(trigger.getName());
        holder.descriptionView.setText(trigger.getDescription());

        /* workaround to avoid deadlock */
        holder.checkBoxView.setOnCheckedChangeListener(null);
        for (final Trigger selectedTrigger : selectedTriggers) {
            if (selectedTrigger.equals(trigger)) {
                holder.checkBoxView.setChecked(true);
            }
        }

        holder.checkBoxView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (triggersChangedListener == null) {
                return;
            }
            if (isChecked) {
                triggersChangedListener.onTriggerAdded(triggers.get(holder.getAdapterPosition()));
            } else {
                triggersChangedListener.onTriggerRemoved(triggers.get(holder.getAdapterPosition()));
            }
        });
        holder.itemView.setOnClickListener((v) -> holder.checkBoxView.performClick());
    }

    @Override
    public int getItemCount() {
        return triggers != null ? triggers.size() : 0;
    }

    public void setOnTriggersChangedListener(final OnTriggersChangedListener listener) {
        this.triggersChangedListener = listener;
    }

    public void setSelectedTriggers(final List<Trigger> selectedTriggers) {
        this.selectedTriggers = selectedTriggers;
    }

    public interface OnTriggersChangedListener {
        void onTriggerAdded(final Trigger trigger);
        void onTriggerRemoved(final Trigger trigger);
    }
}
