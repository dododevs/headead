package revolver.headead.ui.fragments.record;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.core.model.Trigger;
import revolver.headead.ui.activities.record.RecordGeoActivity;
import revolver.headead.ui.activities.record.RecordTriggersActivity;

public class RecordScenarioFragment extends BaseRecordFragment {

    private enum DateTimeMode {
        DEFAULT, CUSTOM;

        static DateTimeMode fromString(final String name) {
            if (name == null) {
                return null;
            }
            for (final DateTimeMode dateTimeMode : values()) {
                if (name.equals(dateTimeMode.name())) {
                    return dateTimeMode;
                }
            }
            return null;
        }
    }
    public static final int REQUEST_GEO = "where?".hashCode() & 0xff;
    public static final int REQUEST_TRIGGERS = "why?".hashCode() & 0xff;
    private static final SimpleDateFormat customDateFormatter =
            new SimpleDateFormat("EEEE dd MMM yy", Locale.ITALIAN);
    private static final SimpleDateFormat customTimeFormatter =
            new SimpleDateFormat("HH:mm", Locale.ITALIAN);
    private static final Calendar calendarInstance = Calendar.getInstance();

    private DateTimeMode dateTimeMode = DateTimeMode.DEFAULT;
    private Date customDate;
    private boolean isTimeSet;

    private Location userLocation;
    private CameraPosition cameraPosition;
    private List<Trigger> triggers = new ArrayList<>();

