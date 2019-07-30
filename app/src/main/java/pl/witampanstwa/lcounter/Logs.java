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

    private boolean isRunForTheFirstTime = false;
    private int counterValue;
    private ArrayList<String> alDate;
    private ArrayList<String> alHour;

    private SharedPreferences timeAndCountPrefs;

    private RecyclerView rvLogs;
    private RecyclerViewAdapter rvAdapter;

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        rvAdapter = new RecyclerViewAdapter(alDate, alHour);
        rvLogs.setAdapter(rvAdapter);
    }

    private String getDateFromPreferences() {
        return timeAndCountPrefs
                .getString("alDate", "");
    }

    private String getHourFromPreferences() {
        return timeAndCountPrefs
                .getString("alHour", "");
    }

    private void initialiseData() {
        //init sharedprefs
        timeAndCountPrefs = this.getSharedPreferences(
                "pl.witampanstwa.lcounter", Context.MODE_PRIVATE);

        // initialise AND restore the arrays data
        alDate = new ArrayList<>(Arrays
                .asList(getDateFromPreferences()
                        .split(",_,")));    // will NOT produce null.

        alHour = new ArrayList<>(Arrays
                .asList(getHourFromPreferences()
                        .split(",_,")));    // will NOT produce null.

        counterValue = timeAndCountPrefs.getInt("counterValue", 0);

        // indicate that the app is run for the first time by either checking the date or hour array.
        if (alHour.get(0).equals(""))
            isRunForTheFirstTime = true;

        // if app is run for the first time, both arrays contain an empty string returned by get<Date/Hour>FromPreferences (if such was to return null, a "null" string would then get appended to SharedPrefs (and a much more resource heavy solution would be needed)).
        if (isRunForTheFirstTime) {
            alDate.clear();
            alHour.clear();
        }
    }

    private void trimEntries(){
        if(counterValue != alDate.size())   // doesnt matter whether alDate or alHour is checked as their sizes are always equal
            while (counterValue < alDate.size()){
                alDate.remove(alDate.size() - 1);
                alHour.remove(alDate.size() - 1);
            }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        rvLogs = findViewById(R.id.rvLogs);

        initialiseData();
        trimEntries();

        // If logs are blank
        if (isRunForTheFirstTime) {
            findViewById(R.id.rvLogs).setVisibility(View.GONE);
            findViewById(R.id.twNoLogs).setVisibility(View.VISIBLE);
        }

        initRecyclerView();
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