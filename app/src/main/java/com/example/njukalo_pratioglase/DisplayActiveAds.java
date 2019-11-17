package com.example.njukalo_pratioglase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Script;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayActiveAds extends AppCompatActivity {
    TextView listAds;
    private NotificationManagerCompat notificationManager;
    ArrayList<String> mainAds=new ArrayList<String>();
    ArrayList<String> added= new ArrayList<String>();
    ArrayList<String> removed= new ArrayList<String>();
    String url;
    private static final int LATEST_ADS = 6;

    private void publish() {
        StringBuilder sb = new StringBuilder();
        for (String m : mainAds) {
            sb.append("https://www.njuskalo.hr");
            sb.append(m);
            sb.append(System.getProperty("line.separator"));
            sb.append(System.getProperty("line.separator"));

        }
        Toast.makeText(this, "Prikazani su linkovi trenutno aktivnih oglasa.", Toast.LENGTH_LONG).show();
        listAds.setText(sb.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_active_ads);
        listAds = findViewById(R.id.textPlace);
        notificationManager = NotificationManagerCompat.from(this);
        Intent intent = getIntent();
        mainAds = intent.getStringArrayListExtra("ads");
        url = intent.getStringExtra("url");
        publish();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                new updateAds().execute();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 60000, 60000);

    }

    public void sendOnChannel1() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pregledajte nove oglase: ");
        for(String a : added) {
            sb.append(a).append(" ");
        }
        added.clear();

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setContentTitle("Novi oglasi!")
                .setContentText(sb.toString())
                .setSmallIcon(R.drawable.ic_info_outline_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2() {
        StringBuilder sb = new StringBuilder();
        sb.append("Izbrisani oglasi: ");
        for(String r : removed) {
            sb.append(r).append(" ");
        }
        removed.clear();

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_2_ID)
                .setContentTitle("Neki oglasi više nisu dostupni.")
                .setContentText(sb.toString())
                .setSmallIcon(R.drawable.ic_info_outline_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }


    public class updateAds extends AsyncTask<Void, Void, Void> {
        ArrayList<String> newAds = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document doc = Jsoup.connect(url).get();
                Elements elems = doc.getElementsByClass("entity-title");
                for (int i = 0; i < (elems.size() - LATEST_ADS); i++) {

                    Element link = elems.get(i).select("a[href]").first();
                    newAds.add(link.attr("href"));

                }
            } catch (IOException ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            added.addAll(newAds);
            added.removeAll(mainAds);

            removed.addAll(mainAds);
            removed.removeAll(newAds);

            if(added.size() == 0 && removed.size() == 0){
                return;
            }

            else{
                if(added.size()> 0){
                    sendOnChannel1();
                }

                if(removed.size() > 0) {
                    sendOnChannel2();
                }
                mainAds.clear();
                mainAds.addAll(newAds);
                publish();
            }

            }
        }


}
