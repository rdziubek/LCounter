package pl.witampanstwa.lcounter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView twCount;
    private ConstraintLayout clMain;

    private boolean isRunForTheFirstTime = false;
    private int counterValue = 0;
    private boolean helpDialogShown = false;
    private ArrayList<String> alDate;
    private ArrayList<String> alHour;

    private SharedPreferences timeAndCountPrefs;
    private SharedPreferences settingsPrefs;

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog, null))
                .setNegativeButton("dismiss", null)
                .show()
                .getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.colorLivingCoral));
        builder.create();
        helpDialogShown = true;
        //save the dialog state
        settingsPrefs
                .edit()
                .putBoolean("helpDialogShown", helpDialogShown)
                .apply();
    }

    private void animateBack(int colorFrom, int colorTo, int duration) {
        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration);
        colorAnimation.setInterpolator(new DecelerateInterpolator(3));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                clMain.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    private String getDateTime() {
        return new SimpleDateFormat("EEE, MMM d, ''yy").format(new Date());
    }

    private String getHourTime() {
        //TODO: change 24-hrs to 12-hrs system based on locale
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String arrayListAsString(ArrayList<String> al) {
        return android.text.TextUtils.join(",_,", al);
    }

    private void updateTimeAndCountDataInSharedPrefs() {
        //counter
        timeAndCountPrefs
                .edit()
                .putInt("counterValue", counterValue)
                .apply();

        //date
        timeAndCountPrefs
                .edit()
                .putString("alDate", arrayListAsString(alDate))
                .apply();

        //hour
        timeAndCountPrefs
                .edit()
                .putString("alHour", arrayListAsString(alHour))
                .apply();
    }

    public void count(View view) {   //R.id.twCount onClick
        animateBack(
                getResources().getColor(R.color.colorWhite),
                getResources().getColor(R.color.colorPrimary),
                800);

        //update the data
        counterValue++;
        twCount.setText(String.valueOf(counterValue));
        alDate.add(getDateTime());
        alHour.add(getHourTime());

        //save the updated data permanently
        updateTimeAndCountDataInSharedPrefs();
    }

    // TODO: Convert this into "removeEntryAtIndex(int i)".
    private void removeLastEntry() {
        if (counterValue > 0) {
            //update the data
            counterValue--;
            twCount.setText(String.valueOf(counterValue));
            alDate.remove(alDate.size() - 1);
            alHour.remove(alHour.size() - 1);
        }

        animateBack(
                getResources().getColor(R.color.colorLivingCoral),
                getResources().getColor(R.color.colorPrimary),
                2500);

        //save the updated data permanently
        updateTimeAndCountDataInSharedPrefs();
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
        settingsPrefs = this.getSharedPreferences(
                "pl.witampanstwa.lcounter", Context.MODE_PRIVATE);

        //restore settings
        helpDialogShown = settingsPrefs.getBoolean("helpDialogShown", false);

        //restore counter value
        counterValue = timeAndCountPrefs.getInt("counterValue", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clMain = findViewById(R.id.clMain);
        twCount = findViewById(R.id.twCount);

        twCount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeLastEntry();
                return true;
            }
        });

        clMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeLastEntry();
                return true;
            }
        });

        initialiseData();

        if (!helpDialogShown)
            showHelpDialog();

        twCount.setText(Integer.toString(counterValue));

        twCount.post(new Runnable() {   //has to be executed after the layout is fully loaded, since it uses the view's size.
            @Override
            public void run() {
                //refresh the view to allow it to auto resize
                twCount.setText("");
                twCount.setText(Integer.toString(counterValue));

                //load (restore) the counter history data
                alDate = new ArrayList<>(Arrays
                        .asList(getDateFromPreferences()
                                .split(",_,")));    // will NOT produce null.
                alHour = new ArrayList<>(Arrays
                        .asList(getHourFromPreferences()
                                .split(",_,")));    // will NOT produce null.

                // app-run-first-time indicator
                if (counterValue == 0)
                    isRunForTheFirstTime = true;

                // if app is run for the first time, both arrays contain an empty string returned by get<Date/Hour>FromPreferences.
                if (isRunForTheFirstTime) {
                    alDate.clear();
                    alHour.clear();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/witampanstwa/LCounter/"));
                startActivity(browserIntent);
                return true;

            case R.id.menu_item_list:
                Intent mLogs = new Intent(MainActivity.this, Logs.class);
                startActivityForResult(mLogs, 0);
                return true;

            case R.id.menu_item_help:
                showHelpDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
}