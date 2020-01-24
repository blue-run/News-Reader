package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        WebView webView=findViewById(R.id.webView);

        Intent intent=getIntent();
        int indexNumber=intent.getIntExtra("index",-1);

        if(indexNumber !=-1){
            webView.loadUrl(MainActivity.newsLinks.get(indexNumber));
        }
    }
}
