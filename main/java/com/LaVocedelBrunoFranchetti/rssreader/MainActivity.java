package com.LaVocedelBrunoFranchetti.rssreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
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
            HttpURLConnection baglanti = null;
            try {
                URL url = new URL(params[0]);
                baglanti = (HttpURLConnection) url.openConnection();
                int baglantiDurumu = baglanti.getResponseCode();

                if (baglantiDurumu == HttpURLConnection.HTTP_OK) {

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(baglanti.getInputStream());
                    publishProgress("Caricamento in corso...");
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

                        Model model = new Model();
                        model.setTitle(title);
                        model.setLink(link);
                        model.setDate(date);
                        model.setCreator(creator);

                        Pattern p = Pattern.compile(".*<img[^>]*src=\"([^\"]*)", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(description);
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
                if (baglanti != null)
                    baglanti.disconnect();
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