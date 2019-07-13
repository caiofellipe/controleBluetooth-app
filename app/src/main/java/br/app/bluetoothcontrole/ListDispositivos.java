package br.app.bluetoothcontrole;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.Set;

public class ListDispositivos extends ListActivity {
    private BluetoothAdapter myBluetooth2 = null;
    static String MAC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myBluetooth2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> dispositivosPareados = myBluetooth2.getBondedDevices();
        if(dispositivosPareados.size() > 0){
            for(BluetoothDevice dispositivos : dispositivosPareados){
                String nomeBTH = dispositivos.getName();
                String macBTH = dispositivos.getAddress();
                ArrayBluetooth.add(nomeBTH + "\n" + macBTH);
            }
        }
        setListAdapter(ArrayBluetooth);
    }
}
