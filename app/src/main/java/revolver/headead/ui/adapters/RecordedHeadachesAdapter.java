package revolver.headead.ui.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.core.display.Header;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.display.criteria.OrderingCriterion;
import revolver.headead.core.display.filters.FilteringCriterion;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.DrugTag;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.PainLocation;
import revolver.headead.core.model.PainType;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.IconUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

public class RecordedHeadachesAdapter extends RecyclerView.Adapter<RecordedHeadachesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout innerView;
        final TextView startTimeView;
        final TextView durationView;
        final TextView painLocationView;
        final TextView painIntensityView;
        final TextView painTypeView;
        final TextView drugsTagView;
        public final LinearLayout painDataView;
        final View dividerView;

        final TextView headerLabelView;

        ViewHolder(View v, int viewType) {
            super(v);

            if (viewType == VIEW_TYPE_HEADER) {
                v.setLayoutParams(ViewUtils.newLayoutParams()
                        .matchParentInWidth().wrapContentInHeight().get());
            } else {
                v.setLayoutParams(ViewUtils.newLayoutParams(ViewGroup.MarginLayoutParams.class)
                        .matchParentInWidth().wrapContentInHeight().get());
            }

            innerView = v.findViewById(R.id.item_recorded_headache_inner);
            startTimeView = v.findViewById(R.id.item_recorded_headache_start);
            durationView = v.findViewById(R.id.item_recorded_headache_duration);
            painLocationView = v.findViewById(R.id.item_recorded_headache_pain_location);
            painIntensityView = v.findViewById(R.id.item_recorded_headache_pain_intensity);
            painTypeView = v.findViewById(R.id.item_recorded_headache_pain_type);
            drugsTagView = v.findViewById(R.id.item_recorded_headache_drugs_tag);
            painDataView = v.findViewById(R.id.item_recorded_headache_pain_data);
            dividerView = v.findViewById(R.id.item_recorded_headache_divider);

            headerLabelView = v.findViewById(R.id.item_recorded_headache_header_label);
        }
    }

    public static SimpleDateFormat startAndEndFormatter =
            new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<Headache> headaches;
    private final List<ListItem> dataset = new ArrayList<>();
    private List<Headache> filteredHeadaches;

    private OrderingCriterion orderingCriterion;
    private FilteringCriterion filteringCriterion;
    private OnHeadacheSelectedListener onHeadacheSelectedListener;
    private OnHeadacheLongClickedListener onHeadacheLongClickedListener;

    private final Context context;

    public RecordedHeadachesAdapter(Context context, final List<Headache> headaches, final FilteringCriterion filteringCriterion, final OrderingCriterion orderingCriterion) {
        this.context = context;
        this.filteringCriterion = filteringCriterion;
        this.orderingCriterion = orderingCriterion;
        setHeadaches(headaches);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context, viewType == VIEW_TYPE_HEADER ?
                R.layout.item_recorded_headache_header :
                    R.layout.item_recorded_headache, null), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ListItem item = this.dataset.get(position);
        if (item instanceof Headache) {
            final Headache headache = (Headache) item;
            holder.startTimeView.setText(
                    startAndEndFormatter.format(headache.getStartMoment().getDate()));
            if (headache.getEndMoment() != null) {
                holder.durationView.setText(buildDurationLabel(
                        context, headache.getStartMoment().getDate(),
                            headache.getEndMoment().getDate()));
            } else {
                holder.durationView.setText(R.string.item_recorded_headache_duration_undefined);
            }
            if (headache.getDrugIntakes() != null && !headache.getDrugIntakes().isEmpty()) {
                setupDrugTag(holder.drugsTagView, headache.getDrugIntakes());
                holder.drugsTagView.setVisibility(View.VISIBLE);
            } else {
                holder.drugsTagView.setVisibility(View.GONE);
            }
            holder.painLocationView.setText(
                    PainLocation.joinMultiple(context, headache.getPainLocations()));
            holder.painIntensityView.setText(String.valueOf(headache.getPainIntensity()));
            holder.painTypeView.setText(PainType.joinMultiple(context, headache.getPainTypes()));
            if (position == getItemCount() - 1 || this.dataset.get(position + 1) instanceof Header) {
                holder.dividerView.setVisibility(View.GONE);
            } else {
                holder.dividerView.setVisibility(View.VISIBLE);
            }

            holder.innerView.setOnClickListener((v) -> {
                if (onHeadacheSelectedListener != null) {
                    onHeadacheSelectedListener.onHeadacheSelected(this.headaches.get(
                            this.headaches.indexOf(this.dataset.get(
                                    holder.getAdapterPosition()))), holder);
                }
            });
            holder.innerView.setOnLongClickListener((v) -> {
                if (onHeadacheLongClickedListener != null) {
                    onHeadacheLongClickedListener.onHeadacheLongClicked(
                            this.headaches.get(this.headaches.indexOf(this.dataset.get(
                                    holder.getAdapterPosition()))), holder);
                }
                return true;
            });
        } else if (item instanceof Header) {
            final Header header = (Header) item;
            holder.headerLabelView.setText(header.getLabel());
        }
    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.dataset.get(position) instanceof Header
                ? VIEW_TYPE_HEADER
                : VIEW_TYPE_ITEM;
    }

    public static String buildDurationLabel(final Context context, final Date start, final Date end) {
        long elapsed = end.getTime() - start.getTime();
        int seconds = (int) (elapsed / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds - hours * 3600) / 60;
        if (hours == 0 && minutes < 1) {
            return context.getString(R.string.item_recorded_headache_duration_less_than_1min);
        } else if (hours == 0) {
            return context.getString(R.string.item_recorded_headache_duration_less_than_1h, minutes);
        } else if (minutes == 0) {
            return context.getString(R.string.item_recorded_headache_duration_no_minutes, hours);
        }
        return context.getString(R.string.item_recorded_headache_duration_hours_and_minutes, hours, minutes);
    }

    private void setupDrugTag(final TextView drugTagView, final List<DrugIntake> drugIntakes) {
        DrugTag drugTag = null;
        for (final DrugIntake drugIntake : drugIntakes) {
            if (drugIntake.getTag() != null) {
                drugTag = drugIntake.getTag();
                break;
            }
        }
        if (drugTag != null) {
            drugTagView.setText(drugTag.getTag());
            drugTagView.setTextColor(drugTag.getColor());
            drugTagView.setCompoundDrawables(null, null,
                    IconUtils.scaledDrawableWithColor(context, R.drawable.ic_medication,
                            M.dp(16.f).intValue(), drugTag.getColor()), null);
        } else {
            drugTagView.setText(String.valueOf(drugIntakes.size()));
            drugTagView.setCompoundDrawables(null, null,
                    IconUtils.scaledDrawableWithResolvedColor(
                            context, R.drawable.ic_medication,
                                M.dp(16.f).intValue(), R.color.blackOlive), null);
        }
        drugTagView.setCompoundDrawablePadding(M.dp(4.f).intValue());
    }

    private void applyFilteringCriterion() {
        if (headaches == null || headaches.isEmpty() || filteringCriterion == null) {
            return;
        }
        this.filteredHeadaches = filteringCriterion.apply(context, this.headaches);
    }

    private void applyOrderingCriterion() {
        if (filteredHeadaches == null || filteredHeadaches.isEmpty() || orderingCriterion == null) {
            return;
        }
        Collections.sort(filteredHeadaches, (headache1, headache2) ->
                headache2.getStartMoment().getDate()
                        .compareTo(headache1.getStartMoment().getDate()));
        orderingCriterion.apply(context, this.dataset, this.filteredHeadaches);
    }

    public void applyFilteringCriterion(final FilteringCriterion filteringCriterion) {
        this.filteringCriterion = filteringCriterion;
        applyFilteringCriterion();
        applyOrderingCriterion();
    }

    public void applyOrderingCriterion(final OrderingCriterion orderingCriterion) {
        this.orderingCriterion = orderingCriterion;
        applyOrderingCriterion();
    }

    public void setOnHeadacheSelectedListener(final OnHeadacheSelectedListener listener) {
        this.onHeadacheSelectedListener = listener;
    }

    public void setOnHeadacheLongClickedListener(final OnHeadacheLongClickedListener listener) {
        this.onHeadacheLongClickedListener = listener;
    }

    public void setHeadaches(final List<Headache> headaches) {
        this.headaches = headaches;
        this.filteredHeadaches = new ArrayList<>(this.headaches);
        applyFilteringCriterion();
        applyOrderingCriterion();
    }

    public interface OnHeadacheSelectedListener {
        void onHeadacheSelected(final Headache headache, final ViewHolder holder);
    }

    public interface OnHeadacheLongClickedListener {
        void onHeadacheLongClicked(final Headache headache, final ViewHolder holder);
    }
}
