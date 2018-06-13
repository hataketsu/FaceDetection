package com.worker.facedetector.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.worker.facedetector.R;
import com.worker.facedetector.SettingActivity;

import org.bytedeco.javacpp.opencv_face;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.FisherFaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class ConstantsHelper {
    public static final String DIRECTORY = Environment.getExternalStorageDirectory() + "/face_detect";
    public static final String FACES_DIRECTORY = DIRECTORY + "/faces";
    public static final String XML_DIRECTORY = DIRECTORY + "/xml";
    public static final String XML_FACE_DETECT_PATH = XML_DIRECTORY + "/face_detect.xml";
    public static final String XML_FACE_RECOG_PATH = XML_DIRECTORY + "/face_recog.xml";
    public static final String LABELS_PATH = DIRECTORY + "/labels.txt";
    private static SharedPreferences preferences;

    public static FaceRecognizer getFaceRecognizer(Context context, boolean autoInit) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int algorithm = preferences.getInt("algorithm", 0);
        switch (algorithm) {
            case 0:
                faceRecognizer = LBPHFaceRecognizer.create(
                        get(SettingActivity.LBP_RADIUS, 2),
                        get(SettingActivity.LBP_NEIGHBORS, 8),
                        get(SettingActivity.LBP_GRID_X, 8),
                        get(SettingActivity.LBP_GRID_Y, 8),
                        get(SettingActivity.LBP_THRESHOLD, 2000));
                break;
            case 1:
                faceRecognizer = EigenFaceRecognizer.create(
                        get(SettingActivity.EIGEN_COMPONENTS, 80),
                        get(SettingActivity.EIGEN_THRESHOLD, 2000));
                break;
            case 2:
                faceRecognizer = FisherFaceRecognizer.create(
                        get(SettingActivity.FISHER_COMPONENTS, 80),
                        get(SettingActivity.FISHER_THRESHOLD, 2000));
                break;
        }
        if (new File(XML_FACE_RECOG_PATH).isFile() && autoInit)
            faceRecognizer.read(XML_FACE_RECOG_PATH);
        return faceRecognizer;
    }


    private static int get(String key, int _default) {
        return preferences.getInt(key, _default);
    }

    public static int getAlgorithm(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("algorithm", 0);
    }

    public static FaceRecognizer faceRecognizer;

    public static CascadeClassifier getFaceDetector(Context context) {
        File folder = new File(XML_DIRECTORY);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_default);
        File mCascadeFile = new File(XML_FACE_DETECT_PATH);
        try {
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        faceDetector.load(mCascadeFile.getAbsolutePath());
        if (faceDetector.empty()) {
            faceDetector = null;
        }

        return faceDetector;
    }

    public static CascadeClassifier faceDetector;
}

