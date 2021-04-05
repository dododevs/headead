package revolver.headead.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.aifa.Aifa;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugDosageUnit;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.SavedDrug;
import revolver.headead.ui.adapters.DrugDosageUnitsAdapter2;
import revolver.headead.ui.fragments.SimpleAlertDialogFragment;
import revolver.headead.ui.fragments.record2.pickers.DrugIntakeDateTimePickerFragment;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.ViewUtils;

public class DrugIntakeActivity extends AppCompatActivity {

    public static final int FRACTION_12 = -12;
    public static final int FRACTION_13 = -13;
    public static final int FRACTION_14 = -14;
    public static final int ONE         =  -1;
    public static final int TWO         =  -2;

    private static SimpleDateFormat dateFormatter =
            new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());

    private String packagingId;
    private DrugPackaging drugPackaging;
    private Integer intakeQuantity = Integer.MIN_VALUE;

    private ChipGroup defaultValuesView;
    private ChipGroup.OnCheckedChangeListener checkedChangeListener;

    private DrugDosageUnit intakeUnit;
    private Date intakeDate;
    private String intakeComment;

    private boolean isFavorite = false;
    private ImageView favoriteView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_intake);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());

        defaultValuesView = findViewById(R.id.activity_drug_intake_quantity_chips);
        defaultValuesView.setOnCheckedChangeListener(checkedChangeListener = (group, checkedId) -> {
            if (checkedId == R.id.fraction12) {
                intakeQuantity = FRACTION_12;
            } else if (checkedId == R.id.fraction13) {
                intakeQuantity = FRACTION_13;
            } else if (checkedId == R.id.fraction14) {
                intakeQuantity = FRACTION_14;
            } else if (checkedId == R.id.one) {
                intakeQuantity = ONE;
            } else if (checkedId == R.id.two) {
                intakeQuantity = TWO;
            }
            onIntakeQuantityUpdated();
        });
        findViewById(R.id.other).setOnClickListener((v) -> {
            /* assume user will cancel the quantity dialog,
               revert back to previously selected default quantity, if any */
            if (intakeQuantity < 0) {
                switch (intakeQuantity) {
                    case FRACTION_12:
                        defaultValuesView.check(R.id.fraction12);
                        break;
                    case FRACTION_13:
                        defaultValuesView.check(R.id.fraction13);
                        break;
                    case FRACTION_14:
                        defaultValuesView.check(R.id.fraction14);
                        break;
                    case ONE:
                        defaultValuesView.check(R.id.one);
                        break;
                    case TWO:
                        defaultValuesView.check(R.id.two);
                        break;
                }
            }
            buildQuantityDialog().show(getSupportFragmentManager(), "drugIntakeQuantity");
        });

        findViewById(R.id.activity_drug_intake_unit).setOnClickListener((v) -> {
            final DrugDosageUnitsAdapter2 adapter;
            new SimpleAlertDialogFragment.Builder(this)
                    .title(R.string.dialog_drug_unit_title)
                    .list(adapter = new DrugDosageUnitsAdapter2(intakeQuantity, intakeUnit))
                    .positiveButton(R.string.dialog_drug_unit_positive, (dialog) -> {
                        intakeUnit = adapter.getSelectedItem();
                        onIntakeUnitUpdated();
                    }, true).build().show(getSupportFragmentManager(), null);
        });
        findViewById(R.id.activity_drug_intake_date).setOnClickListener((v) ->
                new DrugIntakeDateTimePickerFragment()
                        .show(getSupportFragmentManager(), "drugIntakeDate"));
        findViewById(R.id.activity_drug_intake_comment).setOnClickListener((v) ->
                buildCommentEntryDialog().show(getSupportFragmentManager(), "drugIntakeComment"));

        findViewById(R.id.fab).setOnClickListener((v) -> onSubmitButtonPressed());

        favoriteView = findViewById(R.id.activity_drug_intake_favorite);
        favoriteView.setOnClickListener((v) -> onFavoriteButtonPressed());

        Aifa.getDrugLookupService().findPackagingFormatById(
                Aifa.buildDrugPackagingByIdQueryString(
                        packagingId = getIntent().getStringExtra("id")))
                .enqueue(new DrugPackagingLookupByIdCallback());

        if (!App.getDefaultRealm().where(SavedDrug.class)
                .and().equalTo("drugPackagingId", packagingId)
                .findAll().isEmpty()) {
            isFavorite = true;
            favoriteView.setImageResource(R.drawable.ic_favorite_on);
        } else {
            isFavorite = false;
            favoriteView.setImageResource(R.drawable.ic_favorite_off);
        }
    }

    public void setIntakeDate(final Date date) {
        this.intakeDate = date;
        onIntakeDateUpdated();
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    private void onIntakeUnitUpdated() {
        if (intakeUnit != null) {
            ((TextView) findViewById(R.id.activity_drug_intake_unit_label))
                    .setText(getResources().getQuantityText(
                            intakeUnit.getNameResource(), intakeQuantity));
        }
    }

    private void onIntakeDateUpdated() {
        if (intakeDate != null) {
            ((TextView) findViewById(R.id.activity_drug_intake_date_label))
                    .setText(dateFormatter.format(intakeDate));
        } else {
            ((TextView) findViewById(R.id.activity_drug_intake_date_label))
                    .setText(R.string.activity_drug_intake_date_default);
        }
    }

    private void onIntakeCommentUpdated() {
        if (intakeComment != null) {
            ((TextView) findViewById(R.id.activity_drug_intake_comment_label))
                    .setText(intakeComment);
        } else {
            ((TextView) findViewById(R.id.activity_drug_intake_comment_label))
                    .setText(R.string.activity_drug_intake_comment_default);
        }
    }

    private void onIntakeQuantityUpdated() {
        final String label;
        if (intakeQuantity < 0) {
            switch (intakeQuantity) {
                case FRACTION_12:
                    label = getString(R.string.fraction12);
                    break;
                case FRACTION_13:
                    label = getString(R.string.fraction13);
                    break;
                case FRACTION_14:
                    label = getString(R.string.fraction14);
                    break;
                case ONE:
                    label = getString(R.string.one);
                    break;
                case TWO:
                    label = getString(R.string.two);
                    break;
                default:
                    label = getString(R.string.activity_drug_intake_quantity_default);
                    break;
            }
        } else if (intakeQuantity == 0) {
            label = getString(R.string.activity_drug_intake_quantity_default);
        } else {
            defaultValuesView.setOnCheckedChangeListener(null);
            defaultValuesView.check(R.id.other);
            defaultValuesView.setOnCheckedChangeListener(checkedChangeListener);
            label = String.valueOf(intakeQuantity);
        }
        ((TextView) findViewById(R.id.activity_drug_intake_quantity_label)).setText(label);
    }

    private void onFavoriteButtonPressed() {
        if (isFavorite) {
            App.getDefaultRealm().executeTransaction(realm ->
                    realm.where(SavedDrug.class)
                        .and().equalTo("drugPackagingId", packagingId)
                            .findAll().deleteAllFromRealm());
            favoriteView.setImageResource(R.drawable.ic_favorite_off);
        } else {
            App.getDefaultRealm().executeTransaction(realm -> {
                final SavedDrug savedDrug = new SavedDrug();
                savedDrug.setDrugPackagingId(packagingId);
                savedDrug.setDrugIntake(new DrugIntake(drugPackaging,
                        intakeQuantity, intakeUnit != null ?
                            intakeUnit.name() : null, intakeDate, intakeComment));
                realm.insert(savedDrug);
            });
            favoriteView.setImageResource(R.drawable.ic_favorite_on);
        }
        isFavorite = !isFavorite;
    }

    private void onSubmitButtonPressed() {
        boolean missingData = false;
        if (intakeUnit == null) {
            bounceMissingDataView(findViewById(R.id.activity_drug_intake_unit));
            missingData = true;
        } else if (intakeQuantity == null || intakeQuantity == Integer.MIN_VALUE || intakeQuantity == 0) {
            bounceMissingDataView(findViewById(R.id.activity_drug_intake_quantity));
            missingData = true;
        } else if (intakeDate == null) {
            buildDefaultDateDialog().show(getSupportFragmentManager(), null);
            return;
        }

        if (missingData) {
            Snacks.shorter(findViewById(R.id.activity_drug_intake_date),
                    R.string.activity_drug_intake_error_missing_data, false);
        } else {
            returnDataToActivity();
        }
    }

    private void returnDataToActivity() {
        final DrugIntake drugIntake = new DrugIntake(
                drugPackaging, intakeQuantity,
                    intakeUnit.name(), intakeDate, intakeComment);
        App.getDefaultRealm().executeTransaction(realm -> {
            final SavedDrug savedDrug = new SavedDrug();
            savedDrug.setDrugPackagingId(packagingId);
            savedDrug.setDrugIntake(drugIntake);
            realm.insertOrUpdate(savedDrug);
        });
        setResult(RESULT_OK, new Intent().putExtra("drugIntake", drugIntake));
        finish();
    }

    private SimpleAlertDialogFragment buildCommentEntryDialog() {
        return new SimpleAlertDialogFragment.Builder(this)
                .title(R.string.dialog_drug_intake_comment_title)
                .editText(R.string.dialog_drug_intake_comment_hint, EditorInfo.TYPE_CLASS_TEXT,
                        editText -> editText.getText().length() > 0 &&
                                editText.getText().length() < 500)
                .positiveButton(R.string.dialog_drug_intake_comment_positive, fragment -> {
                    intakeComment = fragment.getEntryText().toString();
                    onIntakeCommentUpdated();
                    fragment.dismiss();
                }, false)
                .negativeButton(R.string.dialog_drug_intake_comment_negative, null, true)
                .neutralButton(R.string.dialog_drug_intake_comment_neutral, fragment -> {
                    intakeComment = null;
                    onIntakeCommentUpdated();
                }, true).build();
    }

    private SimpleAlertDialogFragment buildQuantityDialog() {
        return new SimpleAlertDialogFragment.Builder(this)
                .title(R.string.dialog_drug_intake_quantity_title)
                .editText(R.string.dialog_drug_intake_quantity_hint, EditorInfo.TYPE_CLASS_NUMBER, editText -> {
                    try {
                        final int i = Integer.parseInt(editText.getText().toString());
                        return i > 0;
                    } catch (Exception e) {
                        return false;
                    }
                }).positiveButton(R.string.dialog_drug_intake_quantity_positive, (fragment) -> {
                    intakeQuantity = Integer.parseInt(fragment.getEntryText().toString());
                    onIntakeQuantityUpdated();
                }, true).build();
    }

    private SimpleAlertDialogFragment buildDefaultDateDialog() {
        return new SimpleAlertDialogFragment.Builder(this)
                .title(R.string.dialog_drug_intake_default_date_title)
                .message(R.string.dialog_drug_intake_default_date_message)
                .positiveButton(R.string.dialog_drug_intake_default_date_positive, fragment -> {
                    intakeDate = new Date();
                    returnDataToActivity();
                }, true).negativeButton(R.string.dialog_drug_intake_default_date_negative, fragment -> {
                    /* no-op */
                }, true).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.setStatusBarColor(this, ColorUtils.get(this, R.color.blackOliveDark));
    }

    private void showDrugPackagingInfo(final DrugPackaging drugPackaging) {
        final TextView drugNameView =
                findViewById(R.id.activity_drug_intake_drug_name);
        final TextView drugMakerView =
                findViewById(R.id.activity_drug_intake_drug_maker);
        final TextView drugPackagingNameView =
                findViewById(R.id.activity_drug_intake_packaging_name);
        final TextView drugPackagingActivePrincipleView =
                findViewById(R.id.activity_drug_intake_active_principle);
        drugNameView.setText(normalizeText(drugPackaging.getDrugDescription()));
        drugMakerView.setText(normalizeText(drugPackaging.getDrugMaker()));
        drugPackagingNameView.setText(normalizeText(drugPackaging.getPackagingDescription()));
        drugPackagingActivePrincipleView.setText(normalizeText(drugPackaging.getActivePrinciple()));

        intakeUnit = DrugDosageUnit.guessProperUnit(
                this, drugPackaging.getPackagingDescription());
        onIntakeUnitUpdated();
    }

    private static Spanned normalizeText(final String info) {
        if (info != null && !info.isEmpty()) {
            return Html.fromHtml(info);
        }
        return new SpannableString("-");
    }

    private void bounceMissingDataView(final View view) {
        view.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(175L)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start())
                .start();
    }

    private class DrugPackagingLookupByIdCallback implements Callback<List<DrugPackaging>> {
        @Override
        public void onResponse(@NonNull Call<List<DrugPackaging>> call, @NonNull Response<List<DrugPackaging>> response) {
            if (response.isSuccessful()) {
                final List<DrugPackaging> drugPackagingList = response.body();
                if (drugPackagingList != null && !drugPackagingList.isEmpty()) {
                    showDrugPackagingInfo(drugPackaging = drugPackagingList.get(0));
                } else {
                    Toast.makeText(DrugIntakeActivity.this,
                            R.string.fragment_drug_packaging_overview_error,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Toast.makeText(DrugIntakeActivity.this,
                        R.string.fragment_drug_packaging_overview_error,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<DrugPackaging>> call, @NonNull Throwable t) {
            Toast.makeText(DrugIntakeActivity.this,
                    R.string.fragment_drug_packaging_overview_error,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
