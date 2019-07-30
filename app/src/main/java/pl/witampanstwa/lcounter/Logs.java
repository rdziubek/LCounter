package pl.witampanstwa.lcounter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by rober on 03.03.2018.
 */

public class Logs extends AppCompatActivity {

    private SharedPreferences timeAndCountPrefs;
    private ArrayList<String> alDate;
    private ArrayList<String> alHour;

    private void initRecycler() {
        //restore arrays (strings)
        alDate = new ArrayList<>(Arrays
                .asList(timeAndCountPrefs
                        .getString("alDate", "")
                        .split("-")));    // will NOT produce null.

        alHour = new ArrayList<>(Arrays
                .asList(timeAndCountPrefs
                        .getString("alHour", "")
                        .split("-")));    // will NOT produce null.

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvLogs);
        RecyclerViewAdapter rvAdapter = new RecyclerViewAdapter(alDate, alHour);
        recyclerView.setAdapter(rvAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_logs);

        //init sharedprefs
        timeAndCountPrefs = this.getSharedPreferences(
                "pl.witampanstwa.lcounter", Context.MODE_PRIVATE);

        initRecycler();

        // If logs are blank:
        if(alDate.get(0).equals("")) {
            findViewById(R.id.rvLogs).setVisibility(View.GONE);
            findViewById(R.id.twNoLogs).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_github) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/witampanstwa/LCounter/"));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}