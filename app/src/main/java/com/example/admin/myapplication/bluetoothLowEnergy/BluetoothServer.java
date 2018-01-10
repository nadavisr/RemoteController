/*
 * Created by admin on 27/11/2017
 * Last modified 18:24 06/07/17
 */

package com.example.admin.myapplication.bluetoothLowEnergy;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;

import com.example.admin.myapplication.configurations.ConfigurationManager;
import com.example.admin.myapplication.configurations.POJO.BleConfiguration;
import com.example.admin.myapplication.notification.NotificationManager;

import java.util.ArrayList;
import java.util.List;

import services.common.ServicesBaseBgOperation;

import static android.content.Context.BLUETOOTH_SERVICE;

public class BluetoothServer extends ServicesBaseBgOperation {

    //region Fields

    private final Context m_context;

    private BluetoothBroadcastReceiver m_bluetoothBroadcastReceiver;

    private List<BluetoothDevice> m_devices;

    private BluetoothGattServer m_gattServer;

    private BluetoothManager m_bluetoothManager;

    private BluetoothAdapter m_bluetoothAdapter;

    private BluetoothLeAdvertiser m_bluetoothLeAdvertiser;

    private BleConfiguration m_bleConfiguration;

    private boolean m_lazyStart;

    private boolean m_advertising;

