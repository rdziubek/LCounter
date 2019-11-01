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

    private boolean wereLogsEmpty = false;
    private int counterValue;
    private ArrayList<String> alDate;
    private ArrayList<String> alHour;

    private SharedPreferences counterDataPrefs;

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
        return counterDataPrefs
                .getString("alDate", "");
    }

    private String getHourFromPreferences() {
        return counterDataPrefs
                .getString("alHour", "");
    }

    private void initialiseData() {
        //init sharedprefs
        counterDataPrefs = this.getSharedPreferences(
                "pl.witampanstwa.lcounter", Context.MODE_PRIVATE);

        // load (restore) the counter history data--can be restored in real-time as it provides the main functionality for this activity
        alDate = new ArrayList<>(Arrays
                .asList(getDateFromPreferences()
                        .split(",_,")));    // will NOT produce null.
        alHour = new ArrayList<>(Arrays
                .asList(getHourFromPreferences()
                        .split(",_,")));    // will NOT produce null.

        counterValue = counterDataPrefs.getInt("counterValue", 0);

        if(alDate.get(0).equals("") || alHour.get(0).equals(""))
            wereLogsEmpty = true;

        // when nothing found in sharedPrefs both arrays contain an empty string returned by get<Date/Hour>FromPreferences.
        if (wereLogsEmpty) {
            alDate.clear();
            alHour.clear();
        }
    }

    private String arrayListAsString(ArrayList<String> al) {
        return android.text.TextUtils.join(",_,", al);
    }

    private void updateCounterDataInSharedPrefs() {
        //counter
        counterDataPrefs
                .edit()
                .putInt("counterValue", counterValue)
                .apply();

        //date
        counterDataPrefs
                .edit()
                .putString("alDate", arrayListAsString(alDate))
                .apply();

        //hour
        counterDataPrefs
                .edit()
                .putString("alHour", arrayListAsString(alHour))
                .apply();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        rvLogs = findViewById(R.id.rvLogs);

        initialiseData();

        // If logs are blank
        if (wereLogsEmpty) {
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