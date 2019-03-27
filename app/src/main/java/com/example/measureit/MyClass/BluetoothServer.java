package com.example.measureit.MyClass;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;

public class BluetoothServer extends Service {

    // IBinder for activities and services
    public final IBinder binder = new BLEBinder();
    // Bluetooth Definations
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice bluetoothDevice;
    public BluetoothSocket bluetoothSocket;
    public InputStream inputStream;
    public OutputStream outputStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //
    public String connectedDeviceAddress = null;
    public String connectedDeviceName = null;

    public class BLEBinder extends Binder {
        public BluetoothServer getService() {
            return BluetoothServer.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        if (!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBluetoothIntent);
        }
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent startDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startDiscoverable.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startDiscoverable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(startDiscoverable);
        }
        startDiscovery();
        super.onCreate();
    }

    public List<HashMap<String, Object>> getBondedList() {
        List<HashMap<String, Object>> bluetoothBondedList = new ArrayList<>();
        Set<BluetoothDevice> bondedDevices  = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                HashMap<String, Object> deviceItem = new HashMap<>();
                deviceItem.put("deviceName", device.getName()+"    ");
                deviceItem.put("deviceAddress", device.getAddress());
                bluetoothBondedList.add(deviceItem);
            }
        }
        return bluetoothBondedList;
    }

    public int connectDevice(String address) {
        if (address.equals(connectedDeviceAddress)) {
            return -2;
        }
        if (bluetoothAdapter == null) {
            // return false;
            return -1;
        }
        else {
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            if (bluetoothDevice != null) {
                stopDiscovery();
                // Use Thread.run() will block the main UI
                // .run() will return when the thread is over
                ConnectThread connectThread = new ConnectThread();
                connectedDeviceAddress = bluetoothDevice.getAddress();
                Toast.makeText(getApplicationContext(), "Attempting to Connect " + bluetoothDevice.getName(), Toast.LENGTH_SHORT)
                            .show();
                connectThread.start();
            }
        }
        return 1;
    }

    class ConnectThread extends Thread {
        @Override
        public void run() {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                bluetoothSocket = null;
            }
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.connect();
                } catch (IOException e) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e2) {
                        bluetoothSocket = null;
                    }
                }
                if (bluetoothSocket != null) {
                    try {
                        inputStream = bluetoothSocket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        inputStream = null;
                    }
                    try {
                        outputStream = bluetoothSocket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        outputStream = null;
                    }
                }
            }
        }
    }

    public void disconnectDevice() {
        connectedDeviceAddress = null;
        DisconnectThread disconnectThread = new DisconnectThread();
        disconnectThread.start();
    }

    class DisconnectThread extends Thread {
        @Override
        public void run() {
            if (bluetoothSocket != null && inputStream != null && outputStream != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    bluetoothSocket = null;
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    inputStream = null;
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    outputStream = null;
                }
            }
        }
    }

    public boolean getConnectionState() {
        return bluetoothSocket.isConnected();
    }

    public int getOneDistanceNumber() {
        int bytes;
        byte[] buf = new byte[1024];
        String hexResult = "00";
        int decResult;
        try {
            if ((bytes = inputStream.read(buf)) > 0) {
                hexResult = bytesToHex(buf, bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        decResult = Integer.parseInt(hexResult, 16);
        return decResult;
    }

    public static String bytesToHex(byte[] bytes, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < count; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                stringBuilder.append(0);
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    public void sendCommand(String command) {
        try {
            outputStream.write(command.getBytes(StandardCharsets.UTF_8));
            // outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDiscovery() {
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
    }

    public void stopDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
