package com.example.esp_v3

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esp_v3.databinding.ActivitySendImageBinding
import com.example.esp_v3.databinding.SendKokBinding
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.Socket

class SendImage : AppCompatActivity() {

    private lateinit var binding: SendKokBinding
    //private lateinit var myimageView: ImageView
    lateinit var buttonIm:Button
    var handler = Handler()

    fun OnCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SendKokBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_send_image)
        /*myimageView = findViewById<View>(R.id.imageView) as ImageView
        buttonIm = findViewById<View>(R.id.buttonImage) as Button*/

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.buttonImage1.setOnClickListener{
            send()
        }


        /* Thread myThread = new Thread(new ServerImageThread());
        myThread.start();*/
    }

    fun send() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setType("image/*")
        startActivityForResult(i, 1)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    val imageUri = data!!.data
                    val imageStream = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    binding.imageView1.setImageBitmap(selectedImage)
                    val bos = ByteArrayOutputStream()
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 0, bos)
                    val array = bos.toByteArray()
                    val sic = SendImageClient()
                    sic.execute(array)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "No image selected", Toast.LENGTH_LONG).show()
            }
        }
    }

    inner class SendImageClient : AsyncTask<ByteArray?, Void?, Void?>() {
        override fun doInBackground(vararg voids: ByteArray?): Void? {
            try {
                val socket = Socket("192.168.0.1", 80)
                val out = socket.getOutputStream()
                val dos = DataOutputStream(out)
                dos.writeInt(voids[0]!!.size)
                dos.write(voids[0], 0, voids[0]!!.size)
                handler.post {
                    Toast.makeText(
                        applicationContext,
                        "image sent",
                        Toast.LENGTH_LONG
                    ).show()
                }
                dos.close()
                out.close()
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
                handler.post {
                    Toast.makeText(
                        applicationContext,
                        "IO exepction",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            return null
        }
    }
}
