package com.worker.facedetector;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.worker.facedetector.helpers.ConstantsHelper;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_remove_red_eye_24dp);
        askForPermission();
    }

    private void askForPermission() {
        PermissionUtil.with(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).onAllGranted(new Func() {
            protected void call() {
            }
        }).ask(134);//134 is just a random number
    }

    public void onClick(View view) throws Exception {
        switch (view.getId()) {
            case R.id.recognizeBtn:
                if (!new File(ConstantsHelper.XML_FACE_RECOG_PATH).isFile()) {
                    Toast.makeText(this, "You haven't trained data!", Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(MainActivity.this, RecognizerActivity.class));
                break;
            case R.id.usersBtn:
                startActivity(new Intent(MainActivity.this, UserListActivity.class));
                break;
            case R.id.settingBtn:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.helpBtn:
                startActivity(new Intent(this, AboutActivity.class));
            case R.id.quitBtn:
                finish();
        }
    }


}
