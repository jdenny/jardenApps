package com.jardenconsulting.wifidirect;

/**
 * Created by john.denny@gmail.com on 07/01/2019.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.Collection;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver
        implements WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MyWiFiActivity mActivity;
    //!! private String serverAddress;

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Collection<WifiP2pDevice> devices = peers.getDeviceList();
        mActivity.showMessage("onPeersAvailable(); peers.size=" + devices.size());
        for (WifiP2pDevice device: devices) {
            mActivity.showMessage("address =" + device.deviceAddress +
                    "; name=" + device.deviceName + "; " + device.toString());
        }
        if (devices.size() > 0) {
            WifiP2pDevice device = devices.iterator().next();
            WifiP2pConfig config = new WifiP2pConfig();
            //!! serverAddress = device.deviceAddress;
            config.deviceAddress = device.deviceAddress;
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    mActivity.showMessage("connect success");
                    //!! mActivity.transferMessage(serverAddress);
                }

                @Override
                public void onFailure(int reason) {
                    mActivity.showMessage("connect failure; reason=" + reason);
                }
            });

        }
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MyWiFiActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.showMessage("wifi p2p enabled");
            } else {
                mActivity.showMessage("wifi p2p not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            mActivity.showMessage("wifi p2p peers changed");
            if (mManager == null) {
                mActivity.showMessage("mManager is null!");
            } else {
                mManager.requestPeers(mChannel, this);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            mActivity.showMessage("wifi p2p connection changed");
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, this);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            mActivity.showMessage("wifi p2p device changed");
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String host = info.groupOwnerAddress.getHostAddress();
        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
            new MyWiFiActivity.ServerAsyncTask(mActivity).execute();
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
            mActivity.transferMessage(host);
        }
    }
}
