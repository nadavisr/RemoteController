/*
 * Created by admin on 03/10/2017
 * Last modified 07:53 03/10/17
 */

package com.example.admin.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.os.Environment;

import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.BitsConfiguration;
import com.example.admin.myapplication.notification.NotificationManager;

import java.util.ArrayList;
import java.util.List;

import businessLogic.bits.BitExecutor;
import businessLogic.bits.BitImpact;
import businessLogic.bits.BitResult;
import businessLogic.bits.BitResultStatus;
import businessLogic.bits.interfaces.IBit;
import businessLogic.common.interfaces.IHandler;
import services.bits.BitResultLogHandler;
import services.bits.BluetoothBit;
import services.bits.LocationBit;
import services.bits.StorageSpaceBit;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.</P>
 * <P>Application bit checks manager.</P>
 */

public class ApplicationBitExecutor extends ServicesBaseBgOperation {

    //region Fields

    private final Activity m_activity;

    private BitExecutor m_bitExecutor;

    //endregion

    //region Constructors

    public ApplicationBitExecutor(Activity activity) {
        m_activity = activity;
        m_bitExecutor = null;
    }

    //endregion

    //region IBackgroundOperation Implementation

    @SuppressWarnings("unchecked")
    @Override
    protected void internalInitialize() throws Exception {
        if (m_activity == null) {
            throw new NullPointerException("Activity is null");
        }

        // General adapters:
        BitResultToastHandler bitResultToastHandler = new BitResultToastHandler();
        BitResultLogHandler bitResultLogHandler = new BitResultLogHandler();

        List<IBit> bits = new ArrayList<>();

        // External media storage bit:
        Boolean isSDPresent = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
            IBit externalStorageBit = new StorageSpaceBit(externalStoragePath, "ExternalStorageBit", 5, BitImpact.Local);
            externalStorageBit.setBitResultHandlers(bitResultToastHandler, bitResultLogHandler);
            bits.add(externalStorageBit);
        }

        // Internal media storage bit:
        String internalStoragePath = m_activity.getDataDir().getPath();
        IBit internalStorageBit = new StorageSpaceBit(internalStoragePath, "InternalStorageBit", 5, BitImpact.Local);
        internalStorageBit.setBitResultHandlers(bitResultToastHandler, bitResultLogHandler);
        bits.add(internalStorageBit);

        // Location bit:
        LocationManager locationManager = (LocationManager) m_activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            LocationBit locationBit = new LocationBit(locationManager, 5, BitImpact.Local);
            IHandler<BitResult> bitLocationHandler = new LocationBitResultHandler();
            locationBit.setBitResultHandlers(bitLocationHandler, bitResultLogHandler);
            bits.add(locationBit);
        }

        // Bluetooth bit:
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            BluetoothBit bluetoothBit = new BluetoothBit(bluetoothAdapter, 10, BitImpact.System);
            IHandler<BitResult> bluetoothBitResultHandler = new BluetoothBitResultHandler(bluetoothAdapter);
            bluetoothBit.setBitResultHandlers(bluetoothBitResultHandler, bitResultLogHandler);
            bits.add(bluetoothBit);
        }

        // Create BitExecutor :
        BitsConfiguration bitsConfiguration = ConfigurationManager.getBitsConfiguration();
        if (bitsConfiguration == null) {
            throw new Exception("BitsConfiguration received null from ConfigurationManager");
        }
        short frequency = bitsConfiguration.getRunningFrequencyInMillis();

        m_bitExecutor = new BitExecutor(m_logger, bits, frequency);
    }

    @Override
    protected void internalStart() {
        m_bitExecutor.performBits();
    }

    @Override
    protected void internalStop() throws Exception {
        if (m_bitExecutor.isExecuting()) {
            m_bitExecutor.stopPerformBits();
        }
    }

    @Override
    protected void innerDispose() throws Exception {
        m_bitExecutor = null;
    }

    //endregion

    //region Nested Classes

    private class LocationBitResultHandler implements IHandler<BitResult> {


        @Override
        public void setInput(BitResult bitResult) {
            if (bitResult == null || bitResult.getBitResultStatus() == BitResultStatus.OK) {
                return;
            }
            NotificationManager.showShortToast(m_activity, "Please turn on location");
        }
    }

    private class BluetoothBitResultHandler implements IHandler<BitResult> {
        private final BluetoothAdapter m_bluetoothAdapter;

        BluetoothBitResultHandler(BluetoothAdapter bluetoothAdapter) {
            m_bluetoothAdapter = bluetoothAdapter;
        }

        @Override
        public void setInput(BitResult bitResult) {
            if (bitResult == null || bitResult.getBitResultStatus() == BitResultStatus.OK) {
                return;
            }
            m_bluetoothAdapter.enable();
            m_logger.info("Bluetooth enabled after receiving result:" + bitResult.toString());
            NotificationManager.showShortToast(m_activity, "Bluetooth enabled");
        }

    }

    private class BitResultToastHandler implements IHandler<BitResult> {

        @Override
        public void setInput(BitResult bitResult) {

            if (bitResult == null || bitResult.getBitResultStatus() == BitResultStatus.OK) {
                return;
            }
            String message = bitResult.getMessage();
            if (message != null && !message.isEmpty()) {
                NotificationManager.showShortToast(m_activity, message);
            }
        }
    }

    //endregion
}