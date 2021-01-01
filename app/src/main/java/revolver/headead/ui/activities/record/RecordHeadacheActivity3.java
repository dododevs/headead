package revolver.headead.ui.activities.record;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import revolver.headead.R;
import revolver.headead.ui.fragments.record2.RecordHeadacheMainFragment;

public class RecordHeadacheActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_headache_3);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());

        new RecordHeadacheMainFragment().show(getSupportFragmentManager(),
                RecordHeadacheMainFragment.class.getSimpleName());
    }
}
