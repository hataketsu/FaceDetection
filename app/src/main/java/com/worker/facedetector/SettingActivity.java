package com.worker.facedetector;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    public static final String LBP_THRESHOLD = "lbp_threshold";
    public static final String LBP_RADIUS = "lbp_radius";
    public static final String LBP_GRID_X = "lbp_gridX";
    public static final String LBP_GRID_Y = "lbp_gridY";
    public static final String LBP_NEIGHBORS = "lbp_neighbors";
    public static final String EIGEN_THRESHOLD = "eigen_threshold";
    public static final String EIGEN_COMPONENTS = "eigen_components";
    public static final String FISHER_COMPONENTS = "fisher_components";
    public static final String FISHER_THRESHOLD = "fisher_threshold";
    private Spinner algos;
    private View view;
    private SharedPreferences preferences;
    private EditText lbp_threshold;
    private EditText lbp_radius;
    private EditText lbp_gridX;
    private EditText lbp_gridY;
    private EditText lbp_neighbors;
    private EditText eigen_threshold;
    private EditText eigen_components;
    private EditText fisher_components;
    private EditText fisher_threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        initViews();
    }

    private void initViews() {
        algos = findViewById(R.id.algorithms);
        lbp_threshold = findViewById(R.id.lbp_threshold);
        lbp_radius = findViewById(R.id.lbp_radius);
        lbp_gridX = findViewById(R.id.lbp_gridX);
        lbp_gridY = findViewById(R.id.lbp_gridY);
        lbp_neighbors = findViewById(R.id.lbp_neighbors);

        eigen_threshold = findViewById(R.id.eigen_threshold);
        eigen_components = findViewById(R.id.eigen_components);

        fisher_components = findViewById(R.id.fisher_components);
        fisher_threshold = findViewById(R.id.fisher_threshold);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fillViews();
    }

    private void fillViews() {
        algos.setSelection(preferences.getInt("algorithm", 0));

        lbp_threshold.setText("" + preferences.getInt(LBP_THRESHOLD, 2000));
        lbp_radius.setText("" + preferences.getInt(LBP_RADIUS, 2));
        lbp_gridX.setText("" + preferences.getInt(LBP_GRID_X, 8));
        lbp_gridY.setText("" + preferences.getInt(LBP_GRID_Y, 8));
        lbp_neighbors.setText("" + preferences.getInt(LBP_NEIGHBORS, 8));

        eigen_threshold.setText("" + preferences.getInt(EIGEN_THRESHOLD, 2000));
        eigen_components.setText("" + preferences.getInt(EIGEN_COMPONENTS, 80));

        fisher_components.setText("" + preferences.getInt(FISHER_COMPONENTS, 80));
        fisher_threshold.setText("" + preferences.getInt(FISHER_THRESHOLD, 2000));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.action_save) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        SharedPreferences.Editor edit = preferences.edit();
        try {
            edit.putInt("algorithm", algos.getSelectedItemPosition());
            edit.putInt(LBP_THRESHOLD, Integer.parseInt(lbp_threshold.getText().toString()));
            edit.putInt(LBP_RADIUS, Integer.parseInt(lbp_radius.getText().toString()));
            edit.putInt(LBP_GRID_X, Integer.parseInt(lbp_gridX.getText().toString()));
            edit.putInt(LBP_GRID_Y, Integer.parseInt(lbp_gridY.getText().toString()));
            edit.putInt(LBP_NEIGHBORS, Integer.parseInt(lbp_neighbors.getText().toString()));

            edit.putInt(EIGEN_THRESHOLD, Integer.parseInt(eigen_threshold.getText().toString()));
            edit.putInt(EIGEN_COMPONENTS, Integer.parseInt(eigen_components.getText().toString()));
            edit.putInt(FISHER_COMPONENTS, Integer.parseInt(fisher_components.getText().toString()));
            edit.putInt(FISHER_THRESHOLD, Integer.parseInt(fisher_threshold.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Input error!!", Toast.LENGTH_LONG).show();
            return;
        }
        edit.apply();
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void reset(View view) {
        preferences.edit().clear().apply();
        fillViews();
    }
}
