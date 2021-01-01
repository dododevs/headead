package revolver.headead.ui.activities.record;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import revolver.headead.R;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.core.model.PainIntensity;
import revolver.headead.core.model.PainLocation;
import revolver.headead.core.model.PainType;
import revolver.headead.core.model.Trigger;
import revolver.headead.ui.fragments.SimpleAlertDialogFragment;
import revolver.headead.ui.fragments.record2.pickers.DateTimePickerFragment;
import revolver.headead.ui.fragments.record2.pickers.PainIntensityPickerFragment;
import revolver.headead.ui.fragments.record2.pickers.PainLocationPickerFragment;
import revolver.headead.ui.fragments.record2.pickers.PainTypePickerFragment;
import revolver.headead.ui.fragments.record2.RecordHeadacheBottomPaneFragment;
import revolver.headead.util.misc.TimeFormattingUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.ViewUtils;

public class RecordHeadacheActivity2 extends AppCompatActivity {

    private static final String LOCATION_PICKER_TAG = "locationPicker";
    private static final String INTENSITY_PICKER_TAG = "intensityPicker";
    private static final String TYPE_PICKER_TAG = "typePickerTag";
    private static final String DATETIME_PICKER_TAG = "dateTimePicker";
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
    private FrameLayout bottomSheetFrame;
    private FrameLayout bottomSheetDimmer;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private PainLocation painLocation;
    private PainIntensity painIntensity;
    private PainType painType;
    private boolean isAuraEnabled;
    private Map<Trigger, Boolean> triggersStatus;
    private ArrayList<DrugIntake> drugDosages = new ArrayList<>();
    private CameraPosition currentCameraPosition;
    private Location currentLocation;

    private Date headacheStartDate;
    private Date headacheEndDate;
    private DateTimePickerFragment.DateTimeMode headacheStartDateTimeMode;
    private DateTimePickerFragment.DateTimeMode headacheEndDateTimeMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_headache_2);

        toolbar = findViewById(R.id.toolbar);

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
    }

    public void setStartHeadacheDate(final Date date, final DateTimePickerFragment.DateTimeMode dateTimeMode) {
        headacheStartDate = date;
        headacheStartDateTimeMode = dateTimeMode;
        onHeadacheStartDateUpdated();
    }

    public void setEndHeadacheDate(final Date date, final DateTimePickerFragment.DateTimeMode dateTimeMode) {
        headacheEndDate = date;
        headacheEndDateTimeMode = dateTimeMode;
        onHeadacheEndDateUpdated();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_DRUGS) {
                drugDosages = data.getParcelableArrayListExtra("drugs");
                if (drugDosages != null) {
                    findViewById(R.id.activity_record_headache_2_medication_checked)
                            .setVisibility(drugDosages.isEmpty() ? View.GONE : View.VISIBLE);
                }
            } else if (requestCode == REQUEST_LOCATION) {
                currentLocation = data.getParcelableExtra("location");
                currentCameraPosition = data.getParcelableExtra("cameraPosition");
                if (currentLocation != null || currentCameraPosition != null) {
                    findViewById(R.id.activity_record_headache_2_geo_checked)
                            .setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.activity_record_headache_2_geo_checked)
                            .setVisibility(View.GONE);
                }
            }
        }
    }

    public void animatePainLocationChange(final PainLocation newPainLocation) {
        painLocationValueView.setText(newPainLocation.getShortStringLabel());
        if (painLocation == null) {
            painLocationIconView.animate()
                    .translationX(M.dp(-12.f))
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
        painLocation = newPainLocation;
        painLocationCardView.findViewById(
                R.id.activity_record_headache_2_pain_location_checked).setVisibility(View.VISIBLE);
    }

    public void animatePainIntensityChange(final PainIntensity newPainIntensity) {
        painIntensityValueView.setText(newPainIntensity.getShortStringLabel());
        if (painIntensity == null) {
            painIntensityIconView.animate()
                    .translationX(M.dp(-12.f))
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

    public void animatePainTypeChange(final PainType newPainType) {
        painTypeValueView.setText(newPainType.getShortStringLabel());
        if (painType == null) {
            painTypeIconView.animate()
                    .translationX(M.dp(-12.f))
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
        painType = newPainType;
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

    public BottomSheetBehavior<LinearLayout> getBottomSheetBehavior() {
        return bottomSheetBehavior;
    }

    public PainLocation getPainLocation() {
        return painLocation;
    }

    public PainIntensity getPainIntensity() {
        return painIntensity;
    }

    public PainType getPainType() {
        return painType;
    }

    public Date getHeadacheStartDate() {
        return headacheStartDate;
    }

    public Date getHeadacheEndDate() {
        return headacheEndDate;
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
                .withEndAction(() -> {
                    bottomSheetDimmer.setVisibility(View.GONE);
                }).start();
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
        if (painLocation == null) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_pain_location));
            missingData = true;
        } else if (painIntensity == null) {
            bounceMissingDataView(findViewById(R.id.activity_record_headache_2_pain_intensity));
            missingData = true;
        } else if (painType == null) {
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
        }
    }

    private void onHeadacheStartDateUpdated() {
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

    private void onHeadacheEndDateUpdated() {
        if (headacheEndDate == null) {
            headacheEndDateIconView.animate()
                    .translationX(0.f)
                    .translationY(0.f)
                    .scaleX(1.f)
                    .scaleY(1.f)
                    .alpha(1.f)
                    .setDuration(200L)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withStartAction(() -> headacheEndDateIconView
                            .setImageResource(R.drawable.ic_timespan_ongoing))
                    .start();
            headacheEndDateValueView.animate()
                    .alpha(0.f)
                    .setDuration(200L)
                    .setInterpolator(new LinearInterpolator())
                    .start();
            headacheEndDateLabelView.setText(R.string.activity_record_headache_2_datetime_ongoing);
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
                            bottomSheetBehavior.setDraggable(draggable);
                            bottomSheetBehavior.setPeekHeight(newPeekHeight, true);
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

    @Override
    public void onBackPressed() {
        final List<Fragment> fragments = getSupportFragmentManager().getFragments();
        final Fragment lastAdded = fragments.get(fragments.size() - 1);
        if (backStackedFragmentTags.contains(lastAdded.getTag())) {
            /* if one of the picker fragments is visible, revert back to
               BottomPaneFragment instead of firing onBackPressed */
            resetBottomPane();
        } else {
            super.onBackPressed();
        }
    }

    public void resetBottomPane() {
        startBottomTransitionToFragment(new RecordHeadacheBottomPaneFragment(),
                null, M.dp(96.f).intValue(), false);
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
