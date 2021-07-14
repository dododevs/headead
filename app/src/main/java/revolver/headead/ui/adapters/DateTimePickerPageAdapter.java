package revolver.headead.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import revolver.headead.ui.fragments.record2.pickers.DateTimePickerPageFragment;

public class DateTimePickerPageAdapter extends FragmentStateAdapter {

    public DateTimePickerPageAdapter(final Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0
                ? DateTimePickerPageFragment.asStart()
                : DateTimePickerPageFragment.asEnd();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
