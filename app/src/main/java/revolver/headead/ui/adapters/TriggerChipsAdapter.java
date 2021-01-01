package revolver.headead.ui.adapters;

import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import revolver.headead.R;
import revolver.headead.core.model.Trigger;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.ViewUtils;

public class TriggerChipsAdapter extends RecyclerView.Adapter<TriggerChipsAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final Chip chip;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .wrapContentInWidth().wrapContentInHeight().horizontalMargin(4.f).get());
            chip = (Chip) v;
        }
    }

    private final List<Trigger> triggers;
    private Map<Trigger, Boolean> triggersStatus;
    private OnSelectedTriggersChangedListener listener;

    public TriggerChipsAdapter(final List<Trigger> triggers,
                               final Map<Trigger, Boolean> triggersStatus) {
        this.triggers = triggers;
        this.triggersStatus = triggersStatus;
        if (this.triggersStatus == null) {
            resetAllTriggers(false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.item_trigger_chip, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Trigger trigger = triggers.get(position);
        holder.chip.setText(trigger.getName());
        holder.chip.setChipIconResource(trigger.getIconResource());
        holder.chip.setCheckable(true);

        final Boolean triggerStatus = triggersStatus.get(trigger);
        holder.chip.setChecked(triggerStatus != null ? triggerStatus : false);
        holder.chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final Trigger currentTrigger = triggers.get(holder.getAdapterPosition());
            triggersStatus.put(currentTrigger, isChecked);
            if (listener != null) {
                if (isChecked) {
                    listener.onTriggerSelected(currentTrigger,
                            triggersStatus, getCurrentlySelectedCount());
                } else {
                    listener.onTriggerUnselected(currentTrigger,
                            triggersStatus, getCurrentlySelectedCount());
                }
            }
        });
        holder.chip.setOnClickListener((v) -> {
            Log.d("click!", triggers.get(holder.getAdapterPosition()).getName());
        });
        holder.chip.setOnLongClickListener((v) -> {
            Log.d("long click!", triggers.get(holder.getAdapterPosition()).getName());
            Snacks.normal(holder.itemView, triggers.get(holder.getAdapterPosition()).getDescription(), true);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return triggers != null ? triggers.size() : 0;
    }

    public void setOnSelectedTriggersChangedListener(final OnSelectedTriggersChangedListener l) {
        this.listener = l;
    }

    public void resetAllTriggers() {
        resetAllTriggers(true);
    }

    private void resetAllTriggers(boolean refresh) {
        this.triggersStatus = new ArrayMap<>();
        for (final Trigger trigger : this.triggers) {
            this.triggersStatus.put(trigger, false);
        }
        if (refresh) {
            notifyDataSetChanged();
            if (listener != null) {
                listener.onTriggerUnselected(null, triggersStatus, 0);
            }
        }
    }

    private int getCurrentlySelectedCount() {
        int i = 0;
        for (final Boolean b : triggersStatus.values()) {
            if (b != null && b) {
                i++;
            }
        }
        return i;
    }

    public interface OnSelectedTriggersChangedListener {
        void onTriggerSelected(@Nullable final Trigger trigger, final Map<Trigger, Boolean> triggersStatus, int currentlySelected);
        void onTriggerUnselected(@Nullable final Trigger trigger, final Map<Trigger, Boolean> triggersStatus, int currentlySelected);
    }
}
