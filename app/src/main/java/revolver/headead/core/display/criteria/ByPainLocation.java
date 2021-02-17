package revolver.headead.core.display.criteria;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import revolver.headead.core.display.Header;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.PainLocation;

public class ByPainLocation extends OrderingCriterion {

    @Override
    public void apply(Context context, List<ListItem> dataset, List<Headache> headaches) {
        /*dataset.clear();
        Collections.sort(headaches, (headache1, headache2) ->
                headache2.getPainLocation().ordinal() - headache1.getPainLocation().ordinal());
        dataset.addAll(headaches);

        PainLocation lastPainLocation = null;
        for (int i = headaches.size() - 1; i >= 0; i--) {
            final Headache headache = headaches.get(i);
            if (lastPainLocation == null) {
                lastPainLocation = headache.getPainLocation();
            } else if (headache.getPainLocation() != lastPainLocation) {
                dataset.add(i + 1, new Header(context.getString(
                        headaches.get(i + 1).getPainLocation().getLongStringLabel())));
                lastPainLocation = headache.getPainLocation();
            }
        }
        dataset.add(0, new Header(context.getString(
                headaches.get(0).getPainLocation().getLongStringLabel())));*/
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ByPainLocation;
    }
}
