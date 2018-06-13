package com.worker.facedetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.worker.facedetector.helpers.ConstantsHelper;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class GetNewFaceActivity extends AppCompatActivity implements IPickResult {

    static {
        System.loadLibrary("opencv_java");
    }

    private String name;
    private ImageView capturedImg;



    private ImageView croppedImg;

    MatOfRect faces = new MatOfRect();
    private View captureBtn;
    private Bitmap faceBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_new_face);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        getSupportActionBar().setTitle(name + "'s new image");
    }

    private void initViews() {
        capturedImg = findViewById(R.id.captured);
        croppedImg = findViewById(R.id.cropped);
        captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setVisibility(View.GONE);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File folder = new File(ConstantsHelper.FACES_DIRECTORY + "/" + name);
                if (!folder.isDirectory())
                    folder.mkdirs();
                File imageFile = new File(folder, System.currentTimeMillis() + ".png");
                try {
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    faceBM.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    Toast.makeText(GetNewFaceActivity.this, "Saved!", Toast.LENGTH_LONG).show();
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recog, menu);
        menu.findItem(R.id.open_image).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                captureBtn.setVisibility(View.GONE);
                PickImageDialog.build(new PickSetup()).show(GetNewFaceActivity.this);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            Bitmap bitmap = r.getBitmap();
            capturedImg.setImageBitmap(bitmap);

            Mat colorMat = new Mat();
            Mat grayMat = new Mat();
            Utils.bitmapToMat(bitmap, colorMat);
            Imgproc.cvtColor(colorMat, grayMat, Imgproc.COLOR_BGRA2GRAY);

            CascadeClassifier faceDetector = ConstantsHelper.getFaceDetector(this);
            faceDetector.detectMultiScale(grayMat, faces, 1.1, 2, 2, new Size(0, 0), new Size());
            Log.d("FACES", "Faces: " + faces.size());
            Rect[] faceArray = faces.toArray();
            if (faceArray.length > 0) {
                Rect face = faceArray[0];
                Mat faceMat = colorMat.submat(face);
                faceBM = Bitmap.createBitmap(faceMat.width(), faceMat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(faceMat, faceBM);
                croppedImg.setImageBitmap(faceBM);
                captureBtn.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
