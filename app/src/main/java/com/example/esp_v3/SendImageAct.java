package com.example.esp_v3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.esp_v3.databinding.ActivityMainBinding;
import com.example.esp_v3.databinding.SendKokBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SendImageAct extends AppCompatActivity {
    Handler handler = new Handler();
    private SendKokBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SendKokBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(1);
            }
        });

        binding.buttonImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(2);
            }
        });
    }

    public void send(int req){
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(i, req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                try {
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    binding.imageView1.setImageBitmap(selectedImage);

                    //

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] array = bos.toByteArray();

                    /*int size = selectedImage.getRowBytes() * selectedImage.getHeight();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                    selectedImage.copyPixelsToBuffer(byteBuffer);
                    byte[] array1 = byteBuffer.array();*/

                    SendImageAct.SendImageClient sic = new SendImageAct.SendImageClient();
                    sic.execute(array);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    debugging("Smth went wrong");
                }
            }
            else {
                debugging("No image selected");
            }
        }
        if (requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                try {
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    binding.imageView1.setImageBitmap(selectedImage);

                    int size = selectedImage.getRowBytes() * selectedImage.getHeight();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                    selectedImage.copyPixelsToBuffer(byteBuffer);
                    byte[] array = byteBuffer.array();

                    SendImageAct.SendImageClient sic = new SendImageAct.SendImageClient();
                    sic.execute(array);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    debugging("Smth went wrong");
                }
            }
            else {
                debugging("No image selected");
            }
        }
    }

    public void debugging(String mess){
        runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
    public class SendImageClient extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... voids) {

            Socket connection = new Socket();

            try {

                String address = binding.setAddressIm.getText().toString(); //get address from EditText view
                String portStr = binding.setPortIm.getText().toString();    //get port from EditText view
                int port = Integer.parseInt(portStr);

                connection = new Socket(address, port);

                OutputStream out = connection.getOutputStream();
                DataOutputStream writer = new DataOutputStream(out);

                byte[] output = voids[0];

                writer.write("IMGE".getBytes());
                writer.write(ByteBuffer.allocate(4).putInt(4).array());
                writer.write("SIZE".getBytes());
                writer.write(ByteBuffer.allocate(4).putInt(output.length).array());
                writer.write("FPNG".getBytes());
                writer.write("DATA".getBytes());                //size
                writer.write(output, 0, output.length);     //image output

                handler.post(() ->{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            final Toast toast = Toast.makeText(getApplicationContext(), "Image sent", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                });
                out.close();
                writer.close();
                connection.close();
            }catch (IOException e)
            {
                e.printStackTrace();
                try {
                    connection.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                handler.post(() ->{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            final Toast toast = Toast.makeText(getApplicationContext(), "IO exeption", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                });

            }

            return null;
        }
    }
}
