package com.LaVocedelBrunoFranchetti.rssreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.LaVocedelBrunoFranchetti.rssreader.R.layout.activity_main;


/**
 * @author Emilio Dalla Torre.
 */
public class webb extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        progressDialog = ProgressDialog.show(webb.this,
                "Caricamento...", "Caricamento in corso...", true);
        final String link = getIntent().getStringExtra("link");
        final String title = getIntent().getStringExtra("title");
        final String creator = getIntent().getStringExtra("creator");
        StrictMode.setThreadPolicy(policy);
        Document document = null;
        try {
            document = Jsoup.connect(link).userAgent("Mozilla").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String divs = document.select("div.entry-content.clearfix").text();
        setContentView(R.layout.articleview);

        TextView art = (TextView) findViewById(R.id.art);
        art.setText(divs);

        TextView titlein = (TextView) findViewById(R.id.titlein);
        titlein.setText(title);

        TextView creatorin = (TextView) findViewById(R.id.creatorin);
        creatorin.setText("di " + creator);

        ImageView imageView = (ImageView) findViewById(R.id.iw);
        URL url = null;

        try {
            Element image = document.select("img.alignright, img.alignleft, img.aligncenter").first();
            String imgurl = image.absUrl("src");


            try {
                url = new URL(imgurl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            CharSequence text = "Questo articolo non contiene immagini o quelle presenti potrebbero non essere supportate da questa app. Per visualizzare completamente l'articolo, visita: ";
            Toast.makeText(this, text + link, Toast.LENGTH_LONG).show();
        }
        final Button button = (Button) findViewById(R.id.share);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = title + " di " + creator + " " + link;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Condividi tramite:"));
            }
        });
        final Button email = (Button) findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","giornalino@istitutobrunofranchetti.gov.it", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Proposta di articolo da *inserisci il tuo nome e classe*");
                intent.putExtra(Intent.EXTRA_TEXT, "Salve, questa mail è stata generata dall'app del Giornalino d'Istituto, allego l'articolo che ho scritto / argomento che vorrei venisse trattato:");
                startActivity(Intent.createChooser(intent, "Scegli come inviarlo:"));
            }
        });
        progressDialog.dismiss();
    }
}

