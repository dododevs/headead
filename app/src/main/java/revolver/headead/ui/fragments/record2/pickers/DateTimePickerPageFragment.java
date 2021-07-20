package revolver.headead.ui.fragments.record2.pickers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import revolver.headead.R;

public class DateTimePickerPageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_datetime_picker_page, container, false);
    }

    public static DateTimePickerPageFragment asStart() {
        final DateTimePickerPageFragment fragment = new DateTimePickerPageFragment();
        final Bundle args = new Bundle();
        args.putString("target", "start");
        fragment.setArguments(args);
        return fragment;
    }

    public static DateTimePickerPageFragment asEnd() {
        final DateTimePickerPageFragment fragment = new DateTimePickerPageFragment();
        final Bundle args = new Bundle();
        args.putString("target", "end");
        fragment.setArguments(args);
        return fragment;
    }
}
