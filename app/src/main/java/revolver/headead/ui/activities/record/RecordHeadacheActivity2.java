package revolver.headead.ui.activities.record;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.ArrayMap;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;
import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.Headache;
import revolver.headead.core.model.PainLocation;
import revolver.headead.core.model.PainType;
import revolver.headead.core.model.Trigger;
import revolver.headead.ui.fragments.SimpleAlertDialogFragment;
import revolver.headead.ui.fragments.display.HeadacheDetailBackdropFragment;
import revolver.headead.ui.fragments.display.HeadacheDetailFrontFragment;
import revolver.headead.ui.fragments.display.ListHeadachesFragment;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;
import revolver.headead.ui.fragments.record2.pickers.PainIntensityPickerFragment;
import revolver.headead.ui.fragments.record2.pickers.PainLocationPickerFragment;
import revolver.headead.ui.fragments.record2.pickers.PainTypePickerFragment;
import revolver.headead.ui.fragments.record2.RecordHeadacheBottomPaneFragment;
import revolver.headead.util.misc.TimeFormattingUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.TextUtils;
import revolver.headead.util.ui.ViewUtils;

public class RecordHeadacheActivity2 extends AppCompatActivity {

    private static final String LOCATION_PICKER_TAG = "locationPicker";
    private static final String INTENSITY_PICKER_TAG = "intensityPicker";
    private static final String TYPE_PICKER_TAG = "typePickerTag";
    private static final String DATETIME_PICKER_TAG = "dateTimePicker";
    private static final String MAIN_LIST_TAG = "mainListFragment";
    public static final String EXTRAS_TAG = "extras";
    private static final List<String> backStackedFragmentTags =
            Arrays.asList(LOCATION_PICKER_TAG, INTENSITY_PICKER_TAG,
                    TYPE_PICKER_TAG, DATETIME_PICKER_TAG, EXTRAS_TAG);
    private static final int REQUEST_DRUGS = "letsFindYouAQuickFix".hashCode() & 0xff;
    private static final int REQUEST_LOCATION = "lemmeTrackYou".hashCode() & 0xff;

    private MaterialCardView painLocationCardView, painIntensityCardView, painTypeCardView;
    private TextView painLocationValueView, painIntensityValueView, painTypeValueView;
    private ImageView painLocationIconView, painIntensityIconView, painTypeIconView;
    private TextView headacheStartDateValueView, headacheEndDateValueView;
    private ImageView headacheStartDateIconView, headacheEndDateIconView;
    private TextView headacheEndDateLabelView;

    private Toolbar toolbar;
    private View.OnClickListener navigationClickListener;
    private FrameLayout bottomSheetFrame;
    private FrameLayout bottomSheetDimmer;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private Fragment backdropFragment;

    private List<PainLocation> painLocations;
    private int painIntensity;
    private List<PainType> painTypes;
    private boolean isAuraEnabled, getsWorseWithMovement;
    private Map<Trigger, Boolean> triggersStatus;
    private ArrayList<DrugIntake> drugDosages = new ArrayList<>();
    private CameraPosition currentCameraPosition;
    private Location currentLocation;

    private Date headacheStartDate;
    private Date headacheEndDate;
    private DateTimePickerFragment.DateTimeMode headacheStartDateTimeMode;
    private DateTimePickerFragment.DateTimeMode headacheEndDateTimeMode;

