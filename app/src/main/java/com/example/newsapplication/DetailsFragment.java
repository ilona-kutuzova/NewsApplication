package com.example.newsapplication;

import static com.example.newsapplication.MyOpener.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }

    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    private String title;
    private String description;
    private String pubDate;
    private String link;

    NewsItem newsItem;
    SQLiteDatabase db;
    private MyListAdapter myAdapter;
    private ArrayList<NewsItem> elements;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        dataFromActivity = getArguments();
        title = dataFromActivity.getString(NewsActivity.ITEM_TITLE);
        description = dataFromActivity.getString(NewsActivity.ITEM_DESCRIPTION);
        pubDate = dataFromActivity.getString(NewsActivity.ITEM_PUBDATE);
        link = dataFromActivity.getString(NewsActivity.ITEM_LINK);

        elements = new ArrayList<>();

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_details, container, false);




        TextView fill_title = (TextView) result.findViewById(R.id.fill_title);
        fill_title.setText(title);

        TextView fill_description = (TextView) result.findViewById(R.id.fill_description);
        fill_description.setText(dataFromActivity.getString(NewsActivity.ITEM_DESCRIPTION));

        TextView fill_pubDate = (TextView) result.findViewById(R.id.fill_pubDate);
        fill_pubDate.setText(dataFromActivity.getString(NewsActivity.ITEM_PUBDATE));

        TextView fill_link = (TextView) result.findViewById(R.id.fill_link);
        fill_link.setText(dataFromActivity.getString(NewsActivity.ITEM_LINK));

        fill_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = dataFromActivity.getString(NewsActivity.ITEM_LINK);

                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

//        fill_link.setOnClickListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String url = dataFromActivity.getString(NewsActivity.ITEM_LINK);
//
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//
////            @Override
////            public void onClick(View v) {
////                String url = dataFromActivity.getString(NewsActivity.ITEM_LINK);
////
////                Uri uri = Uri.parse(url);
////                Intent intent = new Intent(Intent.ACTION_VIEW, url);
////                startActivity(intent);
////            }
//        });

//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(NewsActivity.ITEM_LINK));
//                startActivity(browserIntent);


        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }

    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public NewsItem getItem(int position) {
            return elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            if (newView == null) {
                newView = inflater.inflate(R.layout.fragment_details, parent, false);
            }
//            TextView textView = newView.findViewById(R.id.textGoesHere);
//            textView.setText(getItem(position).getTitle());


            return newView;
        }
    }
}
