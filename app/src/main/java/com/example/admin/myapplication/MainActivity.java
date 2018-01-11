/*
 * Created by admin on  27/09/2017
 * Last modified 19:17 26/09/17
 */

package com.example.admin.myapplication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.FrameLayout;

import com.bq.robotic.droid2ino.activities.BaseBluetoothSendOnlyActivity;
import com.example.admin.myapplication.bluetoothLowEnergy.BluetoothServer;
import com.example.admin.myapplication.common.Enums.Fragments;
import com.example.admin.myapplication.common.Intents;
import com.example.admin.myapplication.fragments.VideoFragment;
import com.example.admin.myapplication.listeners.RobotListener;
import com.example.admin.myapplication.notification.NotificationManager;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import businessLogic.common.BaseUncaughtExceptionHandler;
import businessLogic.common.interfaces.ILog;
import businessLogic.common.interfaces.IUncaughtExceptionHandler;
import businessLogic.droneVideoProvider.FFmpegLocal;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ffmpegLoad.ImageClassifier;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.</P>
 * <P></P>
 */

public class MainActivity extends BaseBluetoothSendOnlyActivity implements RobotListener {

    //region Fields

    //region Statics
    private final static int REQUEST_CODE_FIRST_ASK_MULTIPLE_PERMISSIONS = 124;
    private final static int REQUEST_CODE_SECOND_ASK_MULTIPLE_PERMISSIONS = 125;

/*    private static final Map<Fragments, Fragment> FRAGMENT_DICTIONARY;

    static {
        FRAGMENT_DICTIONARY = new HashMap<>(2);
        FRAGMENT_DICTIONARY.put(Fragments.Video, new VideoFragment());
        FRAGMENT_DICTIONARY.put(Fragments.Settings, new SettingsFragment());
    }*/

    private FloatingActionButton m_floatingActionButtonBluetooth;

    //endregion

    @BindView(R.id.fragment_container)
    FrameLayout m_container;

    private ILog m_logger;

    private Unbinder m_bind;

    private ApplicationBitExecutor m_bitExecutor;

    private FragmentManager m_fragmentManager;

    private MainBroadcastReceiver m_mainBroadcastReceiver;

    //endregion

    //region Methods

