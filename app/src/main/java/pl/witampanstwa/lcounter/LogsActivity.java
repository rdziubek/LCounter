package pl.witampanstwa.lcounter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by robert on 03.03.2018.
 */

public class LogsActivity extends AppCompatActivity {

    private RecyclerView rvLogs;
    private RecyclerViewAdapter rvAdapter;

    CountData countData;

    private void initRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(mLayoutManager);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        rvAdapter = new RecyclerViewAdapter(
                countData.getAlDate(),
                countData.getAlHour());
        rvLogs.setAdapter(rvAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        rvLogs = findViewById(R.id.rvLogs);

        // set up counter data
        countData = new CountData(LogsActivity.this);

        // If logs are blank
        if (countData.isLogsEmpty()) {
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