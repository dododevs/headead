package revolver.headead.ui.fragments.record2.pickers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Date;

import revolver.headead.R;
import revolver.headead.core.model.DateTimePickerPreset;
import revolver.headead.core.model.Moment;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.fragments.BackPressAware;
import revolver.headead.ui.fragments.record2.pickers.timeinput.ClockPageFragment;
import revolver.headead.ui.fragments.record2.pickers.timeinput.PartOfDayPageFragment;
import revolver.headead.ui.views.ClockView;
import revolver.headead.ui.views.MaterialTimePickerView;
import revolver.headead.ui.views.PartOfDayPickerView;
import revolver.headead.util.misc.TimeFormattingUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;

public class TimePickerFragment extends Fragment
        implements BackPressAware, FragmentResultListener {

    public static final String TAG = "timePicker";
    public static final String REQUEST_MODE_CHANGE = "howYouWannaTellMeWhen";
    private static final @StringRes int[] futureTimeErrors = {
            R.string.fragment_time_picker_future_time_1,
            R.string.fragment_time_picker_future_time_2,
            R.string.fragment_time_picker_future_time_3
    };

    private ViewPager2 timeInputPager;

    DateTimePickerFragment.TimeInputMode timeInputMode =
            DateTimePickerFragment.TimeInputMode.CLOCK;

    private DateTimePickerPreset preset;
    private Moment startMoment;
    private Moment endMoment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        preset = DateTimePickerPreset.fromString(requireArguments().getString("mode"));
        startMoment = requireArguments().getParcelable("startMoment");
        endMoment = requireArguments().getParcelable("endMoment");

        final FloatingActionButton fab = requireRecordHeadacheActivity().getBottomFabButton();
        fab.setOnClickListener(v -> checkAndProgress());
        fab.show();

        timeInputPager = view.findViewById(R.id.time_input_pager);
        timeInputPager.setUserInputEnabled(false);
        timeInputPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new ClockPageFragment() : new PartOfDayPageFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        getChildFragmentManager().setFragmentResultListener(
                REQUEST_MODE_CHANGE, getViewLifecycleOwner(), this);

        timeInputPager.postDelayed(() -> {
            final ClockView clockView = view.findViewById(R.id.clock);
            clockView.setShowMinutes();
        }, 3000L);
    }

    void checkAndProgress() {
        Date dateWithTime = startMoment.getDate();
        int partOfDay = -1;
        if (timeInputMode == DateTimePickerFragment.TimeInputMode.CLOCK) {
            final MaterialTimePickerView timePickerView =
                    requireView().findViewById(R.id.clock_picker);
            if (timePickerView.isTimeSet()) {
                dateWithTime = TimeFormattingUtils
                        .joinDateAndTime(startMoment.getDate(), timePickerView.getTimePair());
                if (dateWithTime.after(new Date())) {
                    Snacks.normal(requireView(), getRandomFutureTimeErrorString(), true);
                    return;
                }
            } else {
                return;
            }
        } else if (timeInputMode == DateTimePickerFragment.TimeInputMode.PART_OF_DAY) {
            final PartOfDayPickerView partOfDayPickerView =
                    requireView().findViewById(R.id.part_of_day_picker);
            partOfDay = partOfDayPickerView.getPartOfDay();
        }

        if (preset == DateTimePickerPreset.JUST_ENDED) {
            requireRecordHeadacheActivity().setHeadacheStart(
                    new Moment(dateWithTime, partOfDay, timeInputMode));
            requireRecordHeadacheActivity().setHeadacheEnd(
                    new Moment(new Date(), -1, DateTimePickerFragment.TimeInputMode.CLOCK));
            requireRecordHeadacheActivity().resetBottomPane();
        } else if (preset == DateTimePickerPreset.PAST) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    EndMomentPickerFragment.forPastEpisode(startMoment, endMoment),
                        DatePickerFragment.TAG,
                            M.dp(344.f).intValue(), true);
        }
    }

    String getRandomFutureTimeErrorString() {
        return getString(futureTimeErrors[(int) Math.floor(Math.random() * futureTimeErrors.length)]);
    }

    @Override
    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
        if (REQUEST_MODE_CHANGE.equals(requestKey)) {
            timeInputMode = DateTimePickerFragment
                    .TimeInputMode.fromString(result.getString("mode"));
            switch (timeInputMode) {
                case CLOCK:
                    setViewPagerCurrentItem(0);
                    break;
                case PART_OF_DAY:
                    setViewPagerCurrentItem(1);
                    break;
            }
        }
    }

    private void setViewPagerCurrentItem(int position) {
        final int dy = M.dp(position == 0 ? 230.f : -230.f).intValue();
        final ValueAnimator animator = ValueAnimator.ofInt(0, dy);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(350L);
        final int[] previousValue = {0};
        animator.addUpdateListener(a -> {
            int currentDrag = (int) a.getAnimatedValue() - previousValue[0];
            timeInputPager.fakeDragBy(currentDrag);
            previousValue[0] = (int) a.getAnimatedValue();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                timeInputPager.beginFakeDrag();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                timeInputPager.endFakeDrag();
                timeInputPager.setCurrentItem(position, false);
            }
        });
        animator.start();
    }

    RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    @Override
    public void onBackPressed() {
        if (preset == DateTimePickerPreset.JUST_ENDED) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    DatePickerFragment.forJustEnded(startMoment),
                        DatePickerFragment.TAG,
                            M.dp(448.f).intValue(), true);
        } else if (preset == DateTimePickerPreset.PAST) {
            requireRecordHeadacheActivity().startBottomTransitionToFragment(
                    DatePickerFragment.forPastEpisode(startMoment, endMoment),
                        DatePickerFragment.TAG,
                            M.dp(448.f).intValue(), true);
        }
    }

    public static TimePickerFragment forPastEpisode(Moment startMoment, Moment endMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putParcelable("endMoment", endMoment);
        args.putString("mode", DateTimePickerPreset.PAST.name());
        fragment.setArguments(args);
        return fragment;
    }

    public static TimePickerFragment forJustEnded(Moment startMoment) {
        final TimePickerFragment fragment = new TimePickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable("startMoment", startMoment);
        args.putString("mode", DateTimePickerPreset.JUST_ENDED.name());
        fragment.setArguments(args);
        return fragment;
    }
}
