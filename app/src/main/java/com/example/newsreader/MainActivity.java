package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> newsHeads = new ArrayList<>( );
    static ArrayList<String> newsLinks = new ArrayList<>( );
    static ArrayAdapter arrayAdapter;
    LazyLoader lazyLoader;
    ListView listView;
//    static SQLiteDatabase newsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        newsDB=this.openOrCreateDatabase("ArticleStories",MODE_PRIVATE,null);
//        newsDB.execSQL("CREATE TABLE IF NOT EXISTS Articles (id INTEGER PRIMARY KEY, articleId INTEGER,title VARCHAR, content VARCHAR)");




        // AsyncTask object creation and initialisation
        GetNews getNews = new GetNews( );
        try {
            getNews.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get( );
        } catch (Exception e) {
            e.printStackTrace( );
        }
        //.................................................. initialisation of AsyncTask object ends.


        listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newsHeads);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener( ) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),SecondActivity.class);
                intent.putExtra("index",position);
                startActivity(intent);
            }
        });

    }                                                                                       // onCreate ends.


    //                  AsyncTask to download and save Website Data
//    ............................................................................
    public class GetNews extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, newsHeads);

            URL url;
            HttpURLConnection urlConnection;
            String result = "";

            try {

                // Downloading Id-s........................

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection( );
                InputStream inputStream = urlConnection.getInputStream( );
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read( );

                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = inputStreamReader.read( );
                }
//                Log.i("ids",result);
                //downloading ids end........................

                // converting the downloaded string array into json array............
                JSONArray jsonArray = new JSONArray(result);
                int numberOfItems = 20;
                if (jsonArray.length( ) < 20) {
                    numberOfItems = jsonArray.length( );
                }
//                newsDB.execSQL("DELETE FROM Articles");
                // array conversion ends......................................

                // grabbing title and urls of all ids..............
                for (int i = 0; i < numberOfItems; i++) {
                    String newsId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + newsId + ".json?print=pretty");
                    urlConnection = (HttpURLConnection) url.openConnection( );
                    inputStream = urlConnection.getInputStream( );
                    inputStreamReader = new InputStreamReader(inputStream);
                    data = inputStreamReader.read( );

                    String result2 = "";
                    while (data != -1) {
                        char ch = (char) data;
                        result2 += ch;
                        data = inputStreamReader.read( );
                    }
//                    Log.i("result",result2);
                    JSONObject jsonObject = new JSONObject(result2);
                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String newsTitle = jsonObject.getString("title");
                        String newsUrl = jsonObject.getString("url");
                        // grabbing title ends here.............................

                        newsHeads.add(newsTitle);                           // setting titles on listView
                        newsLinks.add(newsUrl);
                        arrayAdapter.notifyDataSetChanged();



                        // storing titles and articleID into the database in table Articles.........
//                        String sql="INSERT INTO Articles(articleId,title) VALUES(?,?)";
//                        SQLiteStatement statement=newsDB.compileStatement(sql);
//                        statement.bindString(1,newsId);
//                        statement.bindString(2,newsTitle);
//                        statement.execute();
                        //....................................................      storing into database ends here.

                    }
                }
                //........................................................          json processing ends here.

                return result;

            } catch (Exception e) {
                e.printStackTrace( );
                return "Error";
            }

        }                                                               // doInBackground Method ends here.
    }
//   .................................................................................... GetNews class ends here
}
