package pl.witampanstwa.lcounter;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class CountData {
    private int counterValue;
    private boolean logsEmpty = false;
    private ArrayList<String> alDate;
    private ArrayList<String> alHour;

    private SharedPreferences counterDataPrefs;

    public CountData(Context activityContext) {
        // init sharedprefs
        counterDataPrefs = activityContext.getSharedPreferences(
                "pl.witampanstwa.lcounter", Context.MODE_PRIVATE);

        // restore counter value
        counterValue = counterDataPrefs.getInt("counterValue", 0);

        // restore day and time data
        alDate = new ArrayList<>(Arrays
                .asList(getDateFromPreferences()
                        .split(",_,")));    // will NOT produce null.
        alHour = new ArrayList<>(Arrays
                .asList(getHourFromPreferences()
                        .split(",_,")));    // will NOT produce null.

        if (alDate.get(0).equals("") || alHour.get(0).equals(""))
            logsEmpty = true;

        // when nothing found in sharedPrefs both arrays contain an empty string returned by get<Date/Hour>FromPreferences
        if (logsEmpty) {
            alDate.clear();
            alHour.clear();
        }
    }

    public int getCounterValue() {
        return counterValue;
    }

    public boolean isLogsEmpty() {
        return logsEmpty;
    }

    public ArrayList<String> getAlDate() {
        return alDate;
    }

    public ArrayList<String> getAlHour() {
        return alHour;
    }

    public void addEntry() {
        counterValue++;
        alDate.add(getDateTime());
        alHour.add(getHourTime());
        updateCounterDataInSharedPrefs();
    }

    public void removeEntryCountingFromLatest(int index) {
        if (counterValue > 0) {
            int lastEntryIndex = alDate.size() - 1;
            int entryToBeRemovedIndex = lastEntryIndex - index;

            // update the data
            counterValue--;     // only one entry is deleted each call
            alDate.remove(entryToBeRemovedIndex);
            alHour.remove(entryToBeRemovedIndex);

            // save the updated data permanently
            updateCounterDataInSharedPrefs();
        }
    }

    private String arrayListAsString(ArrayList<String> al) {
        return android.text.TextUtils.join(",_,", al);
    }

    private void updateCounterDataInSharedPrefs() {
        // counter
        counterDataPrefs
                .edit()
                .putInt("counterValue", counterValue)
                .apply();

        // date
        counterDataPrefs
                .edit()
                .putString("alDate", arrayListAsString(alDate))
                .apply();

        // hour
        counterDataPrefs
                .edit()
                .putString("alHour", arrayListAsString(alHour))
                .apply();
    }

    private String getDateTime() {
        return new SimpleDateFormat("EEE, MMM d, ''yy").format(new Date());
    }

    private String getHourTime() {
        //TODO: change 24-hrs to 12-hrs system based on locale
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String getDateFromPreferences() {
        return counterDataPrefs
                .getString("alDate", "");
    }

    private String getHourFromPreferences() {
        return counterDataPrefs
                .getString("alHour", "");
    }
}
