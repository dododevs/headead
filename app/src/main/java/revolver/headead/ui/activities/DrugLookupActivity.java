package revolver.headead.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revolver.headead.R;
import revolver.headead.aifa.Aifa;
import revolver.headead.aifa.model.Drug;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugDosageUnit;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.ui.adapters.DrugResultsAdapter;
import revolver.headead.ui.fragments.DrugBarcodeDetectorFragment;
import revolver.headead.ui.fragments.DrugPackagingOverviewFragment;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.Keyboards;
import revolver.headead.util.ui.Snacks;
import revolver.headead.util.ui.ViewUtils;

public class DrugLookupActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_DRUG_INTAKE = "quickFixFound".hashCode() & 0xff;

    private View snackbarContainerView;
    private Call<List<Drug>> lastCall = null;
    private String lastQuery = null;

    private DrugResultsAdapter resultsAdapter;

    private RecyclerView resultsView;
    private View noResultsView;
    private View noQueryView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_lookup);

        findViewById(R.id.back).setOnClickListener((v) -> onBackPressed());

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            findViewById(R.id.barcode).setOnClickListener((v) ->
                    new DrugBarcodeDetectorFragment().show(
                            getSupportFragmentManager(), "barcodeScanner"));
        } else {
            findViewById(R.id.barcode).setVisibility(View.GONE);
        }

        final SearchView searchView = findViewById(R.id.search);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(this);

        resultsView = findViewById(R.id.results);
        resultsView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        resultsView.setAdapter(resultsAdapter =
                new DrugResultsAdapter(getSupportFragmentManager(), new ArrayList<>()));

        noResultsView = findViewById(R.id.no_results);
        noQueryView = findViewById(R.id.no_query);
        snackbarContainerView = findViewById(R.id.activity_drug_lookup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.setStatusBarColor(this, ColorUtils.get(this, R.color.blackOliveDark));
    }

    public void onFarmacodeDetected(final String farmacode) {
        startActivityForResult(new Intent(this, DrugIntakeActivity.class)
                .putExtra("id", farmacode), DrugLookupActivity.REQUEST_DRUG_INTAKE);
    }

    public void onDrugPackagingAndDosageConfirmed(final DrugIntake drugIntake) {
        setResult(RESULT_OK, new Intent().putExtra("intake", drugIntake));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DRUG_INTAKE && resultCode == RESULT_OK && data != null) {
            onDrugPackagingAndDosageConfirmed(data.getParcelableExtra("drugIntake"));
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 2) {
            lastQuery = newText;
            noQueryView.setVisibility(View.GONE);
            submitQuery();
        } else {
            invalidateLastCall(true);
            resultsView.setVisibility(View.GONE);
            noResultsView.setVisibility(View.GONE);
            noQueryView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Keyboards.hideOnWindowAttached(snackbarContainerView);
        return true;
    }

    private void submitQuery() {
        startLoading();
        invalidateLastCall(false);
        (lastCall = Aifa.getDrugLookupService()
                .search(Aifa.buildDrugQueryString(lastQuery)))
                .enqueue(new DrugLookupResponseCallback());
    }

    private void invalidateLastCall(boolean stopLoading) {
        if (lastCall != null && lastCall.isExecuted()) {
            lastCall.cancel();
        }
        if (stopLoading) {
            stopLoading();
        }
    }

    private void startLoading() {
        findViewById(R.id.wheel).setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        findViewById(R.id.wheel).setVisibility(View.INVISIBLE);
    }

    private void updateDrugList(final List<Drug> drugs) {
        if (drugs == null || drugs.isEmpty()) {
            resultsView.setVisibility(View.GONE);
            noResultsView.setVisibility(View.VISIBLE);
        } else {
            noResultsView.setVisibility(View.GONE);
            resultsView.setVisibility(View.VISIBLE);
        }
        resultsAdapter.updateDrugList(drugs);
    }

    private class DrugLookupResponseCallback implements Callback<List<Drug>> {
        @Override
        public void onResponse(@NonNull Call<List<Drug>> call, @NonNull Response<List<Drug>> response) {
            if (call.isCanceled()) {
                return;
            }
            stopLoading();
            if (response.isSuccessful()) {
                updateDrugList(response.body());
                lastCall = null;
                lastQuery = null;
            } else {
                Snacks.longer(snackbarContainerView, getString(R.string.activity_drug_lookup_error),
                        false, getString(R.string.RETRY), (v) -> submitQuery());
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Drug>> call, @NonNull Throwable t) {
            if (call.isCanceled()) {
                return;
            }
            stopLoading();
            Snacks.longer(snackbarContainerView, getString(R.string.activity_drug_lookup_error),
                    false, getString(R.string.RETRY), (v) -> submitQuery());
        }
    }
}