    //region Activity LifeCycle Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpegLocal is not supported by device
        }*/


        hideSystemUI();

        setContentView(R.layout.activity_main);

        m_floatingActionButtonBluetooth = (FloatingActionButton) findViewById(R.id.fab_bluetooth);
        initializeOnClickBluetoothListener();

        m_logger = LogManager.getLogger();

        m_logger.verbose("in onCreate");

        initializeUncaughtExceptionHandler();

        checkPermissions(REQUEST_CODE_FIRST_ASK_MULTIPLE_PERMISSIONS);

        initializeBitExecutor();

        m_mainBroadcastReceiver = new MainBroadcastReceiver();

        initializeBroadcastReceiver();

        m_bind = ButterKnife.bind(this);

        initializeWindowListeners();

        m_fragmentManager = getFragmentManager();

        setFragment(Fragments.Video, false);
    }

    BluetoothServer bluetoothServer;

    @Override
    protected void onStart() {
        m_logger.verbose("in onStart");
        m_bitExecutor.start();

//        startBluetoothServer();

        super.onStart();
    }

    @Override
    protected void onStop() {
        m_logger.verbose("in onStop");
        m_bitExecutor.stop();

//        stopBluetoothServer();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        m_logger.verbose("in onDestroy");
        if (m_bitExecutor != null) {
            m_bitExecutor.dispose();
        }

        if (m_mainBroadcastReceiver != null) {
            unregisterReceiver(m_mainBroadcastReceiver);
        }

        if (m_bind != null) {
            m_bind.unbind();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        shutdownApplication(0);
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        List<String> deniedPermissions = getDeniedPermissions(permissions, grantResults);
        if (deniedPermissions.isEmpty()) {
            m_logger.info("All necessary permissions are available.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (String deniedPermission : deniedPermissions) {
            if (sb.length() == 0) {
                sb.append(deniedPermission);
            } else {
                sb.append(", ");
                sb.append(deniedPermission);
            }
        }

        switch (requestCode) {
            case REQUEST_CODE_FIRST_ASK_MULTIPLE_PERMISSIONS:

                m_logger.warning("The next permissions are denied: " + sb.toString() + ". Ask again from the user..");
                NotificationManager.showShortToast(this, "Must approve all permissions to use the app.");
                checkPermissions(REQUEST_CODE_SECOND_ASK_MULTIPLE_PERMISSIONS);
                break;

            case REQUEST_CODE_SECOND_ASK_MULTIPLE_PERMISSIONS:
                m_logger.warning("The next permissions are denied: " + sb.toString() + " second time. Shutting down the application..");
                NotificationManager.showShortToast(this, "The application can not run without the necessary permissions.");
                shutdownApplication(4);
        }
    }

    @NonNull
    private List<String> getDeniedPermissions(String[] permissions, int[] grantResults) {
        int size = Math.min(permissions.length, grantResults.length);
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (permissions[i].isEmpty()) {
                    m_logger.warning("Some unknown permission denied.");
                } else {

                    deniedPermissions.add(permissions[i]);
                }
            }
        }
        return deniedPermissions;
    }
    //endregion

    //region Private Methods

    private void checkPermissions(int requestCode) {
        String packageName = this.getPackageName();
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException ex) {
            m_logger.warning("The file: " + packageName + ",not found! request receive permissions manually.", ex);
            askManuallyPermissions();
        }

        if (packageInfo == null) {
            m_logger.warning("PackageInfo is null! request receive permissions manually.");
            askManuallyPermissions();
        } else {
            String[] permissionsNeeded = packageInfo.requestedPermissions;
            if (permissionsNeeded != null && permissionsNeeded.length > 0) {
                ActivityCompat.requestPermissions(this, permissionsNeeded, requestCode);
            }
        }
    }

    private void askManuallyPermissions() {
        String packageName = this.getPackageName();

        NotificationManager.showShortToast(this, "Please check if all the permissions approved.");

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        this.startActivity(intent);
    }

    private void initializeWindowListeners() {
        View decorView = getWindow().getDecorView();

        View.OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener = visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                hideSystemUI();
            }
        };
        decorView.setOnSystemUiVisibilityChangeListener(onSystemUiVisibilityChangeListener);
    }

    private void initializeUncaughtExceptionHandler() {
        IUncaughtExceptionHandler uncaughtExceptionHandler
                = new UncaughtExceptionHandler();

        try {
            uncaughtExceptionHandler.initializeUncaughtExceptionHandler();
        } catch (Exception e) {
            m_logger.warning("Initialization of caught unhandled exception failed!", e);
        }
    }

    private void initializeBitExecutor() {
        m_bitExecutor = new ApplicationBitExecutor(this);

        m_bitExecutor.initialize();
    }

    private void initializeBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intents.KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION);
        registerReceiver(m_mainBroadcastReceiver, intentFilter);
    }

    private void startBluetoothServer() {
        bluetoothServer = new BluetoothServer(this);
        bluetoothServer.initialize();
        bluetoothServer.start();
        bluetoothServer.startAdvertising();
    }

    private void stopBluetoothServer() {
        bluetoothServer.stopAdvertising();
        bluetoothServer.stop();
        bluetoothServer.dispose();
    }

    private void hideSystemUI() {
        View mDecorView = getWindow().getDecorView();

        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void restartApplication() {
        NotificationManager.showShortToast(this, "Restarting the application..");
        m_logger.info("Restarting the application..");
        try {
            Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        } catch (Exception ex) {
            m_logger.fatal("Could not restart the application, shutting down the application..");
        } finally {
            shutdownApplication(2);
        }
    }

    private void shutdownApplication(int closingStatus) {
        setResult(closingStatus);
        finish();
    }


    //endregion

    //region Public Methods

    @UiThread
    public void setFragment(Fragments fragmentEnum, boolean addToBackStack) {
        if (fragmentEnum == null) {
            m_logger.warning("setFragmentWithBackStack received null");
            return;
        }

        if (Looper.getMainLooper() != Looper.myLooper()) {
            m_logger.warning("The methods setFragment do not runs on the UI thread.");
            return;
        }

/*        Fragment fragment = FRAGMENT_DICTIONARY.get(fragmentEnum);

        if (fragment == null) {
            return;
        }*/

        FragmentTransaction fragmentTransaction = m_fragmentManager.beginTransaction();

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.replace(R.id.fragment_container, new VideoFragment());
        fragmentTransaction.commit();

    }

    public void onChangeConnection(View view)
    {
        switch (view.getId()) {

            case R.id.fab_bluetooth:
                requestDeviceConnection();
                break;
/*
            case R.id.disconnect_button:
                stopBluetoothConnection();
                break;*/
        }
    }

    //endregion

    //endregion

    //region Nested Classes

    private class UncaughtExceptionHandler extends BaseUncaughtExceptionHandler {

        private UncaughtExceptionHandler() {
            super(MainActivity.this.m_logger);
        }

        @Override
        protected void innerHandleUncaughtException(Thread thread, Throwable throwable) {

            if (Looper.myLooper() != Looper.getMainLooper()) {
                return;
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Error!")
                    .setMessage("An error has occurred in the application, the application will be turned off")
                    .setCancelable(true)
                    .setPositiveButton("Exit",
                            (dialog, which) -> {
                                //dismiss the dialog
                            });

            final AlertDialog alertDialog = builder.create();

            alertDialog.show();

        }

        @Override
        protected void runClosingSequence(Thread thread, Throwable throwable) {
            shutdownApplication(1);
        }
    }

    private class MainBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                m_logger.warning("MainBroadcastReceiver received intent with Null action.");
                return;
            }
            if (action.isEmpty()) {
                m_logger.warning("MainBroadcastReceiver received intent with empty string action.");
                return;
            }

            switch (action) {
                case Intents.KEY_VIDEO_CHANNEL_STOPPED_ON_EXCEPTION:
                    Bundle bundle = intent.getExtras();
                    String exceptionMessage = "None";
                    if (bundle != null) {
                        exceptionMessage = bundle.getString(Intents.EXTRA_KEY_EXCEPTION_MESSAGE, "None");
                    }
                    m_logger.fatal("Main broad cast receiver received that Video Channel stopped due to an exception, " +
                            "the application will restart. Exception: " + exceptionMessage);
                    restartApplication();
                    break;
            }
        }
    }

    //endregion


    /**************************************************************************************
     **************************   ROBOTLISTENER CALLBACKS   *******************************
     **************************************************************************************/

    /**
     * Callback from the RobotFragment for checking if the device is connected to an Arduino
     * through the bluetooth connection.
     * If the device is not connected it warns the user of it through a Toast.
     *
     * @return true if is connected or false if not
     */
    @Override
    public boolean onCheckIsConnected() {
        return isConnected();
    }


    /**
     * Callback from the RobotFragment for checking if the device is connected to an Arduino
     * through the bluetooth connection
     * without the warning toast if is not connected.
     *
     * @return true if is connected or false if not
     */
    public boolean onCheckIsConnectedWithoutToast() {
        return isConnectedWithoutToast();
    }


    /**
     * Callback from the RobotFragment for sending a message to the Arduino through the bluetooth
     * connection.
     *
     * @param message to be send to the Arduino
     */
    @Override
    public void onSendMessage(String message) {
//		Log.e(LOG_TAG, "message to send to arduino: " + message);
        sendMessage(message);
    }

    @SuppressLint("ResourceType")
    private void initializeOnClickBluetoothListener()
    {
        if (m_floatingActionButtonBluetooth == null) {
            m_logger.warning("The methods initializeOnClickListenerBluetooth called when FloatingActionButton is null!");
            return;
        }

        removeOnClickListenerBluetooth();

        m_floatingActionButtonBluetooth.setImageResource(R.drawable.ic_close_holo_dark);
        m_floatingActionButtonBluetooth.setImageResource(17301632);

    }

    private void removeOnClickListenerBluetooth() {
        if (m_floatingActionButtonBluetooth == null) {
            m_logger.warning("The methods removeOnClickListener called when FloatingActionButton is null!");
            return;
        }
        m_floatingActionButtonBluetooth.setImageResource(0);

    }

}
