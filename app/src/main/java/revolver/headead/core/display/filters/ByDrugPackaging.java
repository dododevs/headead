package revolver.headead.core.display.filters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.display.ListItem;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.Headache;

public class ByDrugPackaging extends FilteringCriterion {

    public ByDrugPackaging(DrugPackaging drugPackaging) {
        super(drugPackaging);
    }

    @Override
    public List<Headache> apply(Context context, List<Headache> headaches) {
        final List<Headache> filteredHeadaches = new ArrayList<>();
        for (int i = headaches.size() - 1; i >= 0; i--) {
            final List<DrugIntake> drugIntakes = headaches.get(i).getDrugIntakes();
            if (drugIntakes == null || drugIntakes.isEmpty()) {
                continue;
            }
            for (final DrugIntake drugIntake : drugIntakes) {
                if (drugIntake.getDrugPackaging().equals(query)) {
                    filteredHeadaches.add(headaches.get(i));
                    break;
                }
            }
        }
        return filteredHeadaches;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ByDrugPackaging && ((ByDrugPackaging) obj).query == query;
    }
}
