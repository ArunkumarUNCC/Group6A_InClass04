package com.group6a_inclass04.group6a_inclass04;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> id = new ArrayList<String>();
    static int fPicId = -1;
    static ImageView fPicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fPicView = (ImageView) findViewById(R.id.imageViewMainImg);

        if(connectedOnline()){
            new GetList().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php");
            //Log.d("Phot_ID", id.get(0));

            fPicId = 0;

            //String lPicId = getPhoto(fPicId);

            //new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php?pid="+id.get(0));
        }
        else Toast.makeText(MainActivity.this, "Internet Not Connected", Toast.LENGTH_SHORT).show();

    }

    private boolean connectedOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info!=null && info.isConnected()){
            return true;
        }
        else return false;
    }

    //To get List of IDS
    private class GetList extends AsyncTask<String,Void,ArrayList<String>>{
        protected BufferedReader reader=null;
        ArrayList<String> idList = new ArrayList<String>();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            //URL url = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line = "";

                while((line = reader.readLine()) != null){
                    idList.add(line);
                }
                return idList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return idList;
            //return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> idList) {
            super.onPostExecute(idList);

            if(idList.isEmpty()) {
                Toast.makeText(MainActivity.this, "No IDS", Toast.LENGTH_SHORT).show();
                Log.d("Phot_ID", "No IDS");
            }

            else{

                for(String photoId:idList){
                    id.add(photoId);

                    //Log.d("Phot_ID", id.get(0));
                }
                //ImageView viewImage = (ImageView) findViewById(R.id.imageViewMainImg);
                new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php?pid="+id.get(0));

            }
        }
    }


    //To get List of Image
    private class GetImage extends AsyncTask<String,Void,Bitmap>{
        protected InputStream in = null;

        @Override
        protected Bitmap doInBackground(String... params) {
            //URL url = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                in = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(in);

                return image;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            //return idList;
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);

            if(image!=null) {
                ImageView viewImage = (ImageView) findViewById(R.id.imageViewMainImg);
                viewImage.setImageBitmap(image);
//                Toast.makeText(MainActivity.this, "No IDS", Toast.LENGTH_SHORT).show();
//                Log.d("Phot_ID", "No IDS");
            }

            else{

                Toast.makeText(MainActivity.this, "No Image", Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getPhoto(int picId){
        return id.get(picId);
    }
}
