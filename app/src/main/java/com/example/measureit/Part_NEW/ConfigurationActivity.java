package com.example.measureit.Part_NEW;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.measureit.MainActivity;
import com.example.measureit.MyClass.BluetoothServer;
import com.example.measureit.MyClass.ConfigurationSaver;
import com.example.measureit.Part_NEW.MySimpleAdapter.BluetoothListAdapter;
import com.example.measureit.Part_NEW.MySimpleAdapter.ConfigurationListAdapter;
import com.example.measureit.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

public class ConfigurationActivity extends AppCompatActivity {

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    // Definition about bluetooth
    private BluetoothAdapter bluetoothAdapter = null;
    private BroadcastReceiver bluetoothReceiver;
    private List<String> newDevicesList = new ArrayList<>();
    private List<String> newDevicesAddress = new ArrayList<>();
    // Defination of Buttons
    public Button nextButton;
    public Button editButton;
    public Button newdeviceButton;
    public Button backHomeButton;
    // Defination of Dialogs
    public AlertDialog nextAlertDialog1;
    public AlertDialog nextAlertDialog2;
    public AlertDialog newDevicesDialog;
    public AlertDialog configListDialog;
    public AlertDialog deleteDialog;
    public AlertDialog confirmDialog;
    public AlertDialog disconnectDialog;
    //
    public BluetoothServer bluetoothServer;
    public BluetoothServer.BLEBinder bleBinder;
    public ConfigurationSaver configurationSaver;
    //
    public Timer timer;
    // Some flags
    public String selectedConfiguration = null;
    public String selectedBluetoothDevice = null;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleBinder = (BluetoothServer.BLEBinder) service;
            bluetoothServer = bleBinder.getService();
            showPairedList();
            configurationList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothServer = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        nextButton = findViewById(R.id.button_next);
        editButton = findViewById(R.id.button_editconfiguration);
        newdeviceButton = findViewById(R.id.button_newdevice);
        backHomeButton = findViewById(R.id.backHomepage);
        //
        configurationSaver = new ConfigurationSaver();
        bindService(new Intent(this, BluetoothServer.class), serviceConnection, Context.BIND_AUTO_CREATE);
        //
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedConfiguration == null) {
                    final AlertDialog.Builder nextAlertBuilder1 = new AlertDialog.Builder(ConfigurationActivity.this)
                            .setCancelable(false)
                            .setTitle("Configuration Not Selected")
                            .setMessage("A configuation must be seleted before starting a measuring!")
                            .setIcon(R.drawable.icon_alert)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    nextAlertDialog1.dismiss();
                                }
                            })
                            .setNegativeButton("Why?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    nextAlertDialog1 = nextAlertBuilder1.create();
                    nextAlertDialog1.setCanceledOnTouchOutside(false);
                    nextAlertBuilder1.show();
                } else {
                    configurationSaver.configurationSaverInit(getApplicationContext(), true, selectedConfiguration);
                    if (!configurationSaver.getBooleanParams("randomData") && !bluetoothServer.getConnectionState()) {
                        final AlertDialog.Builder nextAlertBuilder2 = new AlertDialog.Builder(ConfigurationActivity.this)
                                .setCancelable(false)
                                .setTitle("Device Not Established!")
                                .setMessage("A device must be seleted before starting a measuring! Or select random data mode!")
                                .setIcon(R.drawable.icon_alert)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        nextAlertDialog2.dismiss();
                                    }
                                });
                        nextAlertDialog2 = nextAlertBuilder2.create();
                        nextAlertDialog2.setCanceledOnTouchOutside(false);
                        nextAlertDialog2.show();
                    } else {
                        Intent nextIntent = new Intent(ConfigurationActivity.this, ScannerActivity.class);
                        nextIntent.putExtra("selectedConfigurationName", selectedConfiguration);
                        nextIntent.putExtra("selectedBluetoothDeviceName", selectedBluetoothDevice);
                        startActivity(nextIntent);
                    }
                }
            }
        });
        bluetoothStartReceive();
        newdeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewDeviceList();
            }
        });
        backHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(serviceConnection);
                startActivity(new Intent(ConfigurationActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_MUTE: return true;
            default: return false;
        }
    }

    public void showPairedList(){
        final ListView deviceListView = findViewById(R.id.bluetoothlist);
        final List<HashMap<String, Object>> listItems = bluetoothServer.getBondedList();
        final BluetoothListAdapter connectedAdapter = new BluetoothListAdapter(ConfigurationActivity.this, listItems,
                R.layout.bluetoothdevicelist_item,
                new String[] {"deviceName", "deviceAddress"},
                new int[] {R.id.devicename, R.id.deviceaddress});
        final SimpleAdapter otherAdapter = new SimpleAdapter(ConfigurationActivity.this, listItems,
                R.layout.bluetoothdevicelist_item,
                new String[] {"deviceName", "deviceAddress"},
                new int[] {R.id.devicename, R.id.deviceaddress});
        deviceListView.setAdapter(otherAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int connectionState = bluetoothServer.connectDevice(String.valueOf(listItems.get(position).get("deviceAddress")));
                switch (connectionState) {
                    case -2:
                        AlertDialog.Builder disconnectBuilder = new AlertDialog.Builder(ConfigurationActivity.this)
                                .setTitle("This device is connected!")
                                .setMessage("Do you want to disconnect it?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bluetoothServer.disconnectDevice();
                                        deviceListView.setAdapter(otherAdapter);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        disconnectDialog.dismiss();
                                    }
                                });
                        disconnectDialog = disconnectBuilder.create();
                        disconnectDialog.setCanceledOnTouchOutside(false);
                        disconnectDialog.show();
                        break;
                    case -1:
                        Toast.makeText(ConfigurationActivity.this, "Something went wrong, try again later", Toast.LENGTH_SHORT)
                                .show();
                    case 1:
                        HashMap<String, Object> firstOne = listItems.get(0);
                        HashMap<String, Object> chosenOne = listItems.get(position);
                        listItems.set(0, chosenOne);
                        listItems.set(position, firstOne);
                        otherAdapter.notifyDataSetChanged();
                        deviceListView.setAdapter(connectedAdapter);
                }
            }
        });
    }

    private void bluetoothStartReceive(){
        bluetoothAdapter.startDiscovery();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice devices = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    newDevicesList.add(devices.getName()+" @ "+devices.getAddress());
                    newDevicesAddress.add(devices.getAddress());
                }
                else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                    BluetoothDevice devices = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (devices.getBondState()){
                        case BluetoothDevice.BOND_NONE:
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Toast.makeText(ConfigurationActivity.this, "Attempting to pair "+devices.getName(), Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            showPairedList();
                            Toast.makeText(ConfigurationActivity.this, "Successfully pair "+devices.getName(), Toast.LENGTH_SHORT)
                                    .show();
                            break;
                            default: break;
                    }
                }
                else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                    BluetoothDevice devices = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Toast.makeText(ConfigurationActivity.this, "Connection to "+devices.getName()+" Successfully!", Toast.LENGTH_SHORT)
                            .show();
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                    BluetoothDevice devices = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Toast.makeText(ConfigurationActivity.this, "Connection to "+devices.getName()+" Broken!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };
        registerReceiver(bluetoothReceiver, intentFilter);
    }

    private void showNewDeviceList(){
        String[] newDevicesArray = new String[newDevicesList.size()];
        newDevicesList.toArray(newDevicesArray);
        AlertDialog.Builder newDeviceListBuilder = new AlertDialog.Builder(ConfigurationActivity.this)
                .setCancelable(false)
                .setTitle("Founded Devices...")
                .setItems(newDevicesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pairDevice(newDevicesAddress.get(which))) {
                            newDevicesDialog.dismiss();
                            showPairedList();
                        }
                    }
                })
                .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newDevicesDialog.dismiss();
                        showNewDeviceList();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bluetoothAdapter.cancelDiscovery();
                        newDevicesDialog.dismiss();
                    }
                });
        newDevicesDialog = newDeviceListBuilder.create();
        newDevicesDialog.setCanceledOnTouchOutside(false);
        newDevicesDialog.show();
    }

    private boolean pairDevice(String toPairDevice){
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        BluetoothDevice toDevice = bluetoothAdapter.getRemoteDevice(toPairDevice);
        try {
            Method createBond = BluetoothDevice.class.getMethod("createBond");
            return (Boolean) createBond.invoke(toDevice);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void configurationList(){
        final ListView configurationListView = findViewById(R.id.configurationlist);
        // Use a ArrayList to save String-Object Map
        final List<HashMap<String, Object>> configurationItems = new ArrayList<>();
        configurationSaver.configurationSaverInit(getApplicationContext(), false, null);
        final String[] names = configurationSaver.getConfigurationNameList();
        final String[] times = configurationSaver.getConfigurationTimeList();
        // Use Calendar to get time
        // After all, time of modifying configuration should be read from saved data
        // This is for the test
        /*
        Calendar calendar = Calendar.getInstance();
        String testtime = DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()).toString();
        String[] times = new String[]{testtime, testtime, testtime, testtime};
        */
        for (int i = 0; i < names.length; i++){
            HashMap<String, Object> configurationItem = new HashMap<>();
            configurationItem.put("name", "Name: "+names[i]);
            configurationItem.put("time", "Modified on: "+times[i]);
            configurationItems.add(configurationItem);
        }
        final ConfigurationListAdapter configurationAdapter = new ConfigurationListAdapter(this, configurationItems,
                R.layout.configurationlist_item,
                new String[]{"name", "time"},
                new int[]{R.id.configurationname, R.id.configurationtime});
        final SimpleAdapter noChosenConfigurationAdapter = new SimpleAdapter(this, configurationItems,
                R.layout.configurationlist_item,
                new String[]{"name", "time"},
                new int[]{R.id.configurationname, R.id.configurationtime});
        configurationListView.setAdapter(noChosenConfigurationAdapter);
        configurationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> firstOne = configurationItems.get(0);
                HashMap<String, Object> chosenOne = configurationItems.get(position);
                selectedConfiguration = names[position];
                configurationItems.set(0, chosenOne);
                configurationItems.set(position, firstOne);
                // configurationAdapter.notifyDataSetChanged();
                configurationListView.setAdapter(configurationAdapter);
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent editionIntent = new Intent(ConfigurationActivity.this, ConfigurationEdition.class);
                final AlertDialog.Builder configListBuilder = new AlertDialog.Builder(v.getContext())
                        .setTitle("Edit One or Add a New")
                        .setIcon(R.drawable.configuration_icon)
                        .setCancelable(false)
                        .setItems(names, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selection = names[which];
                                editionIntent.putExtra("configurationName", selection);
                                editionIntent.putExtra("ifNew", false);
                                startActivity(editionIntent);
                                // finish();
                            }
                        })
                        .setPositiveButton("Add a New", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final AlertDialog addNewDialog = new AlertDialog.Builder(ConfigurationActivity.this).create();
                                addNewDialog.setView(LayoutInflater.from(ConfigurationActivity.this).inflate(R.layout.configuration_addalert, null));
                                addNewDialog.show();
                                addNewDialog.getWindow().setContentView(R.layout.configuration_addalert);
                                Button confirmButton = addNewDialog.findViewById(R.id.confirmButton);
                                final EditText inputText = addNewDialog.findViewById(R.id.nameText);
                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v2) {
                                        String strInput = inputText.getText().toString();
                                        if (isNullEmptyBlank(strInput)){
                                            inputText.setError("Input Text Cannot be Null!");
                                        }
                                        else {
                                            addNewDialog.dismiss();
                                            editionIntent.putExtra("ifNew", true);
                                            editionIntent.putExtra("configurationName", strInput);
                                            startActivity(editionIntent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                configListDialog.dismiss();
                            }
                        })
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                configListDialog.dismiss();
                                final AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(v.getContext())
                                        .setCancelable(false)
                                        .setTitle("Select One to Delete")
                                        .setIcon(R.drawable.icon_alert)
                                        .setItems(names, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, final int which) {
                                                final AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(v.getContext())
                                                        .setCancelable(false)
                                                        .setTitle("Confirm Deletion?")
                                                        .setMessage("Are you sure to delete "+names[which])
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which2) {
                                                                configurationSaver.delSaver(names[which]);
                                                                confirmDialog.dismiss();
                                                                configurationList();
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which2) {
                                                                confirmDialog.dismiss();
                                                            }
                                                        });
                                                confirmDialog = confirmDialogBuilder.create();
                                                confirmDialog.setCanceledOnTouchOutside(false);
                                                confirmDialog.show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteDialog.dismiss();
                                            }
                                        });
                                deleteDialog = deleteDialogBuilder.create();
                                deleteDialog.setCanceledOnTouchOutside(false);
                                deleteDialog.show();
                            }
                        });
                configListDialog = configListBuilder.create();
                configListDialog.setCanceledOnTouchOutside(false);
                configListDialog.show();
            }
        });
    }

    private boolean isNullEmptyBlank(String str){
        if (str == null || "".equals(str) || "".equals(str.trim())){
            return true;
        }
        return false;
    }

    protected void onDestroy(){
        super.onDestroy();
        // timer.cancel();
        if (bluetoothReceiver != null) {
            unregisterReceiver(bluetoothReceiver);
        }
        unbindService(serviceConnection);
    }
}