    private MaterialButtonToggleGroup datetimeModeView;
    private TextView customDateView, customTimeView;
    private MaterialButton geoSetView, triggersSetView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_record_scenario, container, false);

        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> requireActivity().onBackPressed());

        rootView.findViewById(R.id.fragment_record_scenario_date).setOnClickListener((v) ->
                createDatePickerDialog((view, year, month, dayOfMonth) -> {
                    final boolean timeIsSet = customDate != null;
                    customDate = createOrUpdateDate(customDate, year, month, dayOfMonth);
                    onCustomDateUpdated(timeIsSet);
                }).show());
        rootView.findViewById(R.id.fragment_record_scenario_time).setOnClickListener((v) -> {
            if (customDate == null) {
                createCustomDateNullDialog().show();
            } else {
                createTimePickerDialog((view, hourOfDay, minute) -> {
                    customDate = createOrUpdateDate(customDate, hourOfDay, minute);
                    onCustomDateUpdated(true);
                }).show();
            }
        });

        datetimeModeView = rootView.findViewById(R.id.fragment_record_scenario_datetime_mode);
        datetimeModeView.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.fragment_record_scenario_datetime_mode_default && isChecked) {
                rootView.findViewById(R.id.fragment_record_scenario_datetime).setVisibility(View.GONE);
                dateTimeMode = DateTimeMode.DEFAULT;

                /* reset the already set custom date, if any */
                customDate = null;
                customDateView.setText(R.string.fragment_record_scenario_date_default);
                customTimeView.setText(R.string.fragment_record_scenario_time_default);
            } else if (checkedId == R.id.fragment_record_scenario_datetime_mode_custom && isChecked) {
                rootView.findViewById(R.id.fragment_record_scenario_datetime).setVisibility(View.VISIBLE);
                dateTimeMode = DateTimeMode.CUSTOM;
            }
        });

        geoSetView = rootView.findViewById(R.id.fragment_record_scenario_geo_set);
        geoSetView.setOnClickListener((v) -> startActivityForResult(
                new Intent(requireContext(), RecordGeoActivity.class)
                        .putExtra("location", userLocation)
                        .putExtra("cameraPosition", cameraPosition), REQUEST_GEO));
        triggersSetView = rootView.findViewById(R.id.fragment_record_scenario_triggers_set);
        triggersSetView.setOnClickListener((v) -> startActivityForResult
                (new Intent(requireContext(), RecordTriggersActivity.class)
                        .putParcelableArrayListExtra("triggers", triggers != null ?
                                new ArrayList<>(triggers) : new ArrayList<>()), REQUEST_TRIGGERS));
        customDateView = rootView.findViewById(R.id.fragment_record_scenario_custom_date);
        customTimeView = rootView.findViewById(R.id.fragment_record_scenario_custom_time);

        rootView.findViewById(R.id.fab).setOnClickListener((v) -> {
            final Bundle data = getRecordHeadacheActivity().getChainDataBundle();
            if (dateTimeMode == DateTimeMode.DEFAULT) {
                data.putSerializable("date", new Date(System.currentTimeMillis()));
            } else {
                if (customDate == null || !isTimeSet) {
                    createCustomDateMissingDialog().show();
                    return;
                } else {
                    data.putSerializable("date", customDate);
                }
            }
            data.putString("dateTimeMode", dateTimeMode.name());
            data.putParcelable("location", userLocation);
            data.putParcelable("cameraPosition", cameraPosition);
            if (triggers == null || triggers.isEmpty()) {
                data.putParcelableArrayList("triggers", null);
            } else {
                data.putParcelableArrayList("triggers", new ArrayList<>(triggers));
            }
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.fragment_record_scenario_slide_in,
                            R.anim.fragment_record_scenario_slide_out,
                            R.anim.fragment_record_scenario_slide_in,
                            R.anim.fragment_record_scenario_slide_out)
                    .addToBackStack(null)
                    .replace(R.id.frame, new RecordRemedyFragment())
                    .commit();
        });

        return rootView;
    }

    /* ************************ */
    /* restore persistent data! */
    /* ************************ */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        dateTimeMode = DateTimeMode.fromString(getRecordHeadacheActivity()
                .getChainDataBundle()
                .getString("dateTimeMode", DateTimeMode.DEFAULT.name()));
        switch (dateTimeMode) {
            default:
            case DEFAULT:
                datetimeModeView.check(R.id.fragment_record_scenario_datetime_mode_default);
                break;
            case CUSTOM:
                datetimeModeView.check(R.id.fragment_record_scenario_datetime_mode_custom);
                customDate = (Date) getRecordHeadacheActivity()
                        .getChainDataBundle()
                        .getSerializable("date");
                isTimeSet = true;
                onCustomDateUpdated(true);
                break;
        }
        userLocation = getRecordHeadacheActivity()
                .getChainDataBundle()
                .getParcelable("location");
        cameraPosition = getRecordHeadacheActivity()
                .getChainDataBundle()
                .getParcelable("cameraPosition");
        onUserLocationChanged();

        triggers = getRecordHeadacheActivity()
                .getChainDataBundle()
                .getParcelableArrayList("triggers");
        onTriggersListChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_GEO) {
                userLocation = data.getParcelableExtra("location");
                cameraPosition = data.getParcelableExtra("cameraPosition");
                onUserLocationChanged();
            } else if (requestCode == REQUEST_TRIGGERS) {
                triggers = data.getParcelableArrayListExtra("triggers");
                onTriggersListChanged();
            }
        }
    }

    private void onCustomDateUpdated(final boolean timeIsSet) {
        customDateView.setText(customDateFormatter.format(customDate));
        if (timeIsSet) {
            customTimeView.setText(customTimeFormatter.format(customDate));
        }
        isTimeSet = timeIsSet;
    }

    private void onTriggersListChanged() {
        if (triggers != null && !triggers.isEmpty()) {
            triggersSetView.setBackgroundColor(App.colorPrimary());
            triggersSetView.setTextColor(Color.WHITE);
        } else {
            triggersSetView.setBackgroundColor(Color.WHITE);
            triggersSetView.setTextColor(App.colorPrimary());
        }
    }

    private void onUserLocationChanged() {
        if (userLocation != null) {
            geoSetView.setBackgroundColor(App.colorPrimary());
            geoSetView.setTextColor(Color.WHITE);
        } else {
            geoSetView.setBackgroundColor(Color.WHITE);
            geoSetView.setTextColor(App.colorPrimary());
        }
    }

    private DatePickerDialog createDatePickerDialog(final DatePickerDialog.OnDateSetListener listener) {
        return createDatePickerDialog(null, listener);
    }

    private DatePickerDialog createDatePickerDialog(final Date when, final DatePickerDialog.OnDateSetListener listener) {
        final Calendar calendar = resetCalendar();
        if (when != null) {
            calendar.setTime(when);
        }
        return new DatePickerDialog(requireContext(), listener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private TimePickerDialog createTimePickerDialog(final TimePickerDialog.OnTimeSetListener listener) {
        return createTimePickerDialog(null, listener);
    }

    private TimePickerDialog createTimePickerDialog(final Date when, final TimePickerDialog.OnTimeSetListener listener) {
        final Calendar calendar = resetCalendar();
        if (when != null) {
            calendar.setTime(when);
        }
        return new TimePickerDialog(requireContext(), listener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
    }

    private static Date createOrUpdateDate(@Nullable final Date date, int year, int month, int dayOfMonth) {
        final Calendar calendar = resetCalendar();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(year, month, dayOfMonth);
        return calendar.getTime();
    }

    private static Date createOrUpdateDate(@Nullable final Date date, int hour, int minute) {
        final Calendar calendar = resetCalendar();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    private AlertDialog createCustomDateNullDialog() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_custom_date_null_title)
                .setMessage(R.string.dialog_custom_date_null_message)
                .setPositiveButton(R.string.dialog_custom_date_null_positive, null)
                .create();
    }

    private AlertDialog createCustomDateMissingDialog() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_custom_date_missing_title)
                .setMessage(R.string.dialog_custom_date_missing_message)
                .setPositiveButton(R.string.dialog_custom_date_missing_positive, null)
                .create();
    }

    private static Calendar resetCalendar() {
        calendarInstance.setTimeInMillis(System.currentTimeMillis());
        return calendarInstance;
    }
}
