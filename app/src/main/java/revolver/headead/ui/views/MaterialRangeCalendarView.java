package revolver.headead.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import revolver.headead.R;

public class MaterialRangeCalendarView extends MaterialCalendarView {

    private Date startDate, endDate;

    public MaterialRangeCalendarView(Context context) {
        this(context, null);
    }

    public MaterialRangeCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialRangeCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaterialRangeCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final RecyclerView recyclerView = findViewById(R.id.mtrl_date_picker_calendar);
        recyclerView.setLayoutManager(new GridLayoutManager(
                getContext(), 7, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter = new RangeDaysGridAdapter());
    }

    class RangeDaysGridAdapter extends DaysGridAdapter {
        @Override
        public void onBindViewHolder(@NonNull MaterialCalendarView.DaysGridAdapter.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            if (position >= 7 + getFirstWeekdayOffset()) {
                position -= (getFirstWeekdayOffset() + 7);
                Date itemDate = getDateForDayOfMonth(position + 1);
                if (startDate != null && endDate != null) {
                    if (itemDate.after(startDate) && itemDate.before(endDate)) {
                        holder.itemView.setBackgroundResource(R.drawable.bg_item_day_in_range);
                    } else if (itemDate.equals(startDate) | itemDate.equals(endDate)) {
                        holder.itemView.setBackgroundResource(R.drawable.bg_item_day_selected);
                    } else {
                        holder.itemView.setBackground(null);
                    }
                } else {
                    holder.itemView.setBackground(null);
                }
                holder.itemView.setOnClickListener((v) -> {
                    dayOfMonth = holder.getAdapterPosition() - (getFirstWeekdayOffset() + 7) + 1;
                    if ((startDate == null) == (endDate == null)) {
                        startDate = getDateForDayOfMonth(dayOfMonth);
                        endDate = null;
                    } else if (startDate != null) {
                        endDate = getDateForDayOfMonth(dayOfMonth);
                    }
                    notifyDataSetChanged();
                });
            }
        }
    }
}
