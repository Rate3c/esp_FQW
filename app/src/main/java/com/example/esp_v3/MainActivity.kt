package com.example.esp_v3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.esp_v3.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val status:MutableLiveData<String> = MutableLiveData("")
    private var cntrX: Boolean = false
    private var cntrY: Boolean = false
    private var transpar: Boolean = false
    private var white: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.switchCntrX.setOnClickListener(View.OnClickListener {
            cntrX = binding.switchCntrX.isChecked()
        })

        binding.switchCntrY.setOnClickListener(View.OnClickListener {
            cntrY = binding.switchCntrY.isChecked()
        })

        binding.switchTransparent.setOnClickListener(View.OnClickListener {
            transpar = binding.switchTransparent.isChecked()
        })

        binding.switchColor.setOnClickListener(View.OnClickListener {
            white = binding.switchColor.isChecked()
        })

        binding.button.setOnClickListener{

            CoroutineScope(IO).launch{
                    client()
                }
        }

        binding.buttonSendText.setOnClickListener{
            CoroutineScope(IO).launch{
                sendText()
            }
        }

        status.observe(this, Observer {
                newValue ->
            binding.textView.text = newValue
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val myIntent: Intent = Intent(
                    this@MainActivity,
                    SettingsActivity::class.java
                )
                myIntent.putExtra("address", binding.setAddress.text.toString())
                myIntent.putExtra("port", binding.setPort.text.toString())
                this@MainActivity.startActivity(myIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun sendText(){

        status.postValue("IP NOT FOUND\n")
        var connection =  Socket()

        try {

                val address1 = binding.setAddress.getText().toString() //get address from EditText view
                val port1 = Integer.valueOf(binding.setPort.getText().toString()) //get port from EditText view
                val myText = binding.sendTextView.getText().toString() //get text to send from EditText view
                val posX = Integer.valueOf(binding.sendPosX.getText().toString()) //get posX from EditText view
                val posY = Integer.valueOf(binding.sendPosY.getText().toString()) //get posY from EditText view
                var args = 4

                if (transpar) args+=1
                if (white) args+=1

                val address = binding.setAddress.getText().toString() //get address from EditText view
                val port = Integer.valueOf(binding.setPort.getText().toString()) //get port from EditText view
                connection = Socket(address, port)

                val writer: OutputStream = connection.getOutputStream()

                writer.write("TEXT".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(args).array())
                writer.write("SIZE".toByteArray())
                writer.write(ByteBuffer.allocate(4).putInt(myText.length).array())
                writer.write("POSX".toByteArray())
                if (cntrX) writer.write("CENT".toByteArray())
                else writer.write(ByteBuffer.allocate(4).putInt(posX).array())
                writer.write("POSY".toByteArray())
                if (cntrY) writer.write("CENT".toByteArray())
                else writer.write(ByteBuffer.allocate(4).putInt(posY).array())
                if (transpar)  writer.write("TRSP".toByteArray())
                if (white)  writer.write("WTXT".toByteArray())
                writer.write("DATA".toByteArray())
                writer.write(myText.toByteArray())

                val reader = connection.getInputStream()
                val response = reader.readBytes().toString(Charsets.UTF_8)
                val post = "Text below sent to ESP32:\n$myText\nRESPONSE IS:\n$response"
                status.postValue(post)

                reader.close()
                writer.close()
                connection.close()
        }
        catch (e: SocketException) {

            connection.close()
            /*runOnUiThread {
                Toast.makeText(this@MainActivity, "Failed to connect to specified address", Toast.LENGTH_SHORT).show();
            }*/

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: SocketException");
        }
        catch (e: UnknownHostException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        }
        catch (e: NumberFormatException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        }
        catch (e: IOException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: IOException");
        }

    }
    private suspend fun client(){
        val address = binding.setAddress.getText().toString() //get address from EditText view
        status.postValue("IP NOT FOUND\n")

        var connection =  Socket()

        try {
            val myText = binding.sendTextView.getText().toString() //get text to send from EditText view
            val portStr = binding.setPort.getText().toString()    //get port from EditText view
            val port = Integer.valueOf(portStr)
            connection = Socket(address, port)

            val writer: OutputStream = connection.getOutputStream()
            writer.write("INFO".toByteArray())                            //command
            writer.write(ByteBuffer.allocate(4).putInt(0).array())                                          //# of arguments

            val reader = connection.getInputStream()
            val response = reader.readBytes().toString(Charsets.UTF_8)

            val firstRN = response.indexOf("\r\n")

            if (firstRN != -1) {
                val ESPstatus = response.substring(0, firstRN)

                if (ESPstatus == "OK") {
                    val secondRN = response.indexOf("\r\n", firstRN + 2)
                    val length = response.substring(firstRN + 2, secondRN).toInt()
                    val body = response.substring(secondRN + 2, secondRN + 2 + length)

                    status.postValue(body)

                }
            }

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Connection successful", Toast.LENGTH_SHORT).show();
            }

            reader.close()
            writer.close()
            connection.close()
        }
        catch (e: SocketException) {
            runOnUiThread {
                Toast.makeText(this@MainActivity, "Failed to connect to $address address", Toast.LENGTH_SHORT).show();
            }

            connection.close()
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: SocketException");
        }
        catch (e: NumberFormatException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: NumberFormatException");
        }
        catch (e: UnknownHostException) {

            runOnUiThread {
            Toast.makeText(this@MainActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
        }
            connection.close();
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        }
        catch (e: IOException) {

            runOnUiThread {
                Toast.makeText(this@MainActivity, "Enter correct IP address", Toast.LENGTH_SHORT).show();
            }

            connection.close()
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: IOException");
        }

    }

}
