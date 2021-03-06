/*
  In Class Assignment 4
* Name: Arunkumar Bagavathi - Michael Vitulli
* */

package com.group6a_inclass04.group6a_inclass04;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> id = new ArrayList<String>();
    static int fPicId = -1;
    static final int fTOAST_LENTH = Toast.LENGTH_LONG;
    static ImageView fPicView;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fPicView = (ImageView) findViewById(R.id.imageViewMainImg);

        findViewById(R.id.imageViewNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fPicId++;

                if (fPicId > id.size()-1)
                    fPicId = 0;

                if(connectedOnline()) {
                    if(id.isEmpty())
                        new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php");
                    else
                        new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php?pid=" + id.get(fPicId));
                }

            }
        });

        findViewById(R.id.imageViewPrevious).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fPicId--;

                if (fPicId < 0)
                    fPicId = id.size()-1;

                if(connectedOnline()) {
                    if(id.isEmpty())
                        new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php");
                    else
                        new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php?pid=" + id.get(fPicId));
                }

            }
        });

        if(connectedOnline()){
            new GetList().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php");
            //Log.d("Phot_ID", id.get(0));

            fPicId = 0;


            String lPicId = getPhoto(fPicId);

            new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php?pid=" + lPicId);
        }
        else Toast.makeText(MainActivity.this, "Internet Not Connected", fTOAST_LENTH).show();


    }

    private boolean connectedOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info!=null && info.isConnected()){
            return true;
        }
        else{

            Toast.makeText(MainActivity.this, "Internet Not Connected", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    //To get List of IDS
    private class GetList extends AsyncTask<String,Void,ArrayList<String>>{
        protected BufferedReader reader=null;
        ArrayList<String> idList = new ArrayList<String>();

        @Override
        protected ArrayList<String> doInBackground(String... params) {

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
                Toast.makeText(MainActivity.this, "No IDS", fTOAST_LENTH).show();
                Log.d("Phot_ID", "No IDS");
            }else{
                for(String photoId:idList){
                    id.add(photoId);

                }


                fPicId = 0;
                new GetImage().execute("http://dev.theappsdr.com/lectures/inclass_photos/index.php?pid="+id.get(fPicId));


            }
        }
    }


    //To get List of Image
    private class GetImage extends AsyncTask<String,Void,Bitmap>{
        protected InputStream in = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(MainActivity.this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMax(100);
            progress.setCancelable(false);
            progress.setMessage("Loading...");
            progress.show();
        }


        @Override
        protected Bitmap doInBackground(String... params) {

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


            return null;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            progress.dismiss();

            if(image!=null) {
                ImageView viewImage = (ImageView) findViewById(R.id.imageViewMainImg);
                viewImage.setImageBitmap(image);

            }

            else{

                Toast.makeText(MainActivity.this, "No Image", fTOAST_LENTH).show();

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

    public void nextOnClick (View aView){
        if(connectedOnline()){

        }
    }

    public void previousOnClick (View aView){
        if(connectedOnline()){

        }
    }
}
