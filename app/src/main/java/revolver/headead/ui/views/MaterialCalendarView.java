package revolver.headead.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import revolver.headead.R;
import revolver.headead.util.misc.ForbiddenMath;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.ViewUtils;

public class MaterialCalendarView extends LinearLayout {

    private static Calendar calendar = Calendar.getInstance();
    private static final List<Integer> weekdays =
            Arrays.asList(
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
                Calendar.SATURDAY,
                Calendar.SUNDAY
            );
    private static final DateFormatSymbols dateSymbols = DateFormatSymbols.getInstance();
    private static final String[] weekdayNames =
            obtainOrderedWeekdays(dateSymbols.getShortWeekdays());
    private static final String[] monthNames = dateSymbols.getMonths();

    int dayOfMonth;
    DaysGridAdapter adapter;

    public MaterialCalendarView(Context context) {
        this(context, null);
    }

    public MaterialCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaterialCalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(VERTICAL);
        initialize();
    }

    public Date getDate() {
        return getDateForDayOfMonth(dayOfMonth);
    }

    public void setDate(final Date date) {
        calendar.setTime(date);
        updateAllFieldsForCurrentCalendar();
    }

    private void initialize() {
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final View rootView = View.inflate(getContext(), R.layout.mtrl_date_picker, this);
        findViewById(R.id.mtrl_date_picker_month_prev).setOnClickListener((v) -> {
            calendar.roll(Calendar.MONTH, false);
            updateAllFieldsForCurrentCalendar();
        });
        findViewById(R.id.mtrl_date_picker_month_next).setOnClickListener((v) -> {
            calendar.roll(Calendar.MONTH, true);
            updateAllFieldsForCurrentCalendar();
        });
        findViewById(R.id.mtrl_date_picker_year_prev).setOnClickListener((v) -> {
            calendar.roll(Calendar.YEAR, false);
            updateAllFieldsForCurrentCalendar();
        });
        findViewById(R.id.mtrl_date_picker_year_next).setOnClickListener((v) -> {
            calendar.roll(Calendar.YEAR, true);
            updateAllFieldsForCurrentCalendar();
        });

        final RecyclerView recyclerView = rootView.findViewById(R.id.mtrl_date_picker_calendar);
        recyclerView.setLayoutManager(new GridLayoutManager(
                getContext(), 7, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter = new DaysGridAdapter());

        updateAllFieldsForCurrentCalendar();
    }

    private void updateAllFieldsForCurrentCalendar() {
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        adapter.notifyDataSetChanged();
        ((TextView) findViewById(R.id.mtrl_date_picker_month))
                .setText(String.valueOf(monthNames[calendar.get(Calendar.MONTH)]));
        ((TextView) findViewById(R.id.mtrl_date_picker_year))
                .setText(String.valueOf(calendar.get(Calendar.YEAR)));
    }

    private int getDayCount() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    int getFirstWeekdayOffset() {
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        int offset = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        calendar.set(Calendar.DAY_OF_MONTH, today);
        return offset < 0 ? ForbiddenMath.floorMod(offset, 7): offset;
    }

    Date getDateForDayOfMonth(int dayOfMonth) {
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date calendarDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, today);
        return calendarDate;
    }

    private static String[] obtainOrderedWeekdays(final String[] weekdayNames) {
        final String[] orderedWeekdayNames = new String[weekdayNames.length];
        int offset = weekdays.indexOf(calendar.getFirstDayOfWeek());
        int j = 0;
        for (int i = offset; i < weekdays.size(); i++, j++) {
            orderedWeekdayNames[j] = weekdayNames[weekdays.get(i)];
        }
        for (int i = 0; i < offset; i++) {
            orderedWeekdayNames[j] = weekdayNames[weekdays.get(i)];
        }
        return orderedWeekdayNames;
    }

    class DaysGridAdapter extends RecyclerView.Adapter<DaysGridAdapter.ViewHolder> {
        private static final int VIEW_TYPE_WEEK_DAY = 0;
        private static final int VIEW_TYPE_DAY = 1;

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView digitView;
            ViewHolder(View v) {
                super(v);
                digitView = v.findViewById(R.id.item_day_digit);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(parent.getContext(),
                    viewType == VIEW_TYPE_WEEK_DAY ? R.layout.item_week_day : R.layout.item_day, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            /* the first seven items are the weekday initials */
            if (position < 7) {
                holder.digitView.setText(weekdayNames[position].substring(0, 1));
                holder.itemView.setLayoutParams(ViewUtils.newLayoutParams(MarginLayoutParams.class)
                        .wrapContentInWidth().wrapContentInHeight().get());
                holder.itemView.setOnClickListener(null);
                holder.itemView.setBackground(null);
            } else if (position < 7 + getFirstWeekdayOffset()) {
                holder.digitView.setText("");
                holder.itemView.setLayoutParams(ViewUtils.newLayoutParams(MarginLayoutParams.class)
                        .matchParentInWidth().wrapContentInHeight()
                            .verticalMargin(8.f).horizontalMargin(8.f).get());
                holder.itemView.setOnClickListener(null);
                holder.itemView.setBackground(null);
            } else if (position >= 7 + getFirstWeekdayOffset()) {
                position -= (getFirstWeekdayOffset() + 7);
                holder.digitView.setText(String.valueOf(position + 1));
                holder.itemView.setLayoutParams(ViewUtils.newLayoutParams(MarginLayoutParams.class)
                        .matchParentInWidth().wrapContentInHeight()
                            .verticalMargin(8.f).get());//.horizontalMargin(8.f).get());
                if (position == dayOfMonth - 1) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_day_selected);
                    holder.digitView.setTextColor(Color.WHITE);
                } else {
                    holder.itemView.setBackground(null);
                    holder.digitView.setTextColor(ColorUtils.get(getContext(), R.color.blackOlive));
                }
                holder.itemView.setOnClickListener((v) -> {
                    int oldDayOfMonth = dayOfMonth;
                    dayOfMonth = holder.getAdapterPosition() - (getFirstWeekdayOffset() + 7) + 1;
                    notifyItemChanged(oldDayOfMonth + getFirstWeekdayOffset() +
                            7 /* first seven dummy positions for the weekdays letters */
                            - 1 /* first dayOfMonth = 1 */);
                    notifyItemChanged(dayOfMonth + getFirstWeekdayOffset() + 7 - 1);
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position < 7 ? VIEW_TYPE_WEEK_DAY : VIEW_TYPE_DAY;
        }

        @Override
        public int getItemCount() {
            return 7 + getFirstWeekdayOffset() + getDayCount();
        }
    }
}
