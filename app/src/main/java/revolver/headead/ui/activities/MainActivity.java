package revolver.headead.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import revolver.headead.R;
import revolver.headead.ui.activities.record.RecordHeadacheActivity;
import revolver.headead.ui.activities.record.RecordHeadacheActivity2;
import revolver.headead.ui.activities.record.RecordHeadacheActivity3;
import revolver.headead.ui.activities.record.RecordHeadacheActivity4;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, RecordHeadacheActivity2.class));
    }
}