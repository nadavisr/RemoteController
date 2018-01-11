/*
 * Created by admin on 03/10/2017
 * Last modified 13:34 03/10/17
 */

package com.example.admin.myapplication.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TimerTask;

import businessLogic.droneVideoProvider.FFmpegService;
import businessLogic.droneVideoProvider.ImageProvider;
import ffmpegLoad.ImageClassifier;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video.</P>
 * <P>{@link TimerTask} that rendering the newest {@link Bitmap} on the {@link VideoSurface}.</P>
 */
class RenderingTimerTask extends TimerTask {

    //region Fields

    private final VideoSurface m_videoSurface;
    private  TextView m_textview;
    private final Bitmap m_defaultBitmap;
    private Context context;
    private Bitmap m_lastBitmap;
    private int m_count;

    private ImageClassifier imageClassifier;
    int imageCount = 0;
    //endregion

    //region Constructors

    FFmpegService m_service;
    RenderingTimerTask(VideoSurface videoSurface, Bitmap defaultBitmap, Context ctx, TextView textview) {
        m_defaultBitmap = defaultBitmap;
        m_videoSurface = videoSurface;
        m_lastBitmap = m_videoSurface.getCurrentBitmap();
        m_count = 0;
        this.context = ctx;
        m_textview = textview;
        m_service = new FFmpegService(imageProvider, context);
        try {
            imageClassifier = new ImageClassifier(ctx);
        } catch (IOException e) {

            Log.e("ImageClassifier", e.getMessage());
        }
        m_service.start();
    }

    ImageProvider imageProvider = new ImageProvider() {
        @Override
        public void frameReady(byte[] rgb, int width, int hight) {

            imageCount++;

            if (imageCount % 2 != 0)
                return;

            if (imageCount % 10 == 0)
            {

                String answer = imageClassifier.classifyFrame(rgb);
                String[] arr = answer.split("\n");
                if (arr != null && arr.length > 0) {
                    String[] data = arr[1].split(":");
                    if (data != null && data.length > 1) {
                        double precentInDoub = Double.valueOf(data[1]);
                        int precent = (int) (precentInDoub * 100);
                        if (precent > 50) {
                            String cla = data[0] + " " + precent + "%";
                            try {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_textview.setText(cla);

                                    }//public void run() {
                                });

                            } catch (Exception exc) {
                                Log.e("Error", exc.getMessage());
                            }
                        } else {
                            try {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        m_textview.setText("");

                                    }//public void run() {
                                });

                            } catch (Exception exc) {
                                Log.e("Error", exc.getMessage());
                            }
                        }
                    }
                }
            }

            byte[] newArr = new byte[width * hight * 4];

            for (int i = 0; i < hight; i++) {
                for (int j = 0; j < width; j++) {
                    int offset = (i * hight + j) * 3;
                    int newOffset = (i * hight + j) * 4;
                    for (int k = 0; k < 4; k++) {
                        newArr[newOffset] = rgb[offset + 2];
                        newArr[newOffset + 1] = rgb[offset + 1];
                        newArr[newOffset + 2] = rgb[offset];
                        newArr[newOffset + 3] = (byte) 255;
                    }
                }
            }


            Bitmap orig = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
            orig.copyPixelsFromBuffer(ByteBuffer.wrap(newArr));

            m_videoSurface.setDroneBitmap(orig);
        }

    };

    //endregion

    //region TimerTask Implementation

    @Override
    public void run() {
        Bitmap currentBitmap = m_videoSurface.getCurrentBitmap();
        if (currentBitmap == m_defaultBitmap) {
            return;
        }

        if (m_lastBitmap != currentBitmap) {
            m_lastBitmap = currentBitmap;
            m_videoSurface.setBitmap(currentBitmap);
            m_count = 0;
            return;
        }

        m_count++;
        if (m_count == 100) {
            m_lastBitmap = m_defaultBitmap;
            m_videoSurface.setBitmap(m_lastBitmap);
            m_count = 0;
        }
    }

    //endregion
}
