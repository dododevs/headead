package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Date;

import revolver.headead.R;
import revolver.headead.core.model.DateTimePickerPreset;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.views.MaterialTimePickerView;
import revolver.headead.ui.views.PartOfDayPickerView;
import revolver.headead.util.misc.TimeFormattingUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;

public class EndMomentPickerFragment extends TimePickerFragment {

    private ViewPager2 innerTimePickerPager;

    private DateTimePickerPreset preset;
    private Moment startMoment;
    private Moment endMoment;

    private int daysOffset = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_end_moment_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preset = DateTimePickerPreset.fromString(requireArguments().getString("mode"));
        startMoment = requireArguments().getParcelable("startMoment");
        endMoment = requireArguments().getParcelable("endMoment");

        /*innerTimePickerPager = view.findViewById(R.id.fragment_time_picker)
                .findViewById(R.id.time_input_pager);*/
        innerTimePickerPager.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (timeInputMode == DateTimePickerFragment.TimeInputMode.CLOCK) {
                ((TextView) innerTimePickerPager
                        .findViewById(R.id.fragment_time_picker_clock_page_title))
                        .setText(R.string.fragment_time_picker_end_title);
            } else if (timeInputMode == DateTimePickerFragment.TimeInputMode.PART_OF_DAY) {
                ((TextView) innerTimePickerPager
                        .findViewById(R.id.fragment_time_picker_part_of_day_page_title))
                        .setText(R.string.fragment_time_picker_end_title);
            }
        });

        final MaterialButtonToggleGroup daysToggleView =
                view.findViewById(R.id.fragment_end_moment_picker_days);
        daysToggleView.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            final PartOfDayPickerView partOfDayPickerView =
                    innerTimePickerPager.findViewById(R.id.part_of_day_picker);
            if (isChecked && timeInputMode == DateTimePickerFragment.TimeInputMode.PART_OF_DAY) {
                if (checkedId == R.id.fragment_end_moment_picker_zero_days) {
                    partOfDayPickerView.setBounded(true);
                    partOfDayPickerView.setBound(TimeFormattingUtils
                            .getPartOfDayFromDate(startMoment.getDate()));
                    daysOffset = 0;
                } else if (checkedId == R.id.fragment_end_moment_picker_one_day) {
                    partOfDayPickerView.setBounded(false);
                    daysOffset = 1;
                } else if (checkedId == R.id.fragment_end_moment_picker_two_days) {
                    partOfDayPickerView.setBounded(false);
                    daysOffset = 2;
                } else if (checkedId == R.id.fragment_end_moment_picker_three_days) {
                    partOfDayPickerView.setBounded(false);
                    daysOffset = 3;
                }
            }
        });
    }

    @Override
    void checkAndProgress() {
        Date dateWithTime = endMoment.getDate();
        int partOfDay = -1;
        if (timeInputMode == DateTimePickerFragment.TimeInputMode.CLOCK) {
            final MaterialTimePickerView timePickerView =
                    innerTimePickerPager.findViewById(R.id.clock_picker);
            if (timePickerView.isTimeSet()) {
                dateWithTime = TimeFormattingUtils
                        .joinDateAndTime(startMoment.getDate(), timePickerView.getTimePair());
                dateWithTime = applyDayOffset(dateWithTime);
                if (dateWithTime.after(new Date())) {
                    Snacks.normal(requireView(), getRandomFutureTimeErrorString(), true);
                    return;
                }
            }
        } else if (timeInputMode == DateTimePickerFragment.TimeInputMode.PART_OF_DAY) {
            final PartOfDayPickerView partOfDayPickerView =
                    innerTimePickerPager.findViewById(R.id.part_of_day_picker);
            partOfDay = partOfDayPickerView.getPartOfDay();
        }

        if (preset == DateTimePickerPreset.PAST) {
            requireRecordHeadacheActivity().setHeadacheStart(startMoment);
            requireRecordHeadacheActivity().setHeadacheEnd(
                    new Moment(dateWithTime, partOfDay, timeInputMode));
            requireRecordHeadacheActivity().resetBottomPane();
        }
    }

    private Date applyDayOffset(Date date) {
       return new Date(date.getTime() + daysOffset * 24 * 60 * 60 * 1000);
    }

    public static EndMomentPickerFragment forPastEpisode(Moment startMoment, Moment endMoment) {
        final EndMomentPickerFragment fragment = new EndMomentPickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }
}
