package revolver.headead.util.misc;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Pair;
import android.view.Gravity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import revolver.headead.R;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.fragments.display.HeadacheDetailBackdropFragment;
import revolver.headead.ui.fragments.record2.pickers.TimeInputMode;
import revolver.headead.ui.views.CenteredImageSpan;
import revolver.headead.ui.views.PartOfDayDrawable;
import revolver.headead.util.ui.IconUtils;
import revolver.headead.util.ui.M;

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

    public static Spanned formatEndTimeWithDayOffset(final Moment start, final Moment end) {
        int days = (int) Math.floor((end.getDate().getTime()
                - start.getDate().getTime()) / 1000. / 3600. / 24.);
        final SpannableStringBuilder builder = new SpannableStringBuilder(
                HeadacheDetailBackdropFragment.endTimeFormatter.format(end.getDate()));
        if (days > 0) {
            builder.append(" ");
            int pos = builder.length();
            builder.append(String.format(Locale.getDefault(), "+%d", days));
            builder.setSpan(new SuperscriptSpan(),
                    pos, pos + 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(new RelativeSizeSpan(0.55f),
                    pos, pos + 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return builder;
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

    public static Date joinDateAndTime(final Date date, final Pair<Integer, Integer> hourMinutePair) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourMinutePair.first);
        calendar.set(Calendar.MINUTE, hourMinutePair.second);
        return calendar.getTime();
    }

    public static Date joinDateAndTime(final Date date, final Date time) {
        final Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        final Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(time);
        dateCalendar.set(Calendar.HOUR_OF_DAY,
                timeCalendar.get(Calendar.HOUR_OF_DAY));
        dateCalendar.set(Calendar.MINUTE,
                timeCalendar.get(Calendar.MINUTE));
        return dateCalendar.getTime();
    }

    public static int getPartOfDayFromDate(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        seconds += hour * 60 * 60 + minute * 60;
        return (int) Math.round(seconds / 86400.);
    }

    public static int getPartOfDayFromTimePair(int hour, int minute) {
        return (int) Math.round((hour * 60 * 60 + minute * 60) / 86400. * 100);
    }

    public static Spanned createStartToEndString(Context context, Moment start, Moment end) {
        Objects.requireNonNull(start);
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        if (start.getTimeInputMode() == TimeInputMode.CLOCK) {
            builder.append(formatPastDateShort(context, start.getDate()));
        } else if (start.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
            PartOfDayDrawable podDrawable =
                    new PartOfDayDrawable(context, start.getPartOfDay());
            podDrawable.setBounds(0, 0, M.dp(24.f).intValue(), M.dp(24.f).intValue());
            builder.append("   ", new CenteredImageSpan(podDrawable),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        builder.append("  ");
        if (end != null) {
            builder.append("   ", new CenteredImageSpan(IconUtils
                    .scaledDrawableWithResolvedColor(
                            context,
                            R.drawable.ic_timespan_past,
                            M.dp(16.f).intValue(),
                            R.color.flameDark
                    )
            ), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("  ");
            if (end.getTimeInputMode() == TimeInputMode.CLOCK) {
                builder.append(formatPastDateShort(context, end.getDate()));
            } else if (end.getTimeInputMode() == TimeInputMode.PART_OF_DAY) {
                PartOfDayDrawable podDrawable =
                        new PartOfDayDrawable(context, end.getPartOfDay());
                podDrawable.setBounds(0, 0, M.dp(24.f).intValue(), M.dp(24.f).intValue());
                builder.append("   ", new CenteredImageSpan(podDrawable),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            builder.append("   ", new CenteredImageSpan(IconUtils
                    .scaledDrawableWithResolvedColor(
                            context,
                            R.drawable.ic_timespan_start,
                            M.dp(16.f).intValue(),
                            R.color.flameDark
                    )
            ), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}