    private final AdvertiseCallback m_advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            m_logger.info("Peripheral advertising started.");
            NotificationManager.showShortToast(m_context, "Bluetooth Low Energy server peripheral advertising started.");
            m_advertising = true;
        }

        @Override
        public void onStartFailure(int errorCode) {
            if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED) {
                m_advertising = true;
                return;
            }
            m_logger.error("Peripheral advertising failed! error code " + errorCode);
            NotificationManager.showShortToast(m_context, "Bluetooth Low Energy server peripheral advertising failed!");
            m_advertising = false;
        }
    };

    //endregion

    //region Constructors

    public BluetoothServer(Context context, String id) {
        super(id);
        m_context = context;
        m_advertising = m_lazyStart = false;
    }

    public BluetoothServer(Context context) {
        this(context, "");
    }

    //endregion

    //region Methods

    //region Private Methods

    private void setupGattServer() {
        if (m_bluetoothLeAdvertiser == null) {
            m_bluetoothLeAdvertiser = m_bluetoothAdapter.getBluetoothLeAdvertiser();
        }

        if (m_gattServer == null) {
            GattServerCallback gattServerCallback = new GattServerCallback();
            m_gattServer = m_bluetoothManager.openGattServer(m_context, gattServerCallback);
        }

        BluetoothGattService service = new BluetoothGattService(m_bleConfiguration.getServiceUuid(),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Write characteristic
        BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(m_bleConfiguration.getCharacteristicEchoUuid(),
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);

        service.addCharacteristic(writeCharacteristic);

        m_gattServer.addService(service);

        m_lazyStart = false;
    }

    private void stopGattServer() {
        if (m_gattServer != null) {
            m_gattServer.close();
        }
    }

    //endregion

    //region Public Methods

    public void startAdvertising() {

        if (m_lazyStart) {
            m_advertising = true;
            m_logger.info("The BLE server in lazy loading, so the advertising will start after the loading finished.");
            return;
        }

        if (m_bluetoothLeAdvertiser == null) {
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder().setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .build();

        ParcelUuid parcelUuid = new ParcelUuid(m_bleConfiguration.getServiceUuid());
        AdvertiseData data = new AdvertiseData.Builder().setIncludeDeviceName(true)
                .addServiceUuid(parcelUuid)
                .build();

        m_bluetoothLeAdvertiser.startAdvertising(settings, data, m_advertiseCallback);
    }

    public void stopAdvertising() {
        if (m_bluetoothLeAdvertiser != null) {
            m_bluetoothLeAdvertiser.stopAdvertising(m_advertiseCallback);
            m_advertising = false;
        }
    }

    public void sendResponse(BluetoothDevice device, int requestId, int status, int offset, byte[] value) {
        m_gattServer.sendResponse(device, requestId, status, 0, null);
    }

    //endregion

    //region ServicesBaseBgOperation Implementation
    @Override
    protected void internalInitialize() throws Exception {
        if (m_context == null) {
            throw new NullPointerException("The context that received in the constructor is null!");
        }

        m_bleConfiguration = ConfigurationManager.getBleConfiguration();
        if (m_bleConfiguration == null) {
            throw new Exception("Getting Bluetooth Low Energy as null configuration.");
        }

        m_bluetoothManager = (BluetoothManager) m_context.getSystemService(BLUETOOTH_SERVICE);
        if (m_bluetoothManager == null) {
            throw new Exception("Could not get bluetooth manager from context.");
        }

        m_bluetoothAdapter = m_bluetoothManager.getAdapter();
        if (m_bluetoothAdapter == null) {
            throw new Exception("Could not  get bluetooth manager from Bluetooth Manager.");
        }

        @SuppressLint("HardwareIds")
        String deviceInfo = "Bluetooth Device Info: Name = " + m_bluetoothAdapter.getName() + ", Address = " + m_bluetoothAdapter.getAddress();
        m_logger.info(deviceInfo);

        m_devices = new ArrayList<>();

        m_bluetoothLeAdvertiser = m_bluetoothAdapter.getBluetoothLeAdvertiser();
        GattServerCallback gattServerCallback = new GattServerCallback();
        m_gattServer = m_bluetoothManager.openGattServer(m_context, gattServerCallback);

        final IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        m_bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
        m_context.registerReceiver(m_bluetoothBroadcastReceiver, intentFilter);
    }

    @Override
    protected void internalStart() throws Exception {
        // Check if bluetooth is enabled
        if (!m_bluetoothAdapter.isEnabled()) {
            NotificationManager.showShortToast(m_context, "The bluetooth is disabled! Please keep the bluetooth always enabled.");
            m_logger.info("Could not start the Bluetooth server due to the bluetooth is disabled, lazy start enabled");
            m_lazyStart = true;
            return;
        }

        setupGattServer();
    }

    @Override
    protected void internalStop() throws Exception {
        if (m_advertising) {
            stopAdvertising();
        }
        stopGattServer();
    }

    @Override
    protected void innerDispose() throws Exception {
        if (m_bluetoothBroadcastReceiver != null) {
            m_context.unregisterReceiver(m_bluetoothBroadcastReceiver);
        }

        m_bleConfiguration = null;

        if (m_devices != null) {
            m_devices.clear();
            m_devices = null;
        }
        m_gattServer = null;
        m_bluetoothManager = null;
        m_bluetoothAdapter = null;
        m_bluetoothLeAdvertiser = null;
        m_bleConfiguration = null;

    }

    //endregion

    //endregion

    // region Nested Classes

    private class GattServerCallback extends BluetoothGattServerCallback {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            m_logger.verbose("in BluetoothGattServerCallback.onConnectionStateChange");

            super.onConnectionStateChange(device, status, newState);

            m_logger.info("Gatt server: device connection state changed. The device:" +
                    "name = " + device.getName() +
                    ", address = " + device.getAddress() +
                    ", status = " + status +
                    ", newState = " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                m_devices.add(device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                m_devices.remove(device);
            }
        }

        // The Gatt will reject Characteristic Read requests that do not have the permission set,
        // so there is no need to check inside the callback
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device,
                                                int requestId,
                                                int offset,
                                                BluetoothGattCharacteristic characteristic) {
            m_logger.verbose("in BluetoothGattServerCallback.onCharacteristicReadRequest");

            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            if (BluetoothUtils.requiresResponse(characteristic)) {
                // Unknown read characteristic requiring response, send failure
                sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
            }
            // Not one of our characteristics or has NO_RESPONSE property set
        }

        // The Gatt will reject Characteristic Write requests that do not have the permission set,
        // so there is no need to check inside the callback
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device,
                                                 int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite,
                                                 boolean responseNeeded,
                                                 int offset,
                                                 byte[] value) {
            m_logger.verbose("in BluetoothGattServerCallback.onCharacteristicWriteRequest");

            super.onCharacteristicWriteRequest(device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value);


            if (m_bleConfiguration.getCharacteristicEchoUuid().equals(characteristic.getUuid())) {
                sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            m_logger.verbose("in BluetoothGattServerCallback.onNotificationSent");

            super.onNotificationSent(device, status);
        }
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (intExtra) {
                    case BluetoothAdapter.STATE_OFF:
                        m_logger.warning("Bluetooth turned off! the BLE server will back to same state when the bluetooth will turned on.");
                        NotificationManager.showShortToast(m_context, "The bluetooth turned off!");
                        stopAdvertising();

                        break;
                    case BluetoothAdapter.STATE_ON:
                        m_logger.info("The bluetooth turned on.");
                        if (m_lazyStart) {
                            setupGattServer();
                            m_lazyStart = false;
                            m_logger.info("The BLE server restarted alone because lazy start or recovering.");
                        }
                        if (m_advertising) {
                            m_logger.info("The BLE server starting advertising alone because lazy start or recovering.");
                            startAdvertising();
                        }
                        break;
                }

            }

        }

    }

    //endregion
}
