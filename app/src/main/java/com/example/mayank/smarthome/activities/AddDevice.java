package com.example.mayank.smarthome.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mayank.smarthome.R;
import com.example.mayank.smarthome.model.DeviceData;
import com.example.mayank.smarthome.model.FileOperations;
import com.example.mayank.smarthome.model.MyDBHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class AddDevice extends Activity {

    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton toggleButton;
    private ListView listview;
    private ArrayAdapter adapter;
    private static final int ENABLE_BT_REQUEST_CODE = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;
    private UUID uuid = UUID.fromString("adfe7e60-4106-11e5-a151-feff819cdc9f");

    String StartBit = "[";
    String LengthofName = ""; //Length of name of smart board
    String Separator = "-";
    String BoardName = "";
    String ButtonNumber = "";
    String TimerState = "";
    String NumberofHours = "";
    String NumberofMinutes = "";
    String PinAction = "";
    String EndBit = "]";


    //BluetoothSocket bluetoothSocket;
    ConnectedThread c;

    //For custom font
    Typeface customfont;

    //SQL Database for adding device name and current timestamp
    MyDBHandler dbHandler;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Whenever a remote Bluetooth device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                adapter.add(bluetoothDevice.getName() + "\n"
                        + bluetoothDevice.getAddress());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_device);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.some_color));
        }

        dbHandler = new MyDBHandler(this.getApplicationContext());

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        customfont = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.ttf");


        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) listview.getItemAtPosition(position);
                String MAC = itemValue.substring(itemValue.length() - 17);
                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC);
                // Initiate a connection request in a separate thread
                ConnectingThread t = new ConnectingThread(bluetoothDevice);
                t.start();

                BoardName = bluetoothDevice.getName().toString();
                LengthofName = String.valueOf(BoardName.length());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                dbHandler.addDevice(new DeviceData(BoardName, currentDateTimeString));

            }
        });


    }

    public void onToggleClicked(View view) {

        adapter.clear();

        ToggleButton toggleButton = (ToggleButton) view;

        toggleButton.setTypeface(customfont);

        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Oop! Your device does not support Bluetooth",
                    Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);
        } else {

            if (toggleButton.isChecked()) { // to turn on bluetooth
                if (!bluetoothAdapter.isEnabled()) {
                    // A dialog will appear requesting user permission to enable Bluetooth
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Your device has already been enabled." +
                                    "\n" + "Scanning for remote Bluetooth devices...",
                            Toast.LENGTH_SHORT).show();
                    // To discover remote Bluetooth devices
                    discoverDevices();
                    // Make local device discoverable by other devices
                    makeDiscoverable();
                }
            } else { // Turn off bluetooth

                bluetoothAdapter.disable();
                adapter.clear();
                Toast.makeText(getApplicationContext(), "Your device is now disabled.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BT_REQUEST_CODE) {

            // Bluetooth successfully enabled!
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Ha! Bluetooth is now enabled." +
                                "\n" + "Scanning for remote Bluetooth devices...",
                        Toast.LENGTH_SHORT).show();

                // Make local device discoverable by other devices
                makeDiscoverable();

                // To discover remote Bluetooth devices
                discoverDevices();

                // Start a thread to create a  server socket to listen for connection request
                ListeningThread t = new ListeningThread();
                t.start();

            } else { // RESULT_CANCELED as user refused or failed to enable Bluetooth
                Toast.makeText(getApplicationContext(), "Bluetooth is not enabled.",
                        Toast.LENGTH_SHORT).show();

                // Turn off togglebutton
                toggleButton.setChecked(false);
            }
        } else if (requestCode == DISCOVERABLE_BT_REQUEST_CODE) {

            if (resultCode == DISCOVERABLE_DURATION) {
                Toast.makeText(getApplicationContext(), "Your device is now discoverable by other devices for " +
                                DISCOVERABLE_DURATION + " seconds",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fail to enable discoverability on your device.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void discoverDevices() {
        // To scan for remote Bluetooth devices
        if (bluetoothAdapter.startDiscovery()) {
            Toast.makeText(getApplicationContext(), "Discovering other bluetooth devices...",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Discovery failed to start.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void makeDiscoverable() {
        // Make local device discoverable
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

    private class ListeningThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;

        public ListeningThread() {
            BluetoothServerSocket temp = null;
            try {
                temp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), uuid);

            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothServerSocket = temp;
        }

        public void run() {
            BluetoothSocket bluetoothSocket;
            // This will block while listening until a BluetoothSocket is returned
            // or an exception occurs
            while (true) {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection is accepted
                if (bluetoothSocket != null) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "A connection has been accepted.",
                                    Toast.LENGTH_SHORT).show();
                            //stuff that updates ui
                            //switchui();
                            setContentView(R.layout.activity_bluetooth_instruction_receiver);
                            TextView t1 = (TextView) findViewById(R.id.instruction);
                            TextView t2 = (TextView) findViewById(R.id.instructionreceived);

                            t1.setTypeface(customfont);
                            t2.setTypeface(customfont);
                        }
                    });

                    // Manage the connection in a separate thread
                    startConnectedThread(bluetoothSocket);

                    try {
                        bluetoothServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Cancel the listening socket and terminate the thread
        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ConnectingThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectingThread(BluetoothDevice device) {

            BluetoothSocket temp = null;
            bluetoothDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothSocket = temp;
        }

        public void run() {
            // Cancel any discovery as it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // This will block until it succeeds in connecting to the device
                // through the bluetoothSocket or throws an exception
                bluetoothSocket.connect();

            } catch (IOException connectException) {
                connectException.printStackTrace();
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }

            // Code to manage the connection in a separate thread
        /*
            manageBluetoothConnection(bluetoothSocket);
        */
            final String filename = "LoggedData";

            runOnUiThread(new Runnable() {
                public void run() {


                    Toast.makeText(getApplicationContext(), "Successfully connected.",
                            Toast.LENGTH_SHORT).show();

                    //stuff that updates ui
                    //switchui();
                    setContentView(R.layout.activity_bluetooth_control);

                    ImageButton i1 = (ImageButton) findViewById(R.id.lightoff);
                    i1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "1";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "0";
                                TimerState = "0";

                                String Light_Off = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;


                                String LoggedData = BoardName + ButtonNumber + PinAction;


                                byte[] message = Light_Off.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                    ImageButton i2 = (ImageButton) findViewById(R.id.lighton);
                    i2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "1";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "1";
                                TimerState = "0";

                                String Light_On = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;

                                String LoggedData = BoardName + ButtonNumber + PinAction;

                                byte[] message = Light_On.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                    ImageButton i3 = (ImageButton) findViewById(R.id.fanon);
                    i3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "2";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "1";
                                TimerState = "0";

                                String Fan_On = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;

                                String LoggedData = BoardName + ButtonNumber + PinAction;

                                byte[] message = Fan_On.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    });
                    ImageButton i4 = (ImageButton) findViewById(R.id.fanoff);
                    i4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ButtonNumber = "2";
                            NumberofHours = "00";
                            NumberofMinutes = "00";
                            PinAction = "0";
                            TimerState = "0";
                            if (c != null) {

                                String Fan_Off = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;


                                String LoggedData = BoardName + ButtonNumber + PinAction;

                                byte[] message = Fan_Off.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                    });

                    ImageButton i5 = (ImageButton) findViewById(R.id.switch3on);
                    i5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "3";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "1";
                                TimerState = "0";

                                String Light_Off = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;


                                String LoggedData = BoardName + ButtonNumber + PinAction;


                                byte[] message = Light_Off.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                    ImageButton i6 = (ImageButton) findViewById(R.id.switch3off);
                    i6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "3";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "0";
                                TimerState = "0";

                                String Light_Off = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;


                                String LoggedData = BoardName + ButtonNumber + PinAction;


                                byte[] message = Light_Off.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
                    ImageButton i7 = (ImageButton) findViewById(R.id.switch4on);
                    i7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "4";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "1";
                                TimerState = "0";

                                String Light_Off = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;


                                String LoggedData = BoardName + ButtonNumber + PinAction;


                                byte[] message = Light_Off.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                    ImageButton i8 = (ImageButton) findViewById(R.id.switch4off);
                    i8.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (c != null) {

                                ButtonNumber = "4";
                                NumberofHours = "00";
                                NumberofMinutes = "00";
                                PinAction = "0";
                                TimerState = "0";

                                String Light_Off = StartBit + LengthofName + Separator + BoardName + Separator + ButtonNumber + Separator + TimerState
                                        + Separator + NumberofHours + Separator + NumberofMinutes + Separator + PinAction + EndBit;


                                String LoggedData = BoardName + ButtonNumber + PinAction;


                                byte[] message = Light_Off.getBytes();
                                c.write(message);

                                FileOperations fop = new FileOperations();
                                fop.write(filename, LoggedData);
                                if (fop.write(filename, LoggedData)) {
                                    Toast.makeText(getApplicationContext(), filename + ".txt created", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "I/O error", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });


                }
            });

            startConnectedThread(bluetoothSocket);


        }

        // Cancel an open connection and terminate the thread
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()


            // Keep listening to the InputStream until an exception occurs
            while (true)

            {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    final String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;
                    // Send the obtained bytes to the UI activity
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                    //      .sendToTarget();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final TextView t = (TextView) findViewById(R.id.instructionreceived);
                            t.setText(msgReceived);
                            t.setTypeface(customfont);
                        }
                    });
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Connection Lost!", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                }
            }
        }


        /* Call this from the main activity to send data to the remote device */

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    public void startConnectedThread(BluetoothSocket socket) {

        c = new ConnectedThread(socket);
        c.start();
    }

}
