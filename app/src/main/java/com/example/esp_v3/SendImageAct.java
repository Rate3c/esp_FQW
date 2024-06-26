package com.example.esp_v3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.esp_v3.databinding.SendKokBinding;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Objects;

public class SendImageAct extends AppCompatActivity {
    Handler handler = new Handler();
    private boolean invers = false;
    private boolean colored = false;
    private SendKokBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SendKokBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invers = binding.switch1.isChecked();
            }
        });

        binding.switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invers) {
                    binding.switch1.toggle();
                    invers = false;
                }
                colored = binding.switch2.isChecked();
            }
        });

        binding.buttonImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(1);
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
                    selectedImage.setDensity(4);
                    //

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] array = bos.toByteArray();


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

                Intent intent_image = getIntent();
                String address = intent_image.getStringExtra("address");
                int port = Integer.parseInt(Objects.requireNonNull(intent_image.getStringExtra("port")));

                connection = new Socket(address, port);

                OutputStream out = connection.getOutputStream();
                DataOutputStream writer = new DataOutputStream(out);

                byte[] output = voids[0];

                writer.write("IMGE".getBytes());
                writer.write(ByteBuffer.allocate(4).putInt(4).array());
                if (colored) writer.write("SCLR".getBytes());           // send Colored image if checked
                else if (invers) writer.write("INVB".getBytes());       // send Inversed W/B image if checked
                else writer.write("STUB".getBytes());                   // send Straight  W/B image if unchecked
                writer.write("SIZE".getBytes());
                writer.write(ByteBuffer.allocate(4).putInt(output.length).array());
                writer.write("DATA".getBytes());
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
