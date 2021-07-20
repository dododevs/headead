package revolver.headead.core.display.criteria;

import android.content.Context;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import revolver.headead.R;
import revolver.headead.core.display.Header;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;

public class ByDuration extends OrderingCriterion {

    @Override
    public void apply(Context context, List<ListItem> dataset, List<Headache> headaches) {
        dataset.clear();
        /*Collections.sort(headaches, (headache2, headache1) -> {
            if (headache1.getEndDate() == null) {
                return new Date(headache2.getEndDate().getTime() - headache2.getStartDate().getTime())
                        .compareTo(new Date(System.currentTimeMillis() -
                                headache1.getStartDate().getTime()));
            } else if (headache2.getEndDate() == null) {
                return new Date(System.currentTimeMillis() - headache2.getStartDate().getTime())
                        .compareTo(new Date(headache1.getEndDate().getTime() -
                                headache1.getStartDate().getTime()));
            }
            return new Date(headache2.getEndDate().getTime() - headache2.getStartDate().getTime())
                    .compareTo(new Date(headache1.getEndDate().getTime() -
                            headache1.getStartDate().getTime()));
        });*/
        dataset.addAll(headaches);
        dataset.add(0, new Header(context.getString(
                R.string.ordering_criterion_by_duration_header)));
    }
}
