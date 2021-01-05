package revolver.headead.core.display.filters;

import android.content.Context;

import java.util.List;

import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.Headache;

public abstract class FilteringCriterion {

    protected final Object query;

    protected FilteringCriterion(Object query) {
        this.query = query;
    }

    public abstract List<Headache> apply(final Context context, final List<Headache> headaches);
}