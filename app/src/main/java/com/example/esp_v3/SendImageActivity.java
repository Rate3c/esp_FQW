package com.example.esp_v3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendImageActivity extends AppCompatActivity {

    private ImageView myimageView;
    Handler handler = new Handler();


    protected void OnCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_kok);
        myimageView = (ImageView) findViewById(R.id.imageView1);

       /* Thread myThread = new Thread(new ServerImageThread());
        myThread.start();*/
    }


    public void send(View v){
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    myimageView.setImageBitmap(selectedImage);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] array = bos.toByteArray();

                    SendImageClient sic = new SendImageClient();
                    sic.execute(array);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SendImageClient extends AsyncTask<byte[], Void, Void>{

        @Override
        protected Void doInBackground(byte[]... voids) {
            try {
                Socket socket = new Socket("192.168.0.1", 80);

                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(voids[0].length);
                dos.write(voids[0], 0, voids[0].length);
                handler.post(() ->{
                    Toast.makeText(getApplicationContext(), "image sent", Toast.LENGTH_LONG).show();
                });
                dos.close();
                out.close();
                socket.close();
            }catch (IOException e)
            {
                e.printStackTrace();
                handler.post(() ->{
                    Toast.makeText(getApplicationContext(), "IO exepction", Toast.LENGTH_LONG).show();
                });
            }

            return null;
        }
    }


}
