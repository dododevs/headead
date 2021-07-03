package revolver.headead.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import revolver.headead.R;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.ViewUtils;

public class DrugTagColorPickerAdapter extends RecyclerView.Adapter<DrugTagColorPickerAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView colorView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                    .matchParentInWidth().wrapContentInHeight()
                        .verticalMargin(16.f).horizontalMargin(16.f).get());

            colorView = v.findViewById(R.id.item_drug_tag_color_icon);
        }
    }

    private final Context context;
    private final int[] colors;

    private int selectedColorIndex = 0;
    private OnColorSelectedListener listener;

    public DrugTagColorPickerAdapter(Context context, @Nullable int[] colors) {
        this.context = context;
        if (colors == null || colors.length < 1) {
            colors = generateDefaultColors();
        }
        this.colors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(context, R.layout.item_drug_tag_color, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.colorView.setImageResource(R.drawable.ic_color_circle);
        holder.colorView.setImageTintList(ColorStateList.valueOf(colors[position]));
        holder.colorView.setOnClickListener(v -> {
            if (holder.getAdapterPosition() != selectedColorIndex) {
                int oldSelectedColorIndex = selectedColorIndex;
                selectedColorIndex = holder.getAdapterPosition();
                if (listener != null) {
                    listener.onColorSelected(colors[holder.getAdapterPosition()]);
                }
                notifyItemChanged(oldSelectedColorIndex);
                notifyItemChanged(selectedColorIndex);
            }
        });

        if (holder.getAdapterPosition() == selectedColorIndex) {
            holder.colorView.animate()
                    .scaleX(0.85f)
                    .scaleY(0.85f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(175L)
                    .start();
        } else {
            holder.colorView.animate()
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(200L)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    private int[] generateDefaultColors() {
        return new int[] {
                ColorUtils.get(context, R.color.bittersweetRed),
                ColorUtils.get(context, R.color.coralRed),
                ColorUtils.get(context, R.color.mikadoYellow),
                ColorUtils.get(context, R.color.pastelGreen),
                ColorUtils.get(context, R.color.lightBlue),
                ColorUtils.get(context, R.color.darkBlue),
                ColorUtils.get(context, R.color.liverGray),
                ColorUtils.get(context, R.color.middleGray),
                ColorUtils.get(context, R.color.black),
        };
    }

    public int[] getColors() {
        return colors;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

}