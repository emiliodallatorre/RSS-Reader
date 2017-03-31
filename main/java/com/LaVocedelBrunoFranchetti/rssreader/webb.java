package com.LaVocedelBrunoFranchetti.rssreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebView;
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


/**
 * @author Emilio Dalla Torre.
 */
public class webb extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private Context context;
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Element image = document.select("img.alignright, img.alignleft, img.aligncenter, .size-full").first();
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
        final Button view = (Button) findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.webview);
                webView = (WebView) findViewById(R.id.view);
                webView.getSettings().setJavaScriptEnabled(false);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.loadUrl(link);
            }
        });
        final Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = title + " di " + creator + " " + link;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Condividi tramite:"));
            }
        });
        final Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"giornalino@istitutobrunofranchetti.gov.it"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Proposta di un articolo da *inserisci il tuo nome e classe*");
                i.putExtra(Intent.EXTRA_TEXT   , "Salve, questa mail è stata generata dall'app del Giornalino d'Istituto, allego l'articolo che / argomento che vorrei fosse trattato in un prossimo articolo:");
                try {
                    startActivity(Intent.createChooser(i, "Scegli come inviarlo:"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(webb.this, "Non risulta esserci alcun client di email attualmente installato su questo dispositivo.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

