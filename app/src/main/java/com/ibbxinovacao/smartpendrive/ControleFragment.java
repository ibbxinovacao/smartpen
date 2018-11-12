package com.ibbxinovacao.smartpendrive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by Francisco Filho on 17/10/2018.
 */

public class ControleFragment extends Fragment implements Button.OnClickListener {

    private static final String TAG = "Sageglass";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private static OutputStream mmOutputStreamControle;
    private static OutputStream bTAdapter;
    private InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    private static boolean btConnectedControle;
    private AlertDialog alerta;

    public static ControleFragment newInstance(OutputStream mmOutputStream, Boolean btConnected) {
        ControleFragment fragment = new ControleFragment();
        mmOutputStreamControle = mmOutputStream;
        btConnectedControle = btConnected;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.controle_fragment,
                container, false);
        Button down = (Button) view.findViewById(R.id.down);
        down.setOnClickListener(this);
        if(btConnectedControle){
            TextView status = (TextView) view.findViewById(R.id.connected);
            status.setText("Conectado");
        }else{
            TextView status = (TextView) view.findViewById(R.id.connected);
            status.setText("Não Conectado");
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down:
                Log.d(TAG, "clicou down");
                if(btConnectedControle){
                    Log.d(TAG, "Ta conectado");
                    try {
                        sendData("A");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "Não conectado");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("HC-06 não Conectado/Pareado");
                    //define a mensagem
                    builder.setMessage("Favor Conectar ou Parear o dispositivo HC-06");
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
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }
    }

    void sendData(String msg) throws IOException
    {
        mmOutputStreamControle.write(msg.getBytes());
        Log.d(TAG, "Msg Sended");
    }
}

