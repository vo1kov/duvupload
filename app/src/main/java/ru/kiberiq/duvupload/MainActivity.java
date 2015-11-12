package ru.kiberiq.duvupload;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Button buttonLoad;
    private Button buttonSelect;
    private Button buttonGet;

    private int PICK_IMAGE_REQUEST = 1;
    private String path;

    //private String url = "http://kozlov-traffic-ras.ru/post.php";
    private String url = "http://ec2-54-191-33-119.us-west-2.compute.amazonaws.com:8000";

    Context context;
    File f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoad = (Button) findViewById(R.id.load);
        buttonSelect = (Button) findViewById(R.id.select);
        buttonGet = (Button) findViewById(R.id.result);

        EditText edit = (EditText) findViewById(R.id.editText);
        edit.setText(url);

        //edit.addTextChangedListener(Tex);

        context = this.getApplicationContext();


        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (f != null) {

                    RequestParams params = new RequestParams();
                    try {
                        params.put("image", f);
                    } catch (FileNotFoundException e) {
                        Toast toast = Toast.makeText(context, "FILE NOT FOUND", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    params.put("side", "left");
                    params.put("id", "test_id_44");

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(url, params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            String str = "err";
                            try {
                                str = new String(response, "UTF-8");

                            } catch (java.io.UnsupportedEncodingException e) {
                                Toast toast = Toast.makeText(context, "ENCODING ERR", Toast.LENGTH_LONG);
                                toast.show();
                            }

                            Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
                            toast.show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Toast toast = Toast.makeText(context, "4** ERROR", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });

                } else {
                    Toast toast = Toast.makeText(context, "SELECT FILE", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });


        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });


        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                RequestParams params = new RequestParams();
                params.put("id", "test_id_44");

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(url, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        String str = "err";
                        try {
                            str = new String(response, "UTF-8");

                        } catch (java.io.UnsupportedEncodingException e) {
                            Toast toast = Toast.makeText(context, "ENCODING ERR", Toast.LENGTH_LONG);
                            toast.show();
                        }

                        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
                        toast.show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Toast toast = Toast.makeText(context, "4** ERROR", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //path = uri.getPath();

                f = new File(context.getCacheDir(), "temp");
                f.createNewFile();

//Convert bitmap to byte array

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
