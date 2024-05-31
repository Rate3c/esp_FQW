package com.example.esp_v3

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.esp_v3.databinding.ActivityTextBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.ByteBuffer

class SendTextActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTextBinding
    private val status: MutableLiveData<String> = MutableLiveData("")
    private var cntrX: Boolean = false
    private var cntrY: Boolean = false
    private var color: String = "Black"
    private var transpar: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.whiteButton.setOnClickListener(View.OnClickListener {
            color = "White"
            binding.displayColor.text = "White text"
            binding.displayColor.setTextColor(Color.parseColor("#ffffff"))
        })

        binding.blackButton.setOnClickListener(View.OnClickListener {
            color = "Black"
            binding.displayColor.text = "Black text"
            binding.displayColor.setTextColor(Color.parseColor("#000000"))
        })

        binding.redButton.setOnClickListener(View.OnClickListener {
            color = "Red"
            binding.displayColor.text = "Red text"
            binding.displayColor.setTextColor(Color.parseColor("#ff0000"))
        })

        binding.blueButton.setOnClickListener(View.OnClickListener {
            color = "Blue"
            binding.displayColor.text = "Blue text"
            binding.displayColor.setTextColor(Color.parseColor("#0004e8"))
        })

        binding.orangeButton.setOnClickListener(View.OnClickListener {
            color = "Orange"
            binding.displayColor.text = "Orange text"
            binding.displayColor.setTextColor(Color.parseColor("#ff9100"))
        })

        binding.yellowButton.setOnClickListener(View.OnClickListener {
            color = "Yellow"
            binding.displayColor.text = "Yellow text"
            binding.displayColor.setTextColor(Color.parseColor("#ffee00"))
        })

        binding.greenButton.setOnClickListener(View.OnClickListener {
            color = "Green"
            binding.displayColor.text = "Green text"
            binding.displayColor.setTextColor(Color.parseColor("#04f200"))
        })

        binding.buttonSendText.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                sendText()
            }
        }

        binding.switchCntrX.setOnClickListener(View.OnClickListener {
            cntrX = binding.switchCntrX.isChecked()
        })

        binding.switchCntrY.setOnClickListener(View.OnClickListener {
            cntrY = binding.switchCntrY.isChecked()
        })

        binding.switchTransparent.setOnClickListener(View.OnClickListener {
            transpar = binding.switchTransparent.isChecked()
        })

        status.observe(this, Observer { newValue ->
            binding.textViewText.text = newValue
        })
    }


    private suspend fun sendText() {

        val bundle: Bundle? = intent.extras
        val address: String? = intent.getStringExtra("address")
        val port = intent.getStringExtra("port")!!.toInt()

        var connection = Socket()

        try {

            val myText =
                binding.sendTextView.getText().toString() //get text to send from EditText view
            val posX =
                Integer.valueOf(binding.sendPosX.getText().toString()) //get posX from EditText view
            val posY =
                Integer.valueOf(binding.sendPosY.getText().toString()) //get posY from EditText view
            var args = 5

            if (transpar) args += 1

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
            if (transpar) writer.write("TRSP".toByteArray())
            writer.write("TCLR".toByteArray())

            when(color) {
                "Black" ->  writer.write("BLCK".toByteArray())
                "White" ->  writer.write("WHTE".toByteArray())
                "Red" ->  writer.write("XRED".toByteArray())
                "Blue" ->  writer.write("BLUE".toByteArray())
                "Yellow" ->  writer.write("YELW".toByteArray())
                "Orange" ->  writer.write("ORNG".toByteArray())
                "Green" ->  writer.write("GREN".toByteArray())
            }

            writer.write("DATA".toByteArray())
            writer.write(myText.toByteArray())

            val reader = connection.getInputStream()
            val response = reader.readBytes().toString(Charsets.UTF_8)
            val post = "Text below sent to ESP32:\n$myText\nRESPONSE IS:\n$response"
            status.postValue(post)

            Thread.sleep(2_000)

            reader.close()
            writer.close()
            connection.close()
        } catch (e: SocketException) {

            connection.close()
            /*runOnUiThread {
                Toast.makeText(this@MainActivity, "Failed to connect to specified address", Toast.LENGTH_SHORT).show();
            }*/

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: SocketException");
        } catch (e: UnknownHostException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@SendTextActivity, "Enter correct IP address", Toast.LENGTH_SHORT)
                    .show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        } catch (e: NumberFormatException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@SendTextActivity, "Enter correct IP address", Toast.LENGTH_SHORT)
                    .show();
            }
            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: UnknownHostException");
        } catch (e: IOException) {
            connection.close();

            runOnUiThread {
                Toast.makeText(this@SendTextActivity, "Enter correct IP address", Toast.LENGTH_SHORT)
                    .show();
            }

            e.printStackTrace();
            Log.e("AsyncTask", "Background Task: IOException");
        }

    }
}