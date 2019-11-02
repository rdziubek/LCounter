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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView twCount;
    private ConstraintLayout clMain;

    private boolean helpDialogShown = false;
    private CountData countData;

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
                .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorLivingCoral));
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

    public void count(View view) {   //R.id.twCount onClick
        animateBack(
                ContextCompat.getColor(MainActivity.this, R.color.colorWhite),
                ContextCompat.getColor(MainActivity.this, R.color.colorPrimary),
                800);

        countData.addEntry();
        // update UI with updated dataset
        twCount.setText(String.valueOf(countData.getCounterValue()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clMain = findViewById(R.id.clMain);
        twCount = findViewById(R.id.twCount);

        // set up counter data
        countData = new CountData(MainActivity.this);

        twCount.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                animateBack(
                        ContextCompat.getColor(MainActivity.this, R.color.colorLivingCoral),
                        ContextCompat.getColor(MainActivity.this, R.color.colorPrimary),
                        2500);
                countData.removeEntryCountingFromLatest(0);
                twCount.setText(String.valueOf(countData.getCounterValue()));
                return true;
            }
        });

        clMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                animateBack(
                        ContextCompat.getColor(MainActivity.this, R.color.colorLivingCoral),
                        ContextCompat.getColor(MainActivity.this, R.color.colorPrimary),
                        2500);
                countData.removeEntryCountingFromLatest(0);
                twCount.setText(String.valueOf(countData.getCounterValue()));
                return true;
            }
        });

        // set up settings
        settingsPrefs = MainActivity.this.getSharedPreferences(
                "pl.witampanstwa.lcounter", Context.MODE_PRIVATE);
        helpDialogShown = settingsPrefs.getBoolean("helpDialogShown", false);

        if (!helpDialogShown)
            showHelpDialog();

        twCount.post(new Runnable() {   //has to be executed after the layout is fully loaded, since it uses the view's size.
            @Override
            public void run() {
                //refresh the view to allow it to auto resize
                twCount.setText("");
                twCount.setText(Integer.toString(countData.getCounterValue()));
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
                Intent mLogs = new Intent(MainActivity.this, LogsActivity.class);
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