package com.example.esp_v3;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import kotlin.text.Charsets;

public class ReachableDevices extends AppCompatActivity {

    private Button btnScan;
    private ProgressBar progressBar;
    private ListView listViewIp;

    public ArrayList<String> arrOfIps;
    ArrayList<String> ipList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reachable_devices);
        btnScan = (Button)findViewById(R.id.scanIP);
        listViewIp = (ListView)findViewById(R.id.listviewIP);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);


        ipList = new ArrayList();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ipList);
        listViewIp.setAdapter(adapter);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanIpTask().execute();
                progressBar.setVisibility(ProgressBar.VISIBLE);
            }

        });

    }

    private class ScanIpTask extends AsyncTask<Void, String, Void>{

        EditText lowerT = (EditText)findViewById(R.id.lower);
        EditText upperT = (EditText)findViewById(R.id.upper);
        EditText subnetT = (EditText)findViewById(R.id.subnet);
        EditText timeoutT = (EditText)findViewById(R.id.timeout);

        String lowerStr = lowerT.getText().toString();
        String upperStr = upperT.getText().toString();
        String subnet = subnetT.getText().toString();
        String timeoutStr = timeoutT.getText().toString();

        final int lower =  Integer.valueOf(lowerStr);
        final int upper =  Integer.valueOf(upperStr);
        final int timeout =  Integer.valueOf(timeoutStr);

        @Override
        protected void onPreExecute() {
            ipList.clear();
            adapter.notifyDataSetInvalidated();
            Toast.makeText(ReachableDevices.this, "Scanning IPs...", Toast.LENGTH_LONG).show();
        }


        @Override
        protected Void doInBackground(Void... params) {

            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //getting host device IP address
            final String myIpAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
            DhcpInfo dhcp = wifiManager.getDhcpInfo();
            //getting router IP address
            final String routerAddress = Formatter.formatIpAddress(dhcp.gateway);

            arrOfIps= new ArrayList<String>();
            int k = 0; //number of devices found

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;

                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(timeout) &
                            (!inetAddress.toString().substring(1).equals(myIpAddress)) &
                            (!inetAddress.toString().substring(1).equals(routerAddress)))
                    {

                        Socket connection = new Socket(inetAddress.toString().substring(1), 80);
                        OutputStream writer = connection.getOutputStream();

                        writer.write("INFO".getBytes());                         //command
                        writer.write(ByteBuffer.allocate(4).putInt(0).array());

                        byte[] arrayB = new byte[3];
                        arrayB[2] = 0;

                        InputStream reader = connection.getInputStream();
                        reader.read(arrayB, 0, 2);
                        String response = new String(arrayB, Charsets.UTF_8);

                        runOnUiThread(() -> {
                            final Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                            toast.show();
                        });

                        if ((arrayB[0] == ('O')) && (arrayB[1] == ('K'))) {
                                k += 1;
                                publishProgress(k + ".   " + "ESP32" + "   " + inetAddress.toString());
                                arrOfIps.add(inetAddress.toString());
                        }

                        connection.close();
                        reader.close();
                        writer.close();
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        public ArrayList getArrIps() {
            return (ArrayList)arrOfIps.clone();
        };


        @Override
        protected void onProgressUpdate(String... values) {
            ipList.add(values[0]);
            adapter.notifyDataSetInvalidated();
            Toast.makeText(ReachableDevices.this, values[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ReachableDevices.this, "Scanning is Done", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);

        }
    }

}

