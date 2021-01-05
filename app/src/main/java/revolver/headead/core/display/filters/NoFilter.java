package revolver.headead.core.display.filters;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import revolver.headead.core.model.Headache;

public class NoFilter extends FilteringCriterion {

    public NoFilter() {
        super(null);
    }

    @Override
    public List<Headache> apply(Context context, List<Headache> headaches) {
        return new ArrayList<>(headaches);
    }
}
