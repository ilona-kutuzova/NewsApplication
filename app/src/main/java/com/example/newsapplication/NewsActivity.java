package com.example.newsapplication;

import static com.example.newsapplication.MyOpener.TABLE_NAME;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView greeting;
    public ListView newsListView;
    public ArrayAdapter<NewsItem> adapter;
    public List<NewsItem> newsItemList = new ArrayList<NewsItem>();
    NewsItem newsItem;

    private ArrayList<String> titles;
    private ArrayList<String> descriptions;
    private ArrayList<String> pubDates;
    private ArrayList<String> links;

    public static final String ITEM_TITLE = "title";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_PUBDATE = "pubDate";
    public static final String ITEM_LINK = "link";
    SQLiteDatabase db;

    private Toolbar myToolbar;
    private ProgressBar pgsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        greeting = (TextView) findViewById(R.id.welcome);

        //Greeting User
        Intent dataSent = getIntent();
        String nameSent = dataSent.getStringExtra("name");
        String message = "Welcome " + nameSent + "!";
        greeting.setText(message);

        myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("News");

        //For NavigationDrawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        newsListView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<NewsItem>(this,
                android.R.layout.simple_list_item_1,
                newsItemList);
        newsListView.setAdapter(adapter);

        String siteURL = "https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml";
        new RetrieveFeedTask().execute(siteURL);

        newsListView.setOnItemClickListener((adapterView, item, position, id) -> {


            for (int i = 0; i < newsItemList.size(); i++) {
                newsItem = newsItemList.get(i);
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
                Intent nextActivity = new Intent(NewsActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        newsListView.setOnItemLongClickListener((adapterView, view, i, l) -> {

            newsItem = newsItemList.get(i);
            String title = newsItem.getTitle();
            String description = newsItem.getDescription();
            String pubDate = newsItem.getPubDate();
            String link = newsItem.getLink();
            ContentValues newRowValues = new ContentValues();

            //Getting database connection
            MyOpener dbOpener = new MyOpener(this);
            db = dbOpener.getWritableDatabase();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to save it for later?")
                    .setMessage("The selected row is: " + i)

                    .setPositiveButton("Yes", (click, arg) -> {

                        newRowValues.put(MyOpener.COL_TITLE, title);
                        newRowValues.put(MyOpener.COL_DESCRIPTION, description);
                        newRowValues.put(MyOpener.COL_LINK, link);
                        newRowValues.put(MyOpener.COL_PUBDATE, pubDate);

                        long newID = db.insert(TABLE_NAME, null, newRowValues);

                    })

                    .setNegativeButton("No", (click, arg) -> {})
                    .create().show();

//            Toast.makeText(getApplicationContext(), "long clicked", Toast.LENGTH_LONG).show();
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
                Intent i = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.news:
                break;
            case R.id.saved:
                Intent j = new Intent(NewsActivity.this, SavedNewsActivity.class);
                startActivity(j);
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
                Intent i = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(NewsActivity.this, "You clicked on Home", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon2:

                Toast.makeText(NewsActivity.this, "You clicked on News", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon3:
                Intent j = new Intent(NewsActivity.this, SavedNewsActivity.class);
                startActivity(j);
                Toast.makeText(NewsActivity.this, "You clicked on Saved News", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon4:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("You can click on News Title to view the details or long click to save the news for later reading").show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public List<NewsItem> parseRSS(URL feedURL)
            throws XmlPullParserException, IOException {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(feedURL.openStream(), null);

        int eventType = parser.getEventType();

        boolean done = false;

        NewsItem currentNewsItem= new NewsItem();
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        pubDates = new ArrayList<>();
        links = new ArrayList<>();

        while (eventType != XmlPullParser.END_DOCUMENT && !done) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")) {
                        // a new item element
                        currentNewsItem = new NewsItem();



                    } else if (currentNewsItem != null) {
                        if (name.equalsIgnoreCase("link")) {
                            currentNewsItem.setLink(parser.nextText());
                        } else if (name.equalsIgnoreCase("description")) {
                            currentNewsItem.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("pubDate")) {
                            currentNewsItem.setPubDate(parser.nextText());
                        } else if (name.equalsIgnoreCase("title")) {
                            currentNewsItem.setTitle(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && currentNewsItem != null) {
                        newsItemList.add(currentNewsItem);

                    } else if (name.equalsIgnoreCase("channel")) {
                        done = true;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return newsItemList;
    }

    //Using AsyncTask
    class RetrieveFeedTask extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... urls) {
            try {
                URL feedURL = new URL(urls[0]);
                newsItemList = parseRSS(feedURL);
                int i = 0;
                publishProgress(i);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
        }
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            ((ProgressBar)findViewById(R.id.progress_bar)).setProgress(values[0]);

//            if (values[0] == 0) {
//                ((ImageView)findViewById(R.id.image_view)).setImageBitmap(image);
//            }

//        }
    }
}