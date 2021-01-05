package revolver.headead.core.display.criteria;

import android.content.Context;

import java.util.List;

import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;

public abstract class OrderingCriterion {
    public abstract void apply(final Context context, final List<ListItem> dataset, final List<Headache> headaches);
}
