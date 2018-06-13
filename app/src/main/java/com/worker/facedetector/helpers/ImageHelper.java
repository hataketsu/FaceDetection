package com.worker.facedetector.helpers;

import android.graphics.Bitmap;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

public class ImageHelper {
    public static opencv_core.IplImage bitmapToIplImage(Bitmap bmp) {
        opencv_core.IplImage iplImage = opencv_core.IplImage.create(bmp.getWidth(), bmp.getHeight(),
                IPL_DEPTH_8U, 4);
        bmp.copyPixelsToBuffer(iplImage.getByteBuffer());
        return iplImage;
    }

    public static Bitmap iplImagetoBitmap(opencv_core.IplImage iplImage) {
        Bitmap bitmap = Bitmap.createBitmap(iplImage.width(), iplImage.height(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(iplImage.getByteBuffer());
        return bitmap;
    }

    public static opencv_core.IplImage toGray(opencv_core.IplImage image) {
        opencv_core.IplImage grayImg = opencv_core.IplImage.create(image.width(), image.height(),
                IPL_DEPTH_8U, 1);
        cvCvtColor(image, grayImg, opencv_imgproc.CV_BGR2GRAY);
        return grayImg;
    }


}
