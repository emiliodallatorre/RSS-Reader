package com.LaVocedelBrunoFranchetti.rssreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.LaVocedelBrunoFranchetti.rssreader.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog progressDialog;
    private List<Model> modelList;
    private ActionBar actionBar;
    private Context context;
    private WebView webView;
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.who:
                String link = ("http://istitutobrunofranchetti.gov.it/giornalino/chi-siamo");
                Intent intent = new Intent(context, view.class);
                intent.putExtra("link", link);
                context.startActivity(intent);
                break;
            case R.id.mail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"emiliodallatorre12@live.com"});
                try {
                    startActivity(Intent.createChooser(i, "Scegli come inviarlo:"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Non risulta esserci alcun client di email attualmente installato su questo dispositivo.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.rate:
                try {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.LaVocedelBrunoFranchetti.rssreader")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.LaVocedelBrunoFranchetti.rssreader")));
        }
                break;}
        return true;}

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(activity_main);

        listView = (ListView) findViewById(R.id.listView);


        new HaberServisiAsynTask().execute("http://istitutobrunofranchetti.gov.it/giornalino/feed/");

        actionBar = getSupportActionBar();


        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);


        actionBar.setCustomView(R.layout.action_bar_title);
    }

    class HaberServisiAsynTask extends AsyncTask<String, String, List<Model>> {


        @Override
        protected List<Model> doInBackground(String... params) {
            modelList = new ArrayList<Model>();
            HttpURLConnection connessione = null;
            try {
                URL url = new URL(params[0]);
                connessione = (HttpURLConnection) url.openConnection();
                int baglantiDurumu = connessione.getResponseCode();
                System.out.println(connessione);

                if (baglantiDurumu == HttpURLConnection.HTTP_OK) {

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(connessione.getInputStream());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                        publishProgress("Caricamento in corso...");
                    }
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.parse(bufferedInputStream);

                    NodeList haberNodeList = document.getElementsByTagName("item");

                    for (int i = 0; i < haberNodeList.getLength(); i++) {
                        Element element = (Element) haberNodeList.item(i);

                        NodeList nodeListTitle = element.getElementsByTagName("title");
                        NodeList nodeListLink = element.getElementsByTagName("link");
                        NodeList nodeListDate = element.getElementsByTagName("pubDate");
                        NodeList nodeListCreator = element.getElementsByTagName("dc:creator");
                        NodeList nodeListDescription = element.getElementsByTagName("description");

                        String title = nodeListTitle.item(0).getFirstChild().getNodeValue();
                        String link = nodeListLink.item(0).getFirstChild().getNodeValue();
                        String date = nodeListDate.item(0).getFirstChild().getNodeValue();
                        String creator = nodeListCreator.item(0).getFirstChild().getNodeValue();
                        String description = nodeListDescription.item(0).getFirstChild().getNodeValue();

                        String imgurl = null;
                        org.jsoup.nodes.Document documentA = null;
                        try {
                            documentA = Jsoup.connect(link).userAgent("Mozilla").get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            org.jsoup.nodes.Element image = documentA.select("img.alignright, img.alignleft, img.aligncenter, .size-full").first();
                            imgurl = image.absUrl("src");
                        } catch (java.lang.NullPointerException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = null;
                        Bitmap resizedbitmap = null;
                        try {
                            URL urlResim = new URL(imgurl);
                            bitmap = BitmapFactory.decodeStream(urlResim.openConnection().getInputStream());
                            resizedbitmap = Bitmap.createScaledBitmap(bitmap, 250, 150, true);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Model model = new Model();
                        model.setTitle(title);
                        model.setLink(link);
                        model.setDate(date);
                        model.setCreator(creator);
                        model.setImage(resizedbitmap);
                        modelList.add(model);
                        publishProgress("Caricamento...");
                    }


                } else {
                    Toast.makeText(MainActivity.this,
                            "Per cortesia, controlla di essere connesso a internet.",
                            Toast.LENGTH_SHORT).show();

                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,
                        "Errore del server internet: il sito www.istitutobrunofranchetti.gov.it/giornalino non è momentaneamente raggiungibile.",
                        Toast.LENGTH_LONG).show();
            } finally {
                if (connessione != null)
                    connessione.disconnect();
            }

            return modelList;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Caricamento...", "Caricamento...", true);
        }

        @Override
        protected void onPostExecute(List<Model> modelList) {
            super.onPostExecute(modelList);
            CustomAdaptor adapter = new CustomAdaptor(MainActivity.this, modelList);
            listView.setAdapter(adapter);
            progressDialog.cancel();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }
    }

}