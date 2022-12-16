package com.example.newsapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;

/*
MainActivity gives the user option to enter their name and to click BBC News button to view the
news. Also, the user can go to News or SavedNews by using Toolbar or NavigationDrawer options.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button news_button;
    EditText name;
    SharedPreferences prefs = null;

    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText) findViewById(R.id.edit_text);
        news_button = (Button)  findViewById(R.id.bbc_news_button);

        Intent nextPage = new Intent(this, NewsActivity.class);
        nextPage.putExtra("name", name.getText().toString());

        ActivityResultLauncher<Intent> resultFromNameActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // If the result code is zero, the user wants to change their name
                        if (result.getResultCode() == 0) {
                            name.setText("");
                        }
                        // If the result code is 1, close the app
                        else if (result.getResultCode() == 1) {
                            finish();
                        }
                    }
                });

        //Loading the user's name from the SharedPreferences and
        //putting it in the EditText
        SharedPreferences prefs2 = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);
        String savedString = prefs2.getString("name", "");
        name.setText(savedString);

        myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Home");

        //For NavigationDrawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, myToolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);




        news_button.setOnClickListener(click -> {onStartActivityForResult(nextPage);
            resultFromNameActivity.launch(nextPage);});


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
                break;
            case R.id.news:
                Intent i = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(i);
                break;
            case R.id.saved:
                Intent j = new Intent(MainActivity.this, SavedNewsActivity.class);
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
                Toast.makeText(MainActivity.this, "You clicked on Home", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon2:
                Intent i = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(i);
                Toast.makeText(MainActivity.this, "You clicked on News", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon3:
                Intent j = new Intent(MainActivity.this, SavedNewsActivity.class);
                startActivity(j);
                Toast.makeText(MainActivity.this, "You clicked on Saved News", Toast.LENGTH_LONG).show();
                return true;
            case R.id.icon4:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("You can enter your name and click BBC News Button to view recent news").show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    protected void onStartActivityForResult(Intent nextPage) {

        nextPage.putExtra("name", name.getText().toString());

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Saving the current value inside the EditText to the Shared Preferences
        prefs = getSharedPreferences("MySharedPrefs",MODE_PRIVATE);

        SharedPreferences.Editor edit = prefs.edit();

        edit.putString("name", name.getText().toString());
        edit.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}

