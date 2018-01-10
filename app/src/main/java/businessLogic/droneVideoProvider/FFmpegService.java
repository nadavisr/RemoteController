/*
 * Created by Administrator on 02/01/2018
 * Last modified 10:24 02/01/18
 */

package businessLogic.droneVideoProvider;

import android.content.Context;

import ffmpegLoad.FFmpegLoader;

/**
 * Created by Administrator on 02/01/2018.
 */

public class FFmpegService implements FFmpegServiceInterface {

    private Context context;
    private ImageProvider imageProvider;

    public FFmpegService(ImageProvider image, Context context)
    {
        this.imageProvider = image;
        this.context = context;
    }
    @Override
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FFmpegLocal m_ffmpeg = FFmpegLocal.getInstance(context);
                m_ffmpeg.setImageProvider(imageProvider);

                //String[] cmd =new String[]{"-i", "-", "-f", "image2pipe", "-pix_fmt", "bgr32", "-vcodec", "rawvideo", "-"};
                String[] cmd =new String[]{"-i", "-", "-f", "image2pipe", "-pix_fmt", "bgr24", "-vcodec", "rawvideo", "-s", "224x224", "-"};
                try {
                    // to execute "ffmpeg -version" command you just need to pass "-version"
                    m_ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler()
                    {

                        @Override
                        public void onStart() { }

                        @Override
                        public void onProgress(String message){
                            //t.setText(message);
                        }

                        @Override
                        public void onFailure(String message){
                            //    t.setText(message);
                        }

                        @Override
                        public void onSuccess(String message){
                            //    t.setText(message);
                        }

                        @Override
                        public void onFinish() { }
                    });
                }
                catch (FFmpegCommandAlreadyRunningException e)
                {
                    // Handle if FFmpegLocal is already running
                    System.out.println("Handle if FFmpegLocal is already running");
                }
            }
        }).start();
    }

    @Override
    public void stop() {

    }
}