    private Headache editedHeadache;
    private boolean editMode = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_headache_2);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(navigationClickListener = (v) -> revertToMainListFragment());

        painLocationValueView = findViewById(R.id.activity_record_headache_2_pain_location_value);
        painLocationIconView = findViewById(R.id.activity_record_headache_2_pain_location_icon);
        painIntensityValueView = findViewById(R.id.activity_record_headache_2_pain_intensity_value);
        painIntensityIconView = findViewById(R.id.activity_record_headache_2_pain_intensity_icon);
        painTypeValueView = findViewById(R.id.activity_record_headache_2_pain_type_value);
        painTypeIconView = findViewById(R.id.activity_record_headache_2_pain_type_icon);

        headacheStartDateValueView = findViewById(R.id.activity_record_headache_2_datetime_start_value);
        headacheStartDateIconView = findViewById(R.id.activity_record_headache_2_datetime_start_icon);
        headacheEndDateValueView = findViewById(R.id.activity_record_headache_2_datetime_end_value);
        headacheEndDateIconView = findViewById(R.id.activity_record_headache_2_datetime_end_icon);
        headacheEndDateLabelView = findViewById(R.id.activity_record_headache_2_datetime_end_label);

        painLocationCardView = findViewById(R.id.activity_record_headache_2_pain_location);
        painLocationCardView.setOnClickListener((v) ->
                startBottomTransitionToFragment(new PainLocationPickerFragment(),
                        LOCATION_PICKER_TAG, M.dp(256.f).intValue(), true));
        painIntensityCardView = findViewById(R.id.activity_record_headache_2_pain_intensity);
        painIntensityCardView.setOnClickListener((v) ->
                startBottomTransitionToFragment(new PainIntensityPickerFragment(),
                        INTENSITY_PICKER_TAG, M.dp(256.f).intValue(), true));
        painTypeCardView = findViewById(R.id.activity_record_headache_2_pain_type);
        painTypeCardView.setOnClickListener((v) ->
                startBottomTransitionToFragment(new PainTypePickerFragment(),
                        TYPE_PICKER_TAG, M.dp(256.f).intValue(), true));

        findViewById(R.id.activity_record_headache_2_datetime_start).setOnClickListener((v) ->
                startBottomTransitionToFragment(DateTimePickerFragment.asStart(),
                        DATETIME_PICKER_TAG, M.dp(132.f).intValue(), true));
        findViewById(R.id.activity_record_headache_2_datetime_end).setOnClickListener((v) ->
                startBottomTransitionToFragment(DateTimePickerFragment.asEnd(),
                        DATETIME_PICKER_TAG, M.dp(132.f).intValue(), true));
        findViewById(R.id.activity_record_headache_2_medication).setOnClickListener((v) ->
                startActivityForResult(new Intent(this, RecordDrugsActivity.class)
                        .putParcelableArrayListExtra("drugs", drugDosages), REQUEST_DRUGS));
        findViewById(R.id.activity_record_headache_2_geo).setOnClickListener((v) ->
                startActivityForResult(new Intent(this, RecordGeoActivity.class)
                        .putExtra("location", currentLocation)
                        .putExtra("cameraPosition", currentCameraPosition), REQUEST_LOCATION));

        final LinearLayout bottomSheet = findViewById(R.id.activity_record_headache_2_bottom_pane);
        bottomSheet.setBackground(createBottomPaneRoundedBackground());

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheet.setBackground(createBottomPaneRoundedBackground());
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setPeekHeight(M.dp(96.f).intValue(), true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.bottom_frame, new RecordHeadacheBottomPaneFragment())
                .commit();

        bottomSheetFrame = findViewById(R.id.bottom_frame);
        bottomSheetDimmer = findViewById(R.id.activity_record_headache_2_dim);

        revertToMainListFragment();
    }

    public void setStartHeadacheDate(final Date date, final DateTimePickerFragment.DateTimeMode dateTimeMode) {
        headacheStartDate = date;
        headacheStartDateTimeMode = dateTimeMode;
        onHeadacheStartDateUpdated();
    }

    public void setEndHeadacheDate(final Date date, final DateTimePickerFragment.DateTimeMode dateTimeMode) {
        headacheEndDate = date;
        headacheEndDateTimeMode = dateTimeMode;
        onHeadacheEndDateUpdated(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_DRUGS) {
                drugDosages = data.getParcelableArrayListExtra("drugs");
                onDrugIntakesUpdated();
            } else if (requestCode == REQUEST_LOCATION) {
                currentLocation = data.getParcelableExtra("location");
                currentCameraPosition = data.getParcelableExtra("cameraPosition");
                onCurrentLocationUpdated();
            }
        }
    }

    public void animatePainLocationChange(final List<PainLocation> newPainLocations) {
        if (newPainLocations == null || newPainLocations.isEmpty()) {
            painLocationIconView.animate()
                    .translationX(0)
                    .translationY(0)
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            painLocationValueView.animate()
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            painLocations = null;
            painLocationCardView.findViewById(
                    R.id.activity_record_headache_2_pain_location_checked).setVisibility(View.GONE);
            return;
        }
        if (newPainLocations.size() == 1) {
            painLocationValueView.setText(newPainLocations.get(0).getShortStringLabel());
        } else {
            final String first = getString(newPainLocations.get(0).getShortStringLabel());
            final SpannableStringBuilder sb = new SpannableStringBuilder(first)
                    .append("+").append(String.valueOf(newPainLocations.size() - 1));
            final int start = first.length();
            final int end = first.length() + 2;
            sb.setSpan(new RelativeSizeSpan(0.5f), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(new TextUtils.LowerSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            painLocationValueView.setText(sb);
        }
        if (painLocations == null || painLocations.isEmpty()) {
            painLocationIconView.animate()
                    .translationX(M.dp(-12.f))
                    .translationYBy(M.dp(8.f))
                    .scaleX(0.35f)
                    .scaleY(0.35f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            painLocationValueView.animate()
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
        painLocations = newPainLocations;

        painLocationCardView.findViewById(
                R.id.activity_record_headache_2_pain_location_checked).setVisibility(View.VISIBLE);
    }

    public void animatePainIntensityChange(final int newPainIntensity) {
        if (newPainIntensity == 0) {
            painIntensityIconView.animate()
                    .translationX(0)
                    .translationY(0)
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            painIntensityValueView.animate()
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            painIntensity = 0;
            painIntensityCardView.findViewById(
                    R.id.activity_record_headache_2_pain_intensity_checked).setVisibility(View.GONE);
            return;
        }
        painIntensityValueView.setText(String.valueOf(newPainIntensity));
        if (painIntensity == 0) {
            painIntensityIconView.animate()
                    .translationX(M.dp(-12.f))
                    .translationY(M.dp(8.f))
                    .scaleX(0.35f)
                    .scaleY(0.35f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            painIntensityValueView.animate()
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
        painIntensity = newPainIntensity;
        painIntensityCardView.findViewById(
                R.id.activity_record_headache_2_pain_intensity_checked).setVisibility(View.VISIBLE);
    }

    public void animatePainTypeChange(final List<PainType> newPainTypes) {
        if (newPainTypes == null) {
            painTypeIconView.animate()
                    .translationX(0)
                    .translationY(0)
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            painTypeValueView.animate()
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            painTypes = null;
            painTypeCardView.findViewById(
                    R.id.activity_record_headache_2_pain_type_checked).setVisibility(View.GONE);
            return;
        }
        painTypeValueView.setText(PainType.joinMultiple(this, newPainTypes));
        if (painTypes == null) {
            painTypeIconView.animate()
                    .translationX(M.dp(-12.f))
                    .translationY(M.dp(8.f))
                    .scaleX(0.35f)
                    .scaleY(0.35f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            painTypeValueView.animate()
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
        painTypes = newPainTypes;
        painTypeCardView.findViewById(
                R.id.activity_record_headache_2_pain_type_checked).setVisibility(View.VISIBLE);
    }

    public void setTriggersStatus(final Map<Trigger, Boolean> triggersStatus) {
        this.triggersStatus = triggersStatus;
    }

    public Map<Trigger, Boolean> getTriggersStatus() {
        return triggersStatus;
    }

    public void setAuraEnabled(boolean auraEnabled) {
        this.isAuraEnabled = auraEnabled;
    }

    public boolean isAuraEnabled() {
        return isAuraEnabled;
    }

    public void setGetsWorseWithMovement(boolean getsWorseWithMovement) {
        this.getsWorseWithMovement = getsWorseWithMovement;
    }

    public boolean getsWorseWithMovement() {
        return getsWorseWithMovement;
    }

    public BottomSheetBehavior<LinearLayout> getBottomSheetBehavior() {
        return bottomSheetBehavior;
    }

    public List<PainLocation> getPainLocations() {
        return painLocations;
    }

    public int getPainIntensity() {
        return painIntensity;
    }

    public List<PainType> getPainTypes() {
        return this.painTypes;
    }

    public Date getHeadacheStartDate() {
        return headacheStartDate;
    }

    public Date getHeadacheEndDate() {
        return headacheEndDate;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public DateTimePickerFragment.DateTimeMode getHeadacheStartDateTimeMode() {
        return headacheStartDateTimeMode;
    }

    public DateTimePickerFragment.DateTimeMode getHeadacheEndDateTimeMode() {
        return headacheEndDateTimeMode;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            ViewUtils.setLightStatusBar(this, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 23) {
            ViewUtils.setLightStatusBar(this, false);
        }
    }

    private void enableBackgroundDimmer() {
        bottomSheetDimmer.animate()
                .withStartAction(() -> {
                    bottomSheetDimmer.setAlpha(0.f);
                    bottomSheetDimmer.setVisibility(View.VISIBLE);
                }).alpha(1.f).setDuration(200L).setInterpolator(new LinearInterpolator()).start();
    }

    private void disableBackgroundDimmer() {
        bottomSheetDimmer.animate()
                .alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> bottomSheetDimmer.setVisibility(View.GONE)).start();
    }

    private void bounceMissingDataView(final View view) {
        view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
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

    public void onNextButtonPressed() {
        boolean missingData = false;
        if (painLocations == null || painLocations.isEmpty()) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_pain_location));
            missingData = true;
        } else if (painIntensity <= 0) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_pain_intensity));
            missingData = true;
        } else if (painTypes == null) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_pain_type));
            missingData = true;
        } else if (headacheStartDate == null) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_datetime_start));
            missingData = true;
        } else if (headacheEndDate == null && headacheEndDateTimeMode != DateTimePickerFragment.DateTimeMode.ONGOING) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_datetime_end));
            missingData = true;
        }
        if (missingData) {
            Snacks.shorter(bottomSheetFrame,
                    R.string.activity_record_headache_2_error_missing_data, false);
        } else {
            final Headache headache =
                    editMode ? editedHeadache : new Headache();
            headache.setStartDateTimeMode(headacheStartDateTimeMode);
            headache.setStartDate(headacheStartDate);
            headache.setEndDateTimeMode(headacheEndDateTimeMode);
            headache.setEndDate(headacheEndDate);

            final RealmList<PainLocation> painLocations = new RealmList<>();
            painLocations.addAll(this.painLocations);
            headache.setPainLocation(painLocations);

            headache.setPainIntensity(painIntensity);
            headache.setPainType(painTypes);
            headache.setIsAuraPresent(isAuraEnabled);
            headache.setGetsWorseWithMovement(getsWorseWithMovement);

            final RealmList<DrugIntake> intakes = new RealmList<>();
            intakes.addAll(drugDosages);
            headache.setDrugIntakes(intakes);

            if (triggersStatus != null) {
                final RealmList<Trigger> triggers = new RealmList<>();
                for (Map.Entry<Trigger, Boolean> trigger : triggersStatus.entrySet()) {
                    if (trigger.getValue()) {
                        triggers.add(trigger.getKey());
                    }
                }
                headache.setSelectedTriggers(triggers);
            } else {
                headache.setSelectedTriggers(null);
            }

            if (currentLocation != null) {
                headache.setLatitude(currentLocation.getLatitude());
                headache.setLongitude(currentLocation.getLongitude());
            } else {
                headache.setLatitude(Double.MAX_VALUE);
                headache.setLongitude(Double.MAX_VALUE);
            }

            App.getDefaultRealm().executeTransaction(realm -> {
                if (editMode) {
                    realm.insertOrUpdate(headache);
                } else {
                    realm.insert(headache);
                }
            });

            if (editMode) {
                startBottomTransitionToFragment(HeadacheDetailFrontFragment
                        .of(editedHeadache), "detailFront", M.screenHeight() - ViewUtils
                            .getStatusBarHeight() - M.dp(128.f + 16.f)
                                .intValue(), false, false);
                replaceBackdropFragment(HeadacheDetailBackdropFragment.of(editedHeadache));
            } else {
                revertToMainListFragment();
            }

            editedHeadache = null;
            editMode = false;
        }
    }

    private void onHeadacheStartDateUpdated() {
        if (headacheStartDate == null) {
            headacheStartDateIconView.animate()
                    .translationX(0.f)
                    .translationY(0.f)
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            headacheStartDateValueView.animate()
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            findViewById(R.id.activity_record_headache_2_datetime_start_checked)
                    .setVisibility(View.GONE);
            return;
        }
        headacheStartDateIconView.animate()
                .translationX(M.dp(-24.f))
                .translationY(M.dp(12.f))
                .scaleX(0.5f)
                .scaleY(0.5f)
                .alpha(0.f)
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        headacheStartDateValueView.animate()
                .alpha(1.f)
                .setDuration(200L)
                .setInterpolator(new LinearInterpolator())
                .start();
        headacheStartDateValueView.setText(
                TimeFormattingUtils.formatPastDateShort(this, headacheStartDate));
        findViewById(R.id.activity_record_headache_2_datetime_start_checked)
                .setVisibility(View.VISIBLE);
        if (headacheStartDate != null && headacheEndDate != null &&
                headacheStartDate.after(headacheEndDate)) {
            setEndHeadacheDate(headacheStartDate, DateTimePickerFragment.DateTimeMode.CUSTOM);
            buildStartAfterEndErrorDialog().show(getSupportFragmentManager(), null);
        }
    }

    private void onHeadacheEndDateUpdated(boolean reset) {
        if (headacheEndDate == null) {
            headacheEndDateIconView.animate()
                    .translationX(0.f)
                    .translationY(0.f)
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withStartAction(() -> {
                        if (reset) {
                            headacheEndDateIconView.setImageResource(
                                    R.drawable.ic_timespan_end);
                        } else {
                            headacheEndDateIconView.setImageResource(
                                    R.drawable.ic_timespan_ongoing);
                        }
                    })
                    .start();
            headacheEndDateValueView.animate()
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            if (reset) {
                headacheEndDateLabelView.setText(
                        R.string.activity_record_headache_2_datetime_end);
                findViewById(R.id.activity_record_headache_2_datetime_end_checked)
                        .setVisibility(View.GONE);
                return;
            } else {
                headacheEndDateLabelView.setText(
                        R.string.activity_record_headache_2_datetime_ongoing);
            }
        } else {
            headacheEndDateIconView.animate()
                    .translationX(M.dp(-24.f))
                    .translationY(M.dp(12.f))
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            headacheEndDateValueView.animate()
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            headacheEndDateLabelView.setText(R.string.activity_record_headache_2_datetime_end);
            headacheEndDateValueView.setText(
                    TimeFormattingUtils.formatPastDateShort(this, headacheEndDate));
        }
        findViewById(R.id.activity_record_headache_2_datetime_end_checked)
                .setVisibility(View.VISIBLE);
        if (headacheStartDate != null && headacheEndDate != null &&
                headacheStartDate.after(headacheEndDate)) {
            setStartHeadacheDate(headacheEndDate, DateTimePickerFragment.DateTimeMode.CUSTOM);
            buildStartAfterEndErrorDialog().show(getSupportFragmentManager(), null);
        }
    }

    private SimpleAlertDialogFragment buildStartAfterEndErrorDialog() {
        return new SimpleAlertDialogFragment.Builder(this)
                .title(R.string.dialog_start_after_end_title)
                .message(R.string.dialog_start_after_end_message)
                .positiveButton(R.string.dialog_start_after_end_positive, null, true)
                .build();
    }

    private void onCurrentLocationUpdated() {
        if (currentLocation != null || currentCameraPosition != null) {
            findViewById(R.id.activity_record_headache_2_geo_checked)
                    .setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.activity_record_headache_2_geo_checked)
                    .setVisibility(View.GONE);
        }
    }

    private void onDrugIntakesUpdated() {
        if (drugDosages != null) {
            findViewById(R.id.activity_record_headache_2_medication_checked)
                    .setVisibility(drugDosages.isEmpty() ? View.GONE : View.VISIBLE);
        } else {
            findViewById(R.id.activity_record_headache_2_medication_checked)
                    .setVisibility(View.GONE);
        }
    }

    public void replaceBackdropFragment(final Fragment fragment) {
        findViewById(R.id.frame).animate()
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(1.f)
                .withStartAction(() -> {
                    findViewById(R.id.frame).setAlpha(0.f);
                    findViewById(R.id.frame).setVisibility(View.VISIBLE);
                }).start();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame, backdropFragment = fragment)
                .commit();
    }

    public void deleteBackdropFragment() {
        if (backdropFragment == null) {
            return;
        }
        findViewById(R.id.frame).animate()
                .setDuration(200L)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .alpha(0.f)
                .withEndAction(() -> findViewById(R.id.frame).setVisibility(View.GONE))
                .start();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .remove(backdropFragment)
                .commit();
    }

    public void startBottomTransitionToFragment(final Fragment fragment,
                                                final String tag,
                                                final int newPeekHeight,
                                                final boolean dimBackground,
                                                final boolean draggable) {
        bottomSheetFrame.animate()
                .setDuration(200L)
                .setInterpolator(new LinearInterpolator())
                .alpha(0.f)
                .start();
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.bottom_frame, fragment, tag)
                .runOnCommit(() -> bottomSheetFrame.animate()
                        .setDuration(300L)
                        .setInterpolator(new LinearInterpolator())
                        .alpha(1.f)
                        .withEndAction(() -> {
                            bottomSheetBehavior.setExpandedOffset(0);
                            bottomSheetBehavior.setDraggable(draggable);
                            bottomSheetBehavior.setPeekHeight(newPeekHeight, true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            if (dimBackground) {
                                enableBackgroundDimmer();
                            } else {
                                disableBackgroundDimmer();
                            }
                        }).start()).commit();
    }

    public void startBottomTransitionToFragment(final Fragment fragment, final String tag, final int newPeekHeight, final boolean dimBackground) {
        startBottomTransitionToFragment(fragment, tag, newPeekHeight, dimBackground, false);
    }

    public void startBottomTransitionToExpandedFragment(final Fragment fragment, final String tag, final int expandedOffset, final boolean dimBackground, final Pair<View, String> sharedElement) {
        bottomSheetFrame.animate()
                .setDuration(200L)
                .setInterpolator(new LinearInterpolator())
                .alpha(0.f)
                .start();
        final FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (sharedElement != null) {
            transaction.addSharedElement(sharedElement.first, sharedElement.second);
        }
        transaction.replace(R.id.bottom_frame, fragment, tag)
                .runOnCommit(() -> bottomSheetFrame.animate()
                        .setDuration(300L)
                        .setInterpolator(new LinearInterpolator())
                        .alpha(1.f)
                        .withEndAction(() -> {
                            bottomSheetBehavior.setDraggable(false);
                            bottomSheetBehavior.setExpandedOffset(expandedOffset);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            if (dimBackground) {
                                enableBackgroundDimmer();
                            } else {
                                disableBackgroundDimmer();
                            }
                        }).start());
        transaction.commit();
    }

    public void startBottomTransitionToExpandedFragment(final Fragment fragment, final String tag, final int expandedOffset, final boolean dimBackground) {
        startBottomTransitionToExpandedFragment(fragment, tag, expandedOffset, dimBackground, null);
    }

    public void startEditMode(final Headache headache) {
        editMode = true;
        editedHeadache = headache;
        headacheStartDateTimeMode = editedHeadache.getStartDateTimeMode();
        headacheEndDateTimeMode = editedHeadache.getEndDateTimeMode();
        headacheStartDate = editedHeadache.getStartDate();
        headacheEndDate = editedHeadache.getEndDate();
        onHeadacheStartDateUpdated();
        onHeadacheEndDateUpdated(false);
        animatePainLocationChange(editedHeadache.getPainLocations());
        animatePainIntensityChange(editedHeadache.getPainIntensity());
        animatePainTypeChange(editedHeadache.getPainTypes());
        triggersStatus = new ArrayMap<>();
        for (final Trigger trigger : App.getAllTriggers()) {
            triggersStatus.put(trigger, editedHeadache.getSelectedTriggers() != null
                    && editedHeadache.getSelectedTriggers().contains(trigger));
        }
        isAuraEnabled = editedHeadache.isAuraPresent();
        getsWorseWithMovement = editedHeadache.getsWorseWithMovement();
        drugDosages = new ArrayList<>(editedHeadache.getDrugIntakes());
        onDrugIntakesUpdated();
        currentLocation = editedHeadache.getLocation();
        onCurrentLocationUpdated();

        getToolbar().setTitle(R.string.activity_record_headache_2_edit_title);
        getToolbar().setNavigationOnClickListener((v) -> onBackPressed());
        deleteBackdropFragment();
        resetBottomPane();
    }

    public void revertToMainListFragment() {
        startBottomTransitionToExpandedFragment(new ListHeadachesFragment(),
                MAIN_LIST_TAG, M.dp(56.f).intValue(), false);
    }

    public void resetBottomPane() {
        startBottomTransitionToFragment(new RecordHeadacheBottomPaneFragment(),
                null, M.dp(96.f).intValue(), false);
    }

    public void resetToolbar() {
        toolbar.setTitle(R.string.activity_record_headache_2_title);
        toolbar.getMenu().clear();
        toolbar.setOnMenuItemClickListener(null);
        toolbar.setNavigationOnClickListener(navigationClickListener);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_dark);
    }

    public void clearAllFields() {
        headacheStartDateTimeMode = null;
        headacheEndDateTimeMode = null;
        headacheStartDate = null;
        headacheEndDate = null;
        onHeadacheStartDateUpdated();
        onHeadacheEndDateUpdated(true);

        animatePainLocationChange(null);
        animatePainIntensityChange(0);
        animatePainTypeChange(null);

        triggersStatus = null;
        isAuraEnabled = false;
        getsWorseWithMovement = false;

        drugDosages = new ArrayList<>();
        onDrugIntakesUpdated();

        currentLocation = null;
        currentCameraPosition = null;
        onCurrentLocationUpdated();
    }

    @Override
    public void onBackPressed() {
        final List<Fragment> fragments = getSupportFragmentManager().getFragments();
        final Fragment lastAdded = fragments.get(fragments.size() - 1);
        if (backStackedFragmentTags.contains(lastAdded.getTag())) {
            /* if one of the picker fragments is visible, revert back to
               BottomPaneFragment instead of firing onBackPressed */
            resetBottomPane();
        } else if (editMode) {
            startBottomTransitionToFragment(HeadacheDetailFrontFragment.of(editedHeadache),
                    "detailFront", M.screenHeight() - ViewUtils
                            .getStatusBarHeight() - M.dp(128.f + 16.f).intValue(), false, false);
            replaceBackdropFragment(HeadacheDetailBackdropFragment.of(editedHeadache));
            toolbar.postDelayed(this::resetToolbar, 300L);
            editMode = false;
            editedHeadache = null;
        } else if (MAIN_LIST_TAG.equals(lastAdded.getTag())) {
            super.onBackPressed();
        } else {
            revertToMainListFragment();
        }
    }

    public static MaterialShapeDrawable createBottomPaneRoundedBackground() {
        final ShapeAppearanceModel model = new ShapeAppearanceModel.Builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, M.dp(16.f))
                .setTopRightCorner(CornerFamily.ROUNDED, M.dp(16.f))
                .build();
        final MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(model);
        materialShapeDrawable.setFillColor(ColorStateList.valueOf(Color.WHITE));
        materialShapeDrawable.setElevation(M.dp(8.f));
        return materialShapeDrawable;
    }
}