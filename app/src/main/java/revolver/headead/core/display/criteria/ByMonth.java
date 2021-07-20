package revolver.headead.core.display.criteria;

import android.content.Context;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import revolver.headead.core.display.Header;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;

public class ByMonth extends OrderingCriterion {

    private static SimpleDateFormat monthFormatter =
            new SimpleDateFormat("MMMM", Locale.getDefault());
    private static Calendar calendar = Calendar.getInstance();

    @Override
    public void apply(Context context, List<ListItem> dataset, List<Headache> headaches) {
        calendar = Calendar.getInstance();
        dataset.clear();
        dataset.addAll(headaches);

        int lastMonth = -1;
        for (int i = headaches.size() - 1; i >= 0; i--) {
            final Headache headache = headaches.get(i);
            if (lastMonth == -1) {
                lastMonth = getMonthFromDate(headache.getStartMoment().getDate());
            } else if (getMonthFromDate(headache.getStartMoment().getDate()) != lastMonth) {
                dataset.add(i + 1, new Header(
                        monthFormatter.format(headaches.get(i + 1).getStartMoment().getDate())));
                lastMonth = getMonthFromDate(headache.getStartMoment().getDate());
            }
        }
        dataset.add(0, new Header(monthFormatter.format(
                headaches.get(0).getStartMoment().getDate())));
    }

    private static int getMonthFromDate(final Date date) {
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ByMonth;
    }
}
