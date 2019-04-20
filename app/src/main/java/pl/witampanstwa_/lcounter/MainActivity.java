package pl.witampanstwa_.lcounter;

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
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView twCount;
    private String alDateHelper, alHourHelper;
    private boolean helpDialogShown = false;
    //    String TAG = "lcounterapr20";

    private SharedPreferences timeAndCountPrefs;
    private SharedPreferences settingsPrefs;
    //    boolean recreateMain = false;
    private int counterValue = 0;

    private String getDateTime() {
        // TODO: replace all this with date based on locale (e.g. the day string has to be written in local language)
        return new SimpleDateFormat("EEE, MMM d, ''yy").format(new Date());
    }

    private String getHourTime() {
        //TODO: change 24-hrs to 12-hrs system based on locale
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private void saveCounter() {
        timeAndCountPrefs
                .edit()
                .putInt("counterValue", counterValue)
                .apply();
    }

    private void saveArrays() {
        timeAndCountPrefs
                .edit()
                .putString("alDate", alDateHelper)
                .apply();

        timeAndCountPrefs
                .edit()
                .putString("alHour", alHourHelper)
                .apply();
    }

    public void count(View view) {   //defines actions performed after a touch on MainActivity (not the toolbar)

        int colorFrom = getResources().getColor(R.color.colorWhite);
        int colorTo = getResources().getColor(R.color.colorPrimary);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(800);
        colorAnimation.setInterpolator(new DecelerateInterpolator(4));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                twCount.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();

        counterValue++;
        twCount.setText(String.valueOf(counterValue));
        saveCounter();

        //modify arrays
        //Add values to arrays (directly, to the arrays saved as "|" separated stings in sharedprefs).
        alDateHelper = timeAndCountPrefs.getString("alDate", "");       //update values (take them out of sharedpreferences)
        alHourHelper = timeAndCountPrefs.getString("alHour", "");

        //date
        alDateHelper = getDateTime() + (alDateHelper.equals("") ? "" : "-") + alDateHelper;
        alHourHelper = getHourTime() + (alHourHelper.equals("") ? "" : "-") + alHourHelper;
        saveArrays();

//        Log.d(TAG, "delLastCount: On COUNT date: " + timeAndCountPrefs.getString("alDate", ""));
//        Log.d(TAG, "delLastCount: On COUNT hour: " + timeAndCountPrefs.getString("alHour", ""));
    }

    //TODO: implement deleting entries located between of other ones (that is dependent on the passed in delimiter parameter).
    private String deleteCharsBetweenDelimiters(String str, int startDelimiterIndex) {  //called only if counterVal > 0.
        //find the chars range
        int i = 0, charEnd = str.length() - 1;
//        int charStart = 0, currDelimiterIndex = 0;       //charEnd is initialized to str length to handle the situation where there is no delimiter found (e.g. when there is only one entry saved) (and the " - 1", because of the missing delimiter).

        if (counterValue + 1 < 2)
            return "";
        else
            while (str.charAt(i) != '-') {
                charEnd = i;
                i++;
            }
        return str.substring(charEnd + 2);  // +1 because i starts with 0, and another +1 to exclude the delimiter
    }

    private void delLastCount() {
        if (counterValue > 0) {
            counterValue--;
            twCount.setText(String.valueOf(counterValue));

            int colorFrom = getResources().getColor(R.color.colorLivingCoral);
            int colorTo = getResources().getColor(R.color.colorPrimary);
            ValueAnimator colorAnimation =
                    ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(2500);
            colorAnimation.setInterpolator(new DecelerateInterpolator(3));
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    twCount.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();

            saveCounter();

            //modify arrays (directly, those saved as "|" separated stings in sharedprefs).
            alDateHelper = timeAndCountPrefs.getString("alDate", "");       //update values (take them out of sharedpreferences)
            alHourHelper = timeAndCountPrefs.getString("alHour", "");

            //remove all chars before the first delimiter (beginning form the start; with delimiter inclusive).
            alHourHelper = deleteCharsBetweenDelimiters(alHourHelper, 0);   //note: alHourHelper will NOT be null.
            alDateHelper = deleteCharsBetweenDelimiters(alDateHelper, 0);

            //put back modified strings to the shared prefs
            saveArrays();

//            Log.d(TAG, "delLastCount: On DEL date: " + timeAndCountPrefs.getString("alDate", ""));
//            Log.d(TAG, "delLastCount: On DEL hour: " + timeAndCountPrefs.getString("alHour", ""));
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.help_dialog, null))
                .setTitle("Hint")
                .setNegativeButton("dismiss", null).show().getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorStrongAccent));
        builder.create();
        helpDialogShown = true;
        //save the dialog state
        settingsPrefs
                .edit()
                .putBoolean("helpDialogShown", helpDialogShown)
                .apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twCount = findViewById(R.id.twCount);

        twCount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                delLastCount();
                return true;
            }
        });

        //init sharedprefs
        timeAndCountPrefs = this.getSharedPreferences(
                "pl.witampanstwa_.lcounter", Context.MODE_PRIVATE);
        settingsPrefs = this.getSharedPreferences(
                "pl.witampanstwa_.lcounter", Context.MODE_PRIVATE);

        //restore settings
        helpDialogShown = settingsPrefs.getBoolean("helpDialogShown", false);

        if (!helpDialogShown)
            showHelpDialog();

        //restore counter value
        counterValue = timeAndCountPrefs.getInt("counterValue", 0);

        twCount.setText(Integer.toString(counterValue));

        //note: it is unnecessary to restore arrays from sharedprefs in MainActivity, since only count (form sharedprefs) is displayed here; new items for the both arrays are assigned DIRECTLY into shared prefs (in a form of "|" separated string), not via an ArrayList.
        /*        //restore arrays (strings)
         **        alDate = new ArrayList<>(Arrays.asList(TextUtils.split(timeAndCountPrefs.getString("alDate", ""), "|")));
         **        alHour = new ArrayList<>(Arrays.asList(TextUtils.split(timeAndCountPrefs.getString("alHour", ""), "|")));
         */

//        Log.d(TAG, "delLastCount: On CREATE date: " + timeAndCountPrefs.getString("alDate", ""));
//        Log.d(TAG, "delLastCount: On CREATE hour: " + timeAndCountPrefs.getString("alHour", ""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_github:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.github.com/witampanstwa/"));
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