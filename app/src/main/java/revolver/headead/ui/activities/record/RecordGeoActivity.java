package revolver.headead.ui.activities.record;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import revolver.headead.R;
import revolver.headead.ui.adapters.GeocoderResultsAdapter;
import revolver.headead.util.logic.Async;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.Keyboards;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.ViewUtils;

public class RecordGeoActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = "lemmeTrackYou".hashCode() & 0xff;

    private GoogleMap theMap;
    private Location userLocation;
    private CameraPosition cameraPosition;

    private boolean userRequestedGpsProvider = false;
    private boolean userDeniedGpsProvider = false;
    private boolean locationRetrievalInProgress = false;
    private boolean searchInProgress = false;

    private ImageView backView;
    private FloatingActionButton requestLocationView;
    private LocationListener lastLocationListener;

    private SearchView searchView;
    private SearchView.OnCloseListener searchCloseListener;
    private RecyclerView geocoderResultsView;
    private View geocoderNoResultsView;
    private View geocoderResultsContainerView;
    private GeocoderResultsAdapter geocoderResultsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_geo);

        backView = findViewById(R.id.back);
        backView.setOnClickListener((v) -> onBackPressed());

        final ImageView markerView = findViewById(R.id.activity_record_geo_marker);
        try {
            markerView.setImageBitmap(BitmapFactory.decodeStream(getAssets()
                            .open("images/ic_map_marker.png")));
        } catch (IOException e) {
            markerView.setImageResource(R.drawable.maps_default_marker);
        }
        markerView.setOnClickListener((v) -> askPositionConfirmation(false));

        final SupportMapFragment mapFragment = new SupportMapFragment();
        findViewById(R.id.frame).postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, mapFragment)
                        .commit();
            }
        }, 500);
        mapFragment.getMapAsync(googleMap -> {
            theMap = googleMap;
            theMap.getUiSettings().setRotateGesturesEnabled(false);
            theMap.setOnCameraMoveStartedListener((i) ->
                    markerView.animate()
                            .scaleX(1.3f)
                            .scaleY(1.3f)
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(200L).start());
            theMap.setOnCameraIdleListener(() -> {
                markerView.animate()
                        .scaleX(1.f)
                        .scaleY(1.f)
                        .setInterpolator(new LinearInterpolator())
                        .setDuration(200L).start();
                userLocation = new Location(LocationManager.GPS_PROVIDER);
                userLocation.setLatitude(theMap.getCameraPosition().target.latitude);
                userLocation.setLongitude(theMap.getCameraPosition().target.longitude);
                cameraPosition = theMap.getCameraPosition();
            });
            if (cameraPosition != null) {
                theMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else if (userLocation != null) {
                theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(userLocation.getLatitude(), userLocation.getLongitude()),
                            userLocation.getLatitude() == 0.f ||
                                    userLocation.getLongitude() == 0.f ? 4.f : 12.5f));
            }
        });

        searchView = findViewById(R.id.search);
        if (Geocoder.isPresent()) {
            searchView.setOnQueryTextListener(new GeocoderSearchListener());
            searchView.setOnSearchClickListener((v) -> startNewSearch());
            searchView.setOnCloseListener(() -> {
                quitCurrentSearch();
                return false;
            });
            searchView.setOnCloseListener(searchCloseListener = () -> {
                quitCurrentSearch();
                return false;
            });

            geocoderResultsContainerView = findViewById(R.id.activity_record_geo_results_container);
            geocoderResultsView = findViewById(R.id.activity_record_geo_results);
            geocoderNoResultsView = findViewById(R.id.activity_record_geo_no_results);

            geocoderResultsView.setLayoutManager(
                    new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            geocoderResultsView.setAdapter(
                    geocoderResultsAdapter = new GeocoderResultsAdapter(new ArrayList<>()));
            geocoderResultsAdapter.setOnGeocoderResultClickedListener(address -> {
                theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(address.getLatitude(), address.getLongitude()), 14.5f));
                quitCurrentSearch();

                /* if the user has selected a result cancel the ongoing location retrieval, in any */
                if (lastLocationListener != null) {
                    getLocationManager().removeUpdates(lastLocationListener);
                }
                stopLocationLoading();
                locationRetrievalInProgress = false;
            });
        } else {
            searchView.setVisibility(View.GONE);
        }

        requestLocationView = findViewById(R.id.fab);
        requestLocationView.setOnClickListener((v) -> {
            if (locationRetrievalInProgress) {
                return;
            }
            if (userDeniedGpsProvider) {
                checkForPositioningPermission();
            } else {
                requestCurrentLocation();
            }
        });

        /* don't start the localization process if the location has already been set */
        if (getIntent().getParcelableExtra("location") == null) {
            checkForPositioningPermission();
            userLocation = new Location(LocationManager.GPS_PROVIDER);

            /* initialize location to the center of the map in case the user doesn't move */
            userLocation.setLatitude(0.0f);
            userLocation.setLongitude(0.0f);

            Snacks.longer(findViewById(R.id.activity_record_geo),
                    getString(R.string.activity_record_geo_retrieving_location), true
            );
        } else {
            userLocation = getIntent().getParcelableExtra("location");
            cameraPosition = getIntent().getParcelableExtra("cameraPosition");
            Snacks.longer(findViewById(R.id.activity_record_geo),
                    getString(R.string.activity_record_geo_saved_location),
                    false,
                    getString(R.string.activity_record_geo_saved_location_remove),
                    v -> discardCurrentLocationAndExit()
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                permissions.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkForGpsProvider();
        } else {
            userDeniedGpsProvider = true;
        }
    }

    private void startLocationLoading() {
        requestLocationView.setImageDrawable(ViewUtils
                .getIndeterminateProgressDrawable(this, R.color.black));
    }

    private void stopLocationLoading() {
        requestLocationView.setImageResource(R.drawable.ic_geo);
    }

    private void startNewSearch() {
        searchInProgress = true;
        findViewById(R.id.activity_record_geo_results_container).setVisibility(View.VISIBLE);
        backView.setImageResource(R.drawable.ic_search_light);
        backView.setClickable(false);
    }

    private void startSearchLoading() {
        backView.setImageDrawable(ViewUtils
                .getIndeterminateProgressDrawable(this, R.color.white));
    }

    private void stopSearchLoading() {
        backView.setImageResource(R.drawable.ic_search_light);
    }

    private void quitCurrentSearch() {
        searchInProgress = false;
        geocoderResultsContainerView.animate()
                .translationYBy(M.dp(-64.f))
                .setDuration(200L)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    geocoderResultsContainerView.setVisibility(View.GONE);
                    geocoderResultsContainerView.setTranslationY(0.0f);
                }).start();
        geocoderResultsAdapter.updateAddressesList(new ArrayList<>());
        geocoderResultsView.setVisibility(View.GONE);
        geocoderNoResultsView.setVisibility(View.GONE);
        backView.setImageResource(R.drawable.ic_close_light);
        backView.setClickable(true);

        /* workaround to avoid endless loop: SearchView.setIconified() invokes the listener,
           so it must be unregistered before the method is called and re-set afterwards */
        searchView.setOnCloseListener(null);

        /* another workaround: the first call clears the query, the second one closes the SearchView */
        searchView.setIconified(true);
        searchView.setIconified(true);

        searchView.setOnCloseListener(searchCloseListener);
    }

    private void askPositionConfirmation(boolean exitIfNegative) {
        new MaterialAlertDialogBuilder(this, R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_geo_confirm_position_title)
                .setMessage(R.string.dialog_geo_confirm_position_message)
                .setPositiveButton(R.string.dialog_geo_confirm_position_positive, (dialog, which) -> {
                    setResult(RESULT_OK, new Intent()
                            .putExtra("location", userLocation)
                            .putExtra("cameraPosition", theMap.getCameraPosition()));
                    finish();
                })
                .setNegativeButton(R.string.dialog_geo_confirm_position_negative, (dialog, which) -> {
                    if (exitIfNegative) {
                        finish();
                    }
                }).create().show();
    }

    private void discardCurrentLocationAndExit() {
        setResult(RESULT_OK, new Intent()
                .putExtra("location", (Parcelable) null)
                .putExtra("cameraPosition", (Parcelable) null));
        finish();
    }

    private void checkForPositioningPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
        } else {
            checkForGpsProvider();
        }
    }

    private void checkForGpsProvider() {
        if (isGpsEnabled()) {
            requestCurrentLocation();
        } else {
            askUserToEnableGpsProvider();
        }
    }

    private void requestCurrentLocation() {
        locationRetrievalInProgress = true;
        startLocationLoading();
        try {
            getLocationManager().requestSingleUpdate(LocationManager.GPS_PROVIDER, lastLocationListener = new SimpleLocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationRetrievalInProgress = false;
                    stopLocationLoading();
                    /* if the map hasn't loaded yet the camera position will be updated later */
                    if (theMap != null) {
                        theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                ), 14.5f)
                        );
                    }
                    userLocation = new Location(location);
                }

                @Override
                public void onProviderDisabled(String provider) {
                    userDeniedGpsProvider = true;
                    if (locationRetrievalInProgress) {
                        stopLocationLoading();
                    }
                }
            }, getMainLooper());
        } catch (SecurityException e) {
            userDeniedGpsProvider = true;
            stopLocationLoading();
        }
    }

    private void askUserToEnableGpsProvider() {
        new MaterialAlertDialogBuilder(this, R.style.ColoredMaterialAlert)
                .setTitle(R.string.dialog_geo_enable_gps_title)
                .setMessage(R.string.dialog_geo_enable_gps_message)
                .setPositiveButton(R.string.dialog_geo_enable_gps_positive, (dialog, which) -> {
                    userRequestedGpsProvider = true;
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }).setNegativeButton(R.string.dialog_geo_enable_gps_negative, (dialog, which) -> {
            userDeniedGpsProvider = true;
        }).create().show();
    }

    private LocationManager getLocationManager() {
        return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private boolean isGpsEnabled() {
        return getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.setStatusBarColor(this, ColorUtils.get(this, R.color.blackOliveDark));
        if (userRequestedGpsProvider) {
            if (isGpsEnabled()) {
                requestCurrentLocation();
            } else {
                askUserToEnableGpsProvider();
            }
            userRequestedGpsProvider = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (searchInProgress) {
            quitCurrentSearch();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private class GeocoderSearchListener implements SearchView.OnQueryTextListener {
        private Geocoder geocoder;
        private Async<List<Address>> lastCall;

        @Override
        public boolean onQueryTextSubmit(String query) {
            Keyboards.hideOnWindowAttached(searchView);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.isEmpty()) {
                return false;
            }
            if (lastCall != null) {
                lastCall.cancel();
            }
            startSearchLoading();
            lastCall = Async.run(() -> ensureGeocoder().getFromLocationName(newText, 20))
                    .allowNullResult(false)
                    .andThen(new Async.Callback<List<Address>>() {
                        @Override
                        public void done(final List<Address> addresses) {
                            stopSearchLoading();
                            geocoderResultsAdapter.updateAddressesList(addresses);
                            if (addresses.isEmpty()) {
                                geocoderResultsView.setVisibility(View.GONE);
                                geocoderNoResultsView.setVisibility(View.VISIBLE);
                            } else {
                                geocoderNoResultsView.setVisibility(View.GONE);
                                geocoderResultsView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void error(Exception e) {
                            stopSearchLoading();
                            geocoderResultsAdapter.updateAddressesList(new ArrayList<>());
                            geocoderResultsView.setVisibility(View.GONE);
                            geocoderNoResultsView.setVisibility(View.VISIBLE);
                            Toast.makeText(
                                    RecordGeoActivity.this,
                                    R.string.activity_record_geo_geocoder_error,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
            lastCall.start();
            return true;
        }

        private Geocoder ensureGeocoder() {
            if (geocoder == null) {
                geocoder = new Geocoder(RecordGeoActivity.this, Locale.getDefault());
            }
            return geocoder;
        }
    }
}
