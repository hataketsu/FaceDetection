package com.worker.facedetector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.worker.facedetector.helpers.ConstantsHelper;
import com.worker.facedetector.helpers.LabelsHelper;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.opencv.core.Core.FONT_HERSHEY_DUPLEX;
import static org.opencv.core.CvType.CV_32SC1;

public class RecognizerActivity extends AppCompatActivity implements IPickResult {

    public static final Scalar GREEN_COLOR = new Scalar(0, 255, 0, 0);
    private ImageView imageIV;

    static {
        System.loadLibrary("opencv_java");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recognize image");
        initViews();
        startTraining();
    }

    private void initViews() {
        imageIV = findViewById(R.id.imageIV);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.open_image) {
            PickImageDialog.build(new PickSetup()).show(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPickResult(PickResult pickResult) {

        if (pickResult.getError() == null) {
            MatOfRect faces = new MatOfRect();
            int algorithm = ConstantsHelper.getAlgorithm(this);

            Bitmap bitmap = pickResult.getBitmap();

            Mat colorMat = new Mat();
            Mat grayMat = new Mat();
            Utils.bitmapToMat(bitmap, colorMat);
            Imgproc.cvtColor(colorMat, grayMat, Imgproc.COLOR_BGRA2GRAY);

            CascadeClassifier faceDetector = ConstantsHelper.getFaceDetector(this);
            faceDetector.detectMultiScale(grayMat, faces, 1.1, 2, 2, new Size(0, 0), new Size());
            Log.d("FACES", "Faces: " + faces.size());
            Rect[] faceArray = faces.toArray();
            if (faceArray.length > 0) {

                FaceRecognizer faceRecognizer = ConstantsHelper.getFaceRecognizer(this, true);
                Map<Integer, String> labels = LabelsHelper.readLabels();
                for (Rect face : faceArray) {
                    Mat faceMat = grayMat.submat(face);
                    Mat resized = new Mat();
                    if (algorithm != 0) {
                        Imgproc.resize(faceMat, resized, new Size(100, 100));
                        faceMat = resized;
                    }
                    int label = faceRecognizer.predict_label(faceMat);
                    String text = labels.get(label);
                    if (text == null || text.trim().length() == 0)
                        text = "Unknown";
                    Imgproc.rectangle(colorMat, face.tl(), face.br(), GREEN_COLOR, 2);
                    Point tl = new Point();
                    tl.x = face.tl().x - 1;
                    tl.y = face.tl().y - 15;
                    Point br = new Point();
                    br.x = tl.x + text.length() * 12;
                    br.y = face.tl().y;
                    Imgproc.rectangle(colorMat, tl, br, GREEN_COLOR, -1);
                    Point textPos = face.tl().clone();
                    textPos.y -= 2;
                    textPos.x += 2;
                    Imgproc.putText(colorMat, text, textPos, FONT_HERSHEY_DUPLEX, 0.5, new Scalar(255, 255, 255, 0), 1, Core.LINE_AA, false);
                }
            }
            Utils.matToBitmap(colorMat, bitmap);
            imageIV.setImageBitmap(bitmap);

        }
    }

    private void startTraining() {
        final ProgressDialog dialog = ProgressDialog.show(this, "Train data", "Training..");
        dialog.show();
        dialog.setCancelable(false);
        final int algorithm = ConstantsHelper.getAlgorithm(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceRecognizer faceRecognizer = ConstantsHelper.getFaceRecognizer(RecognizerActivity.this, false);
                File folder = new File(ConstantsHelper.FACES_DIRECTORY);

                ArrayList<Mat> images = new ArrayList<>();
                ArrayList<Integer> labels = new ArrayList<>();

                Map<String, Integer> labelsMap = new HashMap<>();
                int labelIndex = 0;
                if (!folder.isDirectory())
                    folder.mkdirs();
                for (File userFolder : folder.listFiles()) {
                    labelIndex++;
                    if (userFolder.isDirectory()) {
                        labelsMap.put(userFolder.getName(), labelIndex);

                        for (File imageFile : userFolder.listFiles()) {
                            if (imageFile.isFile() && imageFile.getName().endsWith(".png")) {
                                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                Mat colorMat = new Mat();
                                Mat grayMat = new Mat();
                                Utils.bitmapToMat(bitmap, colorMat);
                                Imgproc.cvtColor(colorMat, grayMat, Imgproc.COLOR_BGRA2GRAY);
                                if (algorithm != 0) {
                                    Mat resized = new Mat();
                                    Imgproc.resize(grayMat, resized, new Size(100, 100));
                                    grayMat = resized;
                                }
                                images.add(grayMat);
                                labels.add(labelIndex);
                            }
                        }
                    }
                }
                LabelsHelper.saveLabels(labelsMap);

                Mat labelsMat = new Mat(images.size(), 1, CV_32SC1);
                int counter = 0;
                for (int label : labels) {
                    labelsMat.put(counter, 0, label);
                    counter++;
                }
                if (images.size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecognizerActivity.this, "No users image, please input some.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RecognizerActivity.this, UserListActivity.class));
                            dialog.dismiss();
                            finish();
                        }
                    });
                    return;
                }
                faceRecognizer.train(images, labelsMat);
                faceRecognizer.save(ConstantsHelper.XML_FACE_RECOG_PATH);
                dialog.dismiss();
            }
        }).start();

    }
}
