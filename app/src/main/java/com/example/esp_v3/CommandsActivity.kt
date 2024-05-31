package com.example.esp_v3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esp_v3.databinding.ActivityCommandsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.util.Objects

class CommandsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCommandsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommandsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityComm.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch{
                sleepTime()
            }
        }

        binding.silent.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch{
                silent()
            }
        }

        binding.chargeInfo.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch{
                checkChargeInfo()
            }
        }

    }


    private suspend fun sleepTime(){

        val bundle: Bundle? = intent.extras
        val address: String? = intent.getStringExtra("address")
        val port = intent.getStringExtra("port")!!.toInt()


        var connection =  Socket()

        try {
            withContext(Dispatchers.IO) {

                val timeToSleep = Integer.valueOf(binding.sleeptime.getText().toString()) //get sleeptime from EditText view
                val timeToAwake = Integer.valueOf(binding.awaketime.getText().toString()) //get awaketime from EditText view

                connection = Socket(address, port)

                val writer: OutputStream = connection.getOutputStream()

                writer.write("CYCL".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(2).array())
                writer.write("DSPL".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(timeToSleep).array())
                writer.write("AWKE".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(timeToAwake).array())

                runOnUiThread {
                    Toast.makeText(this@CommandsActivity, "'CYCL' command sent", Toast.LENGTH_SHORT).show();
                }

                writer.close()
                connection.close()
            }
        }
        catch (e: java.lang.NumberFormatException){
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter the values", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: NumberFormatException");
        }

        catch (e: SocketException) {

            connection.close()
            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Failed to connect to specified address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: SocketException");
        }
        catch (e: UnknownHostException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        }
        catch (e: IOException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: IOException");
        }

    }

    private suspend fun silent(){

        val bundle: Bundle? = intent.extras
        val address: String? = intent.getStringExtra("address")
        val port = intent.getStringExtra("port")!!.toInt()


        var connection =  Socket()

        try {
            withContext(Dispatchers.IO) {

                connection = Socket(address, port)

                val writer: OutputStream = connection.getOutputStream()

                writer.write("SLNT".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(0).array())

                runOnUiThread {
                    Toast.makeText(this@CommandsActivity, "Silent mode activated", Toast.LENGTH_SHORT).show();
                }

                writer.close()
                connection.close()
            }
        }
        catch (e: SocketException) {

            connection.close()
            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Failed to connect to specified address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: SocketException");
        }
        catch (e: UnknownHostException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        }
        catch (e: IOException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: IOException");
        }

    }


    private suspend fun checkChargeInfo(){

        val bundle: Bundle? = intent.extras
        val address: String? = intent.getStringExtra("address")
        val port = intent.getStringExtra("port")!!.toInt()


        var connection =  Socket()

        try {
            withContext(Dispatchers.IO) {

                connection = Socket(address, port)

                val writer: OutputStream = connection.getOutputStream()

                writer.write("INFO".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(1).array())
                writer.write("CHRG".toByteArray())

                val reader = connection.getInputStream()
                val response = reader.readBytes().toString(Charsets.UTF_8)

                runOnUiThread {
                    Toast.makeText(this@CommandsActivity, "Current charge status: $response%", Toast.LENGTH_SHORT).show();
                }

                writer.close()
                connection.close()
            }
        }
        catch (e: SocketException) {

            connection.close()
            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Failed to connect to specified address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: SocketException");
        }
        catch (e: UnknownHostException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        }
        catch (e: IOException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@CommandsActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: IOException");
        }

    }
}