package com.company.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.company.libray.StageProgressBar;

public class MainActivity extends AppCompatActivity {

    StageProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (StageProgressBar) findViewById(R.id.progressBar);

        progressBar.setMaxSpaceCount(6);
        progressBar.setCurProgress(0);
        progressBar.setProgressChanged(new StageProgressBar.IProgressChanged() {
            @Override
            public void onChanged(int progress) {
                Toast.makeText(MainActivity.this, ""+progress, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
