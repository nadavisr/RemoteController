/*
 * Created by admin on 05/11/2017
 * Last modified 10:48 05/11/17
 */

package services.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.support.annotation.NonNull;

import com.example.admin.myapplication.notification.NotificationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

import static android.os.Looper.getMainLooper;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: services.wifi.</P>
 * <P></P>
 */

public class WifiDirectManager {

    //region Static

    @SuppressLint("StaticFieldLeak")
    private static WifiDirectManager s_instance = null;

    private static final ILog s_log = LogManager.getLogger();

    public static WifiDirectManager getInstance() {
        synchronized (WifiDirectManager.class) {
            return s_instance;
        }
    }

    public static void removeInstance() {
        synchronized (WifiDirectManager.class) {
            if (s_instance != null) {
                s_instance.dispose();
                s_instance = null;
            }
        }
    }

    public static WifiDirectManager createInstance(Context context) throws Exception {
        if (context == null) {
            String msg = "WifiManager.initialize received null context.";
            s_log.warning(msg);
            throw new NullPointerException(msg);
        }
        try {
            if (s_instance == null) {
                synchronized (WifiDirectManager.class) {
                    if (s_instance == null) {
                        s_instance = new WifiDirectManager(context);
                    }
                }
            }
        } catch (Exception ex) {
            s_instance = null;
            s_log.warning("Failed on try to create WifiManager instance");
            throw ex;
        }
        return s_instance;
    }


    //endregion

    //region Fields

    private Context m_context;

    private WifiP2pManager m_manager;

    private WifiP2pManager.Channel m_channel;

    private BroadcastReceiver m_broadcastReceiver;

    private List<WifiP2pDevice> m_peers;

    private final PeerListListener m_peerListListener = new WifiPeerListListener();

    //endregion

    //region Constructors

    private WifiDirectManager(Context srcContext) {
        m_context = srcContext;
        m_onWifiStateChangedListenersAggregator = new ArrayBlockingQueue<>(5);
        m_onWifiConnectionChangedListenersAggregator = new ArrayBlockingQueue<>(5);
        m_onThisDeviceDetailsChangedListenersAggregator = new ArrayBlockingQueue<>(5);
        m_onAvailablePeersChangedListenersAggregator = new ArrayBlockingQueue<>(5);
        initialize();
    }

    //endregion

    //region Methods

    //region Private Methods

    private void initialize() {

        m_manager = (WifiP2pManager) m_context.getSystemService(Context.WIFI_P2P_SERVICE);

        if (m_manager == null) {
            throw new RuntimeException("Could not get 'WIFI P2P Service'!");
        }

        m_channel = m_manager.initialize(m_context, getMainLooper(), null);

        IntentFilter intentFilter = new IntentFilter();

        // Indicates a change in the list of available m_peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

        m_broadcastReceiver = new WifiMangerBroadcastReceiver();

        m_context.registerReceiver(m_broadcastReceiver, intentFilter);

    }

