package com.example.newsapplication;

import static com.example.newsapplication.MyOpener.TABLE_NAME;
import static com.example.newsapplication.MyOpener.VERSION_NUM;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/*
SavedNewsActivity shows NewsItems objects titles in ViewList. NewsItems objects are retrieved from
SQLite database table where they are saved when the user chooses to save them in NewsActivity.
Also, the user can go to News or Home pages by choosing the appropriate options in the toolbar
or navigation drawer.
 */

public class SavedNewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    NewsItem newsItem;
    ArrayList<NewsItem> elements;
    MyListAdapter myAdapter;
    SQLiteDatabase db;

    public ArrayAdapter<NewsItem> adapter;

    public static final String ITEM_TITLE = "title";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_PUBDATE = "pubDate";
    public static final String ITEM_LINK = "link";

    private Toolbar myToolbar;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        elements = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> descriptions = new ArrayList<>();
        ArrayList<String> pubDates = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Saved News");

        //For NavigationDrawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadDataFromDatabase();

        ListView myList = findViewById(R.id.list_view);
        myList.setAdapter(myAdapter = new MyListAdapter());

        myList.setOnItemClickListener((adapterView, item, position, id) -> {
            for (int i = 0; i < elements.size(); i++) {
                newsItem = elements.get(i);
                String title = newsItem.getTitle();
                String description = newsItem.getDescription();
                String pubDate = newsItem.getPubDate();
                String link = newsItem.getLink();
                titles.add(title);
                descriptions.add(description);
                pubDates.add(pubDate);
                links.add(link);
            }

            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, titles.get(position));
            dataToPass.putString(ITEM_DESCRIPTION, descriptions.get(position));
            dataToPass.putString(ITEM_PUBDATE, pubDates.get(position));
            dataToPass.putString(ITEM_LINK, links.get(position));

            if(isTablet)
            {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(SavedNewsActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        myList.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")

                    //The message
                    .setMessage("The selected row is: " + pos)

                    //Clicking Yes button removes news item from the list
                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(elements.get(pos));
                        MyOpener myOpener = new MyOpener(this);
                        myOpener.delete(pos);
                        myAdapter.notifyDataSetChanged();
                        myOpener.close();
                    })

                    .setNegativeButton("No", (click, arg) -> {
                    })

                    .create().show();
            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.home:
                Snackbar.make(coordinatorLayout, "Going to Home Page", Snackbar.LENGTH_LONG)
                        .show();
                Intent i = new Intent(SavedNewsActivity.this, MainActivity.class);
                startActivity(i);

                break;
            case R.id.news:
                Snackbar.make(coordinatorLayout, "Going to News Page", Snackbar.LENGTH_LONG)
                        .show();
                Intent j = new Intent(SavedNewsActivity.this, NewsActivity.class);
                startActivity(j);

                break;
            case R.id.saved:
                break;
            case R.id.exit:
                finishAffinity();
                break;
        }

//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.icon1:
                Intent i = new Intent(SavedNewsActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(SavedNewsActivity.this, "You clicked on Home", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon2:
                Intent j = new Intent(SavedNewsActivity.this, NewsActivity.class);
                startActivity(j);
                Toast.makeText(SavedNewsActivity.this, "You clicked on News", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon3:
                Toast.makeText(SavedNewsActivity.this, "You clicked on Saved News", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon4:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("You can click on News Title to view the details or long click to remove the news from the saved list").show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadDataFromDatabase() {
        //Getting database connection
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();

        //Getting all of the columns
        String [] columns = {MyOpener.COL_ID, MyOpener.COL_TITLE, MyOpener.COL_DESCRIPTION, MyOpener.COL_PUBDATE, MyOpener.COL_LINK};

        //Query all the results from the database
        Cursor results = db.query(false, TABLE_NAME, columns, null, null, null, null,
                null, null);

        //Finding column indices
        int titleColumnIndex = results.getColumnIndex(MyOpener.COL_TITLE);
        int idColumnIndex = results.getColumnIndex(MyOpener.COL_ID);
        int descriptionColumnIndex = results.getColumnIndex(MyOpener.COL_DESCRIPTION);
        int pubDateColumnIndex = results.getColumnIndex(MyOpener.COL_PUBDATE);
        int linkColumnIndex = results.getColumnIndex(MyOpener.COL_LINK);

        //Iterating over the results, return true if there is a next item
        while(results.moveToNext()) {
            long id = results.getLong(idColumnIndex);
            String title = results.getString(titleColumnIndex);
            String description = results.getString(descriptionColumnIndex);
            String pubDate = results.getString(pubDateColumnIndex);
            String link = results.getString(linkColumnIndex);

            elements.add(new NewsItem(title, id, description, pubDate, link));
        }

        printCursor(results);
    }

    private void printCursor (Cursor c){

        int dbVersion = VERSION_NUM;
        int numberCursorColumns = c.getColumnCount();
        String[] nameCursorColumns = c.getColumnNames();
        int numberCursorResults = c.getCount();
        String cursorRowValues;

        //Finding column indices
        int titleColumnIndex = c.getColumnIndex(MyOpener.COL_TITLE);
        int idColumnIndex = c.getColumnIndex(MyOpener.COL_ID);
        int descriptionColumnIndex = c.getColumnIndex(MyOpener.COL_DESCRIPTION);
        int pubDateColumnIndex = c.getColumnIndex(MyOpener.COL_PUBDATE);
        int linkColumnIndex = c.getColumnIndex(MyOpener.COL_LINK);

        while(c.moveToNext()) {
            long id = c.getLong(idColumnIndex);
            String title = c.getString(titleColumnIndex);
            String description = c.getString(descriptionColumnIndex);
            String pubDate = c.getString(pubDateColumnIndex);
            String link = c.getString(linkColumnIndex);

            elements.add(new NewsItem(title, id, description, pubDate, link));
        }

        cursorRowValues = elements.stream().map(Object::toString).collect(Collectors.joining());

        Log.i("DATABASE VERSION", Integer.toString(dbVersion));
        Log.i("NUMBER OF COLUMNS", Integer.toString(numberCursorColumns));
        Log.i("COLUMN NAMES", Arrays.toString(nameCursorColumns));
        Log.i("NUMBER OF RESULTS", Integer.toString(numberCursorResults));
        Log.i("ROW VALUES", DatabaseUtils.dumpCursorToString(c));
    }

    //Implementing a BaseAdapter
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
            View newView =  old;
            LayoutInflater inflater = getLayoutInflater();

            if(newView == null){
                newView = inflater.inflate(R.layout.saved_items_layout, parent, false);
            }
            TextView textView = newView.findViewById(R.id.textGoesHere);
            textView.setText( getItem(position).getTitle() );

            return newView;
        }
    }
}
