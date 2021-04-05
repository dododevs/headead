package revolver.headead.ui.activities.record;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import revolver.headead.R;
import revolver.headead.aifa.model.Drug;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.ui.activities.DrugLookupActivity;
import revolver.headead.ui.adapters.RecordedDrugsAdapter;
import revolver.headead.ui.fragments.record2.RecordHeadacheBottomPaneFragment;
import revolver.headead.ui.fragments.record2.pickers.KnownDrugsPickerFragment;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.M;
import revolver.headead.util.ui.ViewUtils;

import static revolver.headead.ui.activities.record.RecordHeadacheActivity2.createBottomPaneRoundedBackground;

public class RecordDrugsActivity extends AppCompatActivity {

    public static final int REQUEST_DRUG_LOOKUP = "letsFindYouAFix".hashCode() & 0xff;
    public static final int REQUEST_PICK_DRUG_FROM_SAVED = "secondRoundHuh?".hashCode() & 0xff;

    private View noResultsView;
    private List<DrugIntake> drugs = new ArrayList<>();
    private RecordedDrugsAdapter adapter;

    private Fragment.SavedState knownDrugsPickerFragmentSavedState;
    private KnownDrugsPickerFragment knownDrugsPickerFragment;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_drugs);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.add) {
                startActivityForResult(new Intent(
                        this, DrugLookupActivity.class), REQUEST_DRUG_LOOKUP);
            }
            return true;
        });

        final RecyclerView listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        findViewById(R.id.fab).setOnClickListener((v) -> {
            setResult(RESULT_OK, new Intent()
                    .putParcelableArrayListExtra("drugs", new ArrayList<>(drugs)));
            finish();
        });

        noResultsView = findViewById(R.id.no_results);
        drugs = new ArrayList<>(Objects.requireNonNull(
                getIntent().getParcelableArrayListExtra("drugs")));
        listView.setAdapter(adapter = new RecordedDrugsAdapter(drugs));
        adapter.setOnRecordedDrugRemovedListener((intake) -> {
            if (adapter.getItemCount() == 0) {
                noResultsView.setVisibility(View.VISIBLE);
            }
        });
        if (!drugs.isEmpty()) {
            noResultsView.setVisibility(View.GONE);
        }

        bottomSheetBehavior = BottomSheetBehavior.from(
                findViewById(R.id.activity_record_drugs_bottom_pane));
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setPeekHeight(M.dp(64.f).intValue(), true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.setStatusBarColor(this, ColorUtils.get(this, R.color.blackOliveDark));

        if (knownDrugsPickerFragment != null) {
            knownDrugsPickerFragment.setInitialSavedState(knownDrugsPickerFragmentSavedState);
        } else {
            knownDrugsPickerFragment = new KnownDrugsPickerFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.bottom_frame, knownDrugsPickerFragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        knownDrugsPickerFragmentSavedState = getSupportFragmentManager()
                .saveFragmentInstanceState(knownDrugsPickerFragment);
        getSupportFragmentManager().beginTransaction()
                .remove(knownDrugsPickerFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_DRUG_LOOKUP || requestCode == REQUEST_PICK_DRUG_FROM_SAVED)
                && resultCode == RESULT_OK && data != null) {
            noResultsView.setVisibility(View.GONE);
            if (requestCode == REQUEST_DRUG_LOOKUP) {
                drugs.add(data.getParcelableExtra("intake"));
            } else /* if (requestCode == REQUEST_PICK_DRUG_FROM_SAVED) */ {
                drugs.add(data.getParcelableExtra("drugIntake"));
                knownDrugsPickerFragment.setShowSaved(false);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public BottomSheetBehavior<LinearLayout> getBottomSheetBehavior() {
        return bottomSheetBehavior;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior != null) {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                knownDrugsPickerFragment.setShowSaved(false);
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING) {
                final View rootView = knownDrugsPickerFragment.getView();
                if (rootView != null) {
                    rootView.post(() -> knownDrugsPickerFragment.setShowSaved(false));
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}
