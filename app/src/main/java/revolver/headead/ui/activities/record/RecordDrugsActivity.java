package revolver.headead.ui.activities.record;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import revolver.headead.R;
import revolver.headead.aifa.model.Drug;
import revolver.headead.aifa.model.DrugPackaging;
import revolver.headead.core.model.DrugIntake;
import revolver.headead.ui.activities.DrugLookupActivity;
import revolver.headead.ui.adapters.RecordedDrugsAdapter;
import revolver.headead.util.ui.ColorUtils;
import revolver.headead.util.ui.ViewUtils;

public class RecordDrugsActivity extends AppCompatActivity {

    private static final int REQUEST_DRUG_LOOKUP = "letsFindYouAFix".hashCode() & 0xff;

    private View noResultsView;
    private List<DrugIntake> drugs = new ArrayList<>();
    private RecordedDrugsAdapter adapter;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewUtils.setStatusBarColor(this, ColorUtils.get(this, R.color.blackOliveDark));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DRUG_LOOKUP && resultCode == RESULT_OK && data != null) {
            noResultsView.setVisibility(View.GONE);
            drugs.add(data.getParcelableExtra("intake"));
            adapter.notifyDataSetChanged();
        }
    }
}
