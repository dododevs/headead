package revolver.headead.util.misc;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import revolver.headead.R;

public final class TimeFormattingUtils {

    private static SimpleDateFormat timeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat dateFormatter =
            new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private static SimpleDateFormat shortDateFormatter =
            new SimpleDateFormat("dd MMM", Locale.getDefault());

    public static String formatPastDate(Context context, final Date date) {
        if (isToday(date.getTime())) {
            return context.getString(R.string.today_at, timeFormatter.format(date));
        } else if (isYesterday(date.getTime())) {
            return context.getString(R.string.yesterday_at, timeFormatter.format(date));
        } else if (isTheDayBeforeYesterday(date.getTime())) {
            return context.getString(R.string.day_before_yesterday_at, timeFormatter.format(date));
        } else {
            return context.getString(R.string.date_at_time,
                    dateFormatter.format(date), timeFormatter.format(date));
        }
    }

    public static String formatPastDateShort(Context context, final Date date) {
        if (date.after(new Date())) {
            return null;
        }
        if (isToday(date.getTime())) {
            return timeFormatter.format(date);
        } else {
            return shortDateFormatter.format(date);
        }
    }

    public static boolean isToday(final long when) {
        return DateUtils.isToday(when);
    }

    public static boolean isYesterday(final long when) {
        return isToday(when + DateUtils.DAY_IN_MILLIS);
    }

    public static boolean isTheDayBeforeYesterday(final long when) {
        return isYesterday(when + DateUtils.DAY_IN_MILLIS);
    }

}