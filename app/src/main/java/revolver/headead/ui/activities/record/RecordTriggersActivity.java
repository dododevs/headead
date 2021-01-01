package revolver.headead.ui.activities.record;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import revolver.headead.App;
import revolver.headead.R;
import revolver.headead.core.model.Trigger;
import revolver.headead.ui.adapters.TriggersAdapter;

public class RecordTriggersActivity extends AppCompatActivity {

    private List<Trigger> selectedTriggers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_triggers);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());

        final TriggersAdapter adapter = new TriggersAdapter(App.getAllTriggers());
        adapter.setOnTriggersChangedListener(new TriggersAdapter.OnTriggersChangedListener() {
            @Override
            public void onTriggerAdded(Trigger trigger) {
                selectedTriggers.add(trigger);
            }

            @Override
            public void onTriggerRemoved(Trigger trigger) {
                selectedTriggers.remove(trigger);
            }
        });

        selectedTriggers = getIntent().getParcelableArrayListExtra("triggers");
        if (selectedTriggers != null) {
            adapter.setSelectedTriggers(selectedTriggers);
        } else {
            adapter.setSelectedTriggers(selectedTriggers = new ArrayList<>());
        }

        final RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fab).setOnClickListener((v) -> {
            setResult(RESULT_OK, new Intent().putParcelableArrayListExtra(
                    "triggers", new ArrayList<>(selectedTriggers)));
            finish();
        });
    }
}
