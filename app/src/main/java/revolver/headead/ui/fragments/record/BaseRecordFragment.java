package revolver.headead.ui.fragments.record;

import androidx.fragment.app.Fragment;

import revolver.headead.ui.activities.record.RecordHeadacheActivity;

public class BaseRecordFragment extends Fragment {

    public RecordHeadacheActivity getRecordHeadacheActivity() {
        return (RecordHeadacheActivity) getActivity();
    }

}