    private void dispose() {
        if (m_context != null && m_broadcastReceiver != null) {
            m_context.unregisterReceiver(m_broadcastReceiver);
        }
        m_broadcastReceiver = null;
        m_context = null;

        m_manager.removeGroup(m_channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        m_onAvailablePeersChangedListenersAggregator.clear();
        m_onAvailablePeersChangedListenersAggregator = null;

        m_onThisDeviceDetailsChangedListenersAggregator.clear();
        m_onThisDeviceDetailsChangedListenersAggregator = null;

        m_onWifiConnectionChangedListenersAggregator.clear();
        m_onWifiConnectionChangedListenersAggregator = null;

        m_onWifiStateChangedListenersAggregator.clear();
        m_onWifiStateChangedListenersAggregator = null;


    }

    //endregion

    //region Public Methods

    public void discovering() {
        m_manager.discoverPeers(m_channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                m_discoveryStarted = true;
                NotificationManager.showShortToast(m_context, "Starting discovery..");
            }

            @Override
            public void onFailure(int reasonCode) {
                NotificationManager.showShortToast(m_context, "Discovering failed, reason: " + reasonCode);
                m_discoveryStarted = false;
            }
        });
    }

    public void stopDiscovering() {
        if (!m_discoveryStarted) {
            return;
        }
        m_manager.stopPeerDiscovery(m_channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reasonCode) {
                NotificationManager.showShortToast(m_context, "Stop discovering failed, reason: " + reasonCode);
            }
        });
    }

    public void connectToPeer(final String ip) throws NullPointerException {
        if (ip == null || ip.isEmpty()) {
            throw new NullPointerException("Received Null ot empty ip.");
        }
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = ip;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 4;
        m_manager.connect(m_channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                NotificationManager.showShortToast(m_context, "Connecting to IP:" + ip);
            }

            @Override
            public void onFailure(int reasonCode) {
                NotificationManager.showShortToast(m_context, "Connection failed to IP: " + ip + " , reason: " + reasonCode);
            }
        });
    }


    //endregion

    //endregion

    //region Events

    //region Events Aggregator

    Collection<IOnWifiStateChangedListener> m_onWifiStateChangedListenersAggregator;

    Collection<IOnWifiConnectionChangedListener> m_onWifiConnectionChangedListenersAggregator;

    Collection<IOnThisDeviceDetailsChangedListener> m_onThisDeviceDetailsChangedListenersAggregator;

    Collection<IOnAvailablePeersChangedListener> m_onAvailablePeersChangedListenersAggregator;

    //endregion

    //region Add Methods

    public void addOnWifiStateChangedListener(@NonNull IOnWifiStateChangedListener onWifiStateChangedListener) {
        boolean contains = m_onWifiStateChangedListenersAggregator.contains(onWifiStateChangedListener);
        if (!contains) {
            m_onWifiStateChangedListenersAggregator.add(onWifiStateChangedListener);
        }
    }

    public void addOnWifiConnectionChangedListener(@NonNull IOnWifiConnectionChangedListener onWifiConnectionChangedListener) {
        boolean contains = m_onWifiConnectionChangedListenersAggregator.contains(onWifiConnectionChangedListener);
        if (!contains) {
            m_onWifiConnectionChangedListenersAggregator.add(onWifiConnectionChangedListener);
        }
    }

    public void addOnThisDeviceDetailsChangedListener(@NonNull IOnThisDeviceDetailsChangedListener onThisDeviceDetailsChangedListener) {
        boolean contains = m_onThisDeviceDetailsChangedListenersAggregator.contains(onThisDeviceDetailsChangedListener);
        if (!contains) {
            m_onThisDeviceDetailsChangedListenersAggregator.add(onThisDeviceDetailsChangedListener);
        }
    }

    public void addOnAvailablePeersChangedListener(@NonNull IOnAvailablePeersChangedListener onAvailablePeersChangedListener) {
        boolean contains = m_onAvailablePeersChangedListenersAggregator.contains(onAvailablePeersChangedListener);
        if (!contains) {
            m_onAvailablePeersChangedListenersAggregator.add(onAvailablePeersChangedListener);
        }
    }

    //endregion

    //region Remove Methods

    public boolean removeOnWifiStateChangedListener(@NonNull IOnWifiStateChangedListener onWifiStateChangedListener) {
        return m_onWifiStateChangedListenersAggregator.remove(onWifiStateChangedListener);
    }

    public boolean removeOnWifiConnectionChangedListener(@NonNull IOnWifiConnectionChangedListener onWifiConnectionChangedListener) {
        return m_onWifiConnectionChangedListenersAggregator.remove(onWifiConnectionChangedListener);
    }

    public boolean removeOnThisDeviceDetailsChangedListener(@NonNull IOnThisDeviceDetailsChangedListener onThisDeviceDetailsChangedListener) {
        return m_onThisDeviceDetailsChangedListenersAggregator.remove(onThisDeviceDetailsChangedListener);
    }

    public boolean removeOnAvailablePeersChangedListener(@NonNull IOnAvailablePeersChangedListener onAvailablePeersChangedListener) {

        return m_onAvailablePeersChangedListenersAggregator.remove(onAvailablePeersChangedListener);
    }

    //endregion

    //endregion

    //region Nested Classes

    boolean m_discoveryStarted = false;

    private class WifiMangerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) {
                return;
            }

            switch (action) {

                //  Indicates a change in the Wi-Fi P2P status.
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if(wifiState== -1){
                        return;
                    }
                    if (wifiState == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                        NotificationManager.showShortToast(m_context, "Wifi P2P disabled!");
                    }

                    if (!m_onWifiStateChangedListenersAggregator.isEmpty()) {
                        IOnWifiStateChangedListener[] shallowArray =
                                new IOnWifiStateChangedListener[m_onWifiStateChangedListenersAggregator.size()];
                        shallowArray = m_onWifiStateChangedListenersAggregator.toArray(shallowArray);
                        for (IOnWifiStateChangedListener listener : shallowArray) {
                            if (wifiState == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                                listener.onWifiDisabled();
                            } else {
                                listener.onWifiEnabled();
                            }
                        }
                    }

                // Indicates a change in discovery status.
                case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                    if (m_discoveryStarted) {
                        int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
                        if (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                            NotificationManager.showShortToast(m_context, "Discovery closed.");
                            m_discoveryStarted = false;
                        }
                    }
                    break;

                // Indicates a change in the list of available m_peers.
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    if (m_manager != null) {
                        m_manager.requestPeers(m_channel, m_peerListListener);
                    }
                    break;

                // Indicates the state of Wi-Fi P2P connectivity has changed.
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:

                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                    WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);

                    WifiP2pGroup wifiP2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);

                    if (!m_onWifiConnectionChangedListenersAggregator.isEmpty()) {
                        IOnWifiConnectionChangedListener[] shallowArray =
                                new IOnWifiConnectionChangedListener[m_onWifiConnectionChangedListenersAggregator.size()];
                        shallowArray = m_onWifiConnectionChangedListenersAggregator.toArray(shallowArray);
                        for (IOnWifiConnectionChangedListener listener : shallowArray) {
                            listener.onWifiConnectionChanged(networkInfo, wifiP2pInfo, wifiP2pGroup);
                        }
                    }
                    break;

                // Indicates this device's details have changed.
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    if (!m_onThisDeviceDetailsChangedListenersAggregator.isEmpty()) {
                        WifiP2pDevice wifiDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                        IOnThisDeviceDetailsChangedListener[] shallowArray =
                                new IOnThisDeviceDetailsChangedListener[m_onThisDeviceDetailsChangedListenersAggregator.size()];
                        shallowArray = m_onThisDeviceDetailsChangedListenersAggregator.toArray(shallowArray);
                        for (IOnThisDeviceDetailsChangedListener listener : shallowArray) {
                            listener.onThisDeviceDetailsChanged(wifiDevice);
                        }
                    }
                    break;
            }
        }
    }

    private class WifiPeerListListener implements PeerListListener {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();

            List<WifiP2pDevice> shallowList = new ArrayList<>();

            if (refreshedPeers == null || refreshedPeers.isEmpty()) {
                m_peers = null;
                return;
            }

            if (m_peers != null && refreshedPeers.equals(m_peers)) {
                return;
            }

            if (m_peers == null) {
                m_peers = new ArrayList<>();
                m_peers.addAll(refreshedPeers);
                shallowList.addAll(refreshedPeers);
            }

            if (!m_onAvailablePeersChangedListenersAggregator.isEmpty()) {
                IOnAvailablePeersChangedListener[] shallowArray =
                        new IOnAvailablePeersChangedListener[m_onAvailablePeersChangedListenersAggregator.size()];
                shallowArray = m_onAvailablePeersChangedListenersAggregator.toArray(shallowArray);
                for (IOnAvailablePeersChangedListener listener : shallowArray) {
                    listener.onAvailablePeersChanged(shallowList);
                }
            }

        }
    }
    //endregion

    //region Interface

    public interface IOnWifiStateChangedListener {
        void onWifiEnabled();

        void onWifiDisabled();
    }

    public interface IOnWifiConnectionChangedListener {
        void onWifiConnectionChanged(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo, WifiP2pGroup wifiP2pGroup);

    }

    public interface IOnThisDeviceDetailsChangedListener {

        void onThisDeviceDetailsChanged(WifiP2pDevice deviceDetails);

    }

    public interface IOnAvailablePeersChangedListener {

        void onAvailablePeersChanged(List<WifiP2pDevice> availablePeers);
    }


//endregion
}
