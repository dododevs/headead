package revolver.headead.core.model.migrations;

import androidx.annotation.NonNull;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.DrugTag;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.Moment;
import revolver.headead.core.model.PainIntensity;
import revolver.headead.core.model.PainLocation;
import revolver.headead.core.model.PainType;
import revolver.headead.core.model.SavedDrug;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;

import static java.util.Objects.requireNonNull;

public class HeadacheMigration implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm dynamicRealm, long oldVersion, long newVersion) {
        final RealmSchema schema = dynamicRealm.getSchema();

        /*
         * Apply the following updates:
         * - Headache
         *  - field painIntensity: type changes from PainIntensity to int
         *  - field painLocation: removed
         *  - field painLocations: added as a List and populated with the (previously) only PainLocation
         *  - field getsWorseWithMovement: added
         *  - field painType: removed
         *  - field painTypes: added as a List and populated with the (previously) only PainType
         * - SavedDrug: added
         * */
        if (oldVersion == 0) {
            final RealmObjectSchema headacheSchema =
                    schema.get(Headache.class.getSimpleName());
            if (headacheSchema == null) {
                return;
            }

            headacheSchema.addField("newPainIntensity", int.class);
            headacheSchema.transform(headache -> {
                /*
                * Former PainIntensity to int mapping
                * - PainIntensity.LOW       -> 2
                * - PainIntensity.MEDIUM    -> 5
                * - PainIntensity.HIGH      -> 8
                */
                headache.setInt("newPainIntensity",
                        (PainIntensity.fromString(headache
                                .get("painIntensity")).ordinal() + 1) * 3 - 1);
            });
            headacheSchema.addRealmListField("painLocations", String.class);
            headacheSchema.transform(headache -> {
                /*
                 * Former PainLocation (String) is added to the new RealmList<String>
                 */
                final RealmList<String> painLocations = new RealmList<>();
                painLocations.add(headache.getString("painLocation"));
                // painLocations.add(((PainLocation) headache.get("painLocation")).name());
                headache.set("painLocations", painLocations);
            });
            headacheSchema.addField("getsWorseWithMovement", boolean.class);
            headacheSchema.transform(headache -> {
                /*
                * New field getsWorseWithMovement is initialised as false
                */
                headache.setBoolean("getsWorseWithMovement", false);
            });
            headacheSchema.addRealmListField("painTypes", String.class);
            headacheSchema.transform(headache -> {
                /*
                 * Former PainType (String) is added to the new RealmList<String>
                 */
                final RealmList<String> painTypes = new RealmList<>();
                painTypes.add(headache.getString("painType"));
                // painTypes.add(((PainType) headache.get("painType")).name());
                headache.set("painTypes", painTypes);
            });

            headacheSchema.removeField("painIntensity")
                    .renameField("newPainIntensity", "painIntensity")
                    .removeField("painLocation")
                    .removeField("painType");

            final RealmObjectSchema savedDrugSchema =
                    schema.create(SavedDrug.class.getSimpleName());
            savedDrugSchema.addField("drugPackagingId", String.class, FieldAttribute.PRIMARY_KEY);
            savedDrugSchema.addRealmObjectField("drugIntake",
                    requireNonNull(schema.get(DrugIntake.class.getSimpleName())));
            oldVersion++;
        }

        /*
         * Apply the following updates:
         * - DrugTag: added
         * - DrugIntake:
         *  - field tag: added
         * */
        if (oldVersion == 1) {
            final RealmObjectSchema drugTagSchema =
                    schema.create(DrugTag.class.getSimpleName());
            drugTagSchema.addField("tag", String.class);
            drugTagSchema.addField("color", int.class);

            final RealmObjectSchema drugIntakeSchema =
                    requireNonNull(schema.get(DrugIntake.class.getSimpleName()));
            drugIntakeSchema.addRealmObjectField("tag", drugTagSchema);
            oldVersion++;
        }

        /*
         * Apply the following updates:
         * - Moment: added
         * - Headache:
         *  - field startDate, endDate: removed
         *  - field startDateTimeMode, endDateTimeMode: removed
         *  - field startMoment, endMoment: added
         * */
        if (oldVersion == 2) {
            final RealmObjectSchema momentSchema =
                    schema.create(Moment.class.getSimpleName());
            momentSchema.addField("date", Date.class);
            momentSchema.addField("partOfDay", int.class);
            momentSchema.addField("timeInputMode", String.class);

            final RealmObjectSchema headacheSchema =
                    requireNonNull(schema.get(Headache.class.getSimpleName()));
            headacheSchema.addRealmObjectField("startMoment", momentSchema);
            headacheSchema.addRealmObjectField("endMoment", momentSchema);

            headacheSchema.transform(headache -> {
                final Date startDate = headache.getDate("startDate");
                final Date endDate = headache.getDate("endDate");
                final Moment startMoment = new Moment(
                        startDate, -1, DateTimePickerFragment.TimeInputMode.CLOCK);
                final Moment endMoment = new Moment(
                        endDate, -1, DateTimePickerFragment.TimeInputMode.CLOCK);
                headache.set("startMoment", startMoment);
                headache.set("endMoment", endMoment);
            });

            headacheSchema.removeField("startDate");
            headacheSchema.removeField("endDate");
            headacheSchema.removeField("startDateTimeMode");
            headacheSchema.removeField("endDateTimeMode");
        }
    }

}
