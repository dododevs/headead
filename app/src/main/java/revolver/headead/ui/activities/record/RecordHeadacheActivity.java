package revolver.headead.ui.activities.record;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import revolver.headead.R;
import revolver.headead.ui.fragments.record.RecordPainFragment;

public class RecordHeadacheActivity extends AppCompatActivity {

    private Bundle chainDataBundle = new Bundle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_headache);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new RecordPainFragment())
                .commit();
    }

    public Bundle getChainDataBundle() {
        return chainDataBundle;
    }
}
