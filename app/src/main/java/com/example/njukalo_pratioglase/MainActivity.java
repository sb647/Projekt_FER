package com.example.njukalo_pratioglase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText text;
    Button button;
    String url = new String();
    ArrayList<String> ads = new ArrayList<String>();
    private static final int LATEST_ADS = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.enterURL);
        button = findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                url = text.getText().toString();
                new readAds().execute();

            }
        });
    }

    public class readAds extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements elems = doc.getElementsByClass("entity-title");
                for (int i = 0; i < (elems.size() - LATEST_ADS); i++) {

                    Element link = elems.get(i).select("a[href]").first();
                    ads.add(link.attr("href"));
                }
            }catch (Exception ex) {
                invalidURl();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(MainActivity.this, DisplayActiveAds.class);
            intent.putStringArrayListExtra("ads", ads);
            intent.putExtra("url",url);
            startActivity(intent);
        }
    }
    protected void invalidURl(){
        Toast.makeText(this, "Neispravan URL.", Toast.LENGTH_SHORT);
        text.setText("");
        this.recreate();
    }

}
