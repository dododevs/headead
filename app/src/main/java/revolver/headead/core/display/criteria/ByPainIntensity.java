package revolver.headead.core.display.criteria;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import revolver.headead.core.display.Header;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.PainIntensity;

public class ByPainIntensity extends OrderingCriterion {

    @Override
    public void apply(Context context, List<ListItem> dataset, List<Headache> headaches) {
        dataset.clear();
        Collections.sort(headaches, (headache1, headache2) ->
                headache2.getPainIntensity() - headache1.getPainIntensity());
        dataset.addAll(headaches);

        int lastPainIntensity = 0;
        for (int i = headaches.size() - 1; i >= 0; i--) {
            final Headache headache = headaches.get(i);
            if (lastPainIntensity == 0) {
                lastPainIntensity = headache.getPainIntensity();
            } else if (headache.getPainIntensity() != lastPainIntensity) {
                dataset.add(i + 1, new Header(String.valueOf(
                        headaches.get(i + 1).getPainIntensity())));
                lastPainIntensity = headache.getPainIntensity();
            }
        }
        dataset.add(0, new Header(String.valueOf(
                headaches.get(0).getPainIntensity())));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ByPainIntensity;
    }
}
