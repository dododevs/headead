package revolver.headead.ui.fragments.display;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.core.model.Headache;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.adapters.RecordedHeadachesAdapter;

public class HeadacheDetailBackdropFragment extends Fragment {

    public static SimpleDateFormat endTimeFormatter =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headache_detail_backdrop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final Headache headache = requireArguments().getParcelable("headache");
        if (headache == null) {
            Toast.makeText(requireContext(),
                    R.string.fragment_headache_detail_backdrop_no_headache,
                        Toast.LENGTH_SHORT).show();
            return;
        }

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> requireActivity().onBackPressed());
        toolbar.inflateMenu(R.menu.toolbar_fragment_headache_detail_backdrop);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit) {
                requireRecordHeadacheActivity().startEditMode(headache);
                return true;
            }
            return false;
        });

        ((TextView) view.findViewById(R.id.fragment_headache_detail_backdrop_start))
                .setText(RecordedHeadachesAdapter.startAndEndFormatter
                        .format(headache.getStartMoment().getDate()));
        if (headache.getEndMoment() != null) {
            view.findViewById(R.id.fragment_headache_detail_backdrop_timespan_arrow)
                    .setVisibility(View.VISIBLE);
            view.findViewById(R.id.fragment_headache_detail_backdrop_end)
                    .setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.fragment_headache_detail_backdrop_end))
                    .setText(endTimeFormatter.format(headache.getEndMoment().getDate()));
            ((TextView) view.findViewById(R.id.fragment_headache_detail_backdrop_duration))
                    .setText(RecordedHeadachesAdapter.buildDurationLabel(requireContext(),
                            headache.getStartMoment().getDate(),
                                headache.getEndMoment().getDate()));
        } else {
            view.findViewById(R.id.fragment_headache_detail_backdrop_timespan_arrow)
                    .setVisibility(View.GONE);
            view.findViewById(R.id.fragment_headache_detail_backdrop_end)
                    .setVisibility(View.GONE);
            ((TextView) view.findViewById(
                    R.id.fragment_headache_detail_backdrop_duration))
                        .setText(R.string.fragment_headache_detail_backdrop_duration_undefined);
        }

    }

    private RecordHeadacheActivity2 requireRecordHeadacheActivity() {
        return (RecordHeadacheActivity2) requireActivity();
    }

    public static HeadacheDetailBackdropFragment of(final Headache headache) {
        final HeadacheDetailBackdropFragment fragment = new HeadacheDetailBackdropFragment();
        final Bundle args = new Bundle();
        args.putParcelable("headache", headache);
        fragment.setArguments(args);
        return fragment;
    }
}
