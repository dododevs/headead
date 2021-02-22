package revolver.headead.core.display.criteria;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import revolver.headead.core.display.Header;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.PainType;

public class ByPainType extends OrderingCriterion {

    @Override
    public void apply(Context context, List<ListItem> dataset, List<Headache> headaches) {
        /*dataset.clear();
        Collections.sort(headaches, (headache1, headache2) ->
                headache2.getPainTypes().ordinal() - headache1.getPainTypes().ordinal());
        dataset.addAll(headaches);

        PainType lastPainType = null;
        for (int i = headaches.size() - 1; i >= 0; i--) {
            final Headache headache = headaches.get(i);
            if (lastPainType == null) {
                lastPainType = headache.getPainTypes();
            } else if (headache.getPainTypes() != lastPainType) {
                dataset.add(i + 1, new Header(context.getString(
                        headaches.get(i + 1).getPainTypes().getLongStringLabel())));
                lastPainType = headache.getPainTypes();
            }
        }
        dataset.add(0, new Header(context.getString(
                headaches.get(0).getPainTypes().getLongStringLabel())));*/
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ByPainType;
    }
}
