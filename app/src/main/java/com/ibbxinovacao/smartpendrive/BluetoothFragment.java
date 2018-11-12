package com.ibbxinovacao.smartpendrive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.Set;
import android.content.IntentFilter;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.AdapterView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.app.ProgressDialog;


/**
 * Created by Francisco Filho on 17/10/2018.
 */

public class BluetoothFragment extends Fragment  implements AbsListView.OnItemClickListener {

    private static final String TAG = "Sageglass";

    private ArrayList <BluetoothDevice>deviceItemList;
    private static BluetoothAdapter bTAdapter;
    private ArrayAdapter<BluetoothDevice> mAdapter;
    private AlertDialog alerta;

    private Button scan;

    private AbsListView mListView;

    public static BluetoothFragment newInstance(BluetoothAdapter adapter) {
        BluetoothFragment fragment = new BluetoothFragment();
        bTAdapter = adapter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEVICELIST", "Super called for DeviceListFragment onCreate\n");
        deviceItemList = new ArrayList<BluetoothDevice>();

        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");
                deviceItemList.add(device);
            }
        }

        // If there are no devices, add an item that states so. It will be handled in the view.
        //if(deviceItemList.size() == 0) {
        //    deviceItemList.add(new BluetoothDevice("No Devices", "", "false"));
        //}

        Log.d("DEVICELIST", "DeviceList populated\n");
        Log.d("DEVICELIST", deviceItemList.toString());

        mAdapter = new DeviceListAdapter(getActivity(), deviceItemList, bTAdapter);

        Log.d("DEVICELIST", "Adapter created\n");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bluetooth_fragment,
                container, false);
        ToggleButton button = (ToggleButton) view.findViewById(R.id.scan);
        mListView = (AbsListView) view.findViewById(R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                if (isChecked) {
                    mAdapter.clear();
                    getActivity().registerReceiver(bReciever, filter);
                    bTAdapter.startDiscovery();
                } else {
                    getActivity().unregisterReceiver(bReciever);
                    bTAdapter.cancelDiscovery();
                }
            }
        });

        //mListView = (AbsListView) view.findViewById(android.R.id.list);
        return view;
    }

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                //DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                // Add it to our adapter
                mAdapter.add(device);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + deviceItemList.get(position).getName() + "\n");
        deviceItemList.get(position).createBond();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Dispositivo Sendo conectado");
        //define a mensagem
        builder.setMessage("Favor Aguardar e depois apertar OK");
        //define um botão como positivo
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(getActivity(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }
}