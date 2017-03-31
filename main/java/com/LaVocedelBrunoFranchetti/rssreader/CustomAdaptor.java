package com.LaVocedelBrunoFranchetti.rssreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author Emilio Dalla Torre.
 */

public class CustomAdaptor extends BaseAdapter {

    private Context context;
    private List<Model> modelList;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


    public CustomAdaptor(Context context, List<Model> modelList) {
        this.context = context;
        this.modelList = modelList;

    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int i) {
        return modelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StrictMode.setThreadPolicy(policy);

        LinearLayout rootView = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.custom_list, null);
        TextView date_and_creator = (TextView) rootView.findViewById(R.id.date_and_creator);
        final TextView title = (TextView) rootView.findViewById(R.id.title);
        final ImageView image = (ImageView) rootView.findViewById(R.id.resim);
        final Model model = modelList.get(i);

        Date date = new Date(model.getDate());

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        title.setText(model.getTitle());
        date_and_creator.
                setText(String.format("%02d:%02d", date.getHours(), date.getMinutes()) + " | " +
                        dateFormat.format(date) + "   |   " +
                        model.getCreator());
        Bitmap bitmap = model.getImage();
        image.setImageBitmap(bitmap);

        rootView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String link = (model.getLink());
                String title = (model.getTitle());
                String creator = (model.getCreator());
                Intent intent = new Intent(context, webb.class);
                intent.putExtra("link", link);
                intent.putExtra("title", title);
                intent.putExtra("creator", creator);
                context.startActivity(intent);

            }

        });

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

        return rootView;
    }
}