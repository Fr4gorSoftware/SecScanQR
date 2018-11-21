package de.t_dankworth.secscanqr.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.DatabaseHelper;

import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;


/**
 * Created by Thore Dankworth
 * Last Update: 10.08.2018
 * Last Update by Nicolas Lazzari
 * <p>
 * This class is the HistoryActivity and lists all scanned qr-codes
 */


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "History";
    DatabaseHelper historyDatabaseHelper;
    private ListView historyListView;
    final AppCompatActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyDatabaseHelper = new DatabaseHelper(this);
        historyListView = findViewById(R.id.listView);

        showDataInListView();
    }

    /**
     * Refreshes the History after coming back from the HistoryDetailsActivity
     */
    @Override
    public void onResume() {
        super.onResume();

        showDataInListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.history_optionsmenu_delete) {
            resetDatabase();
        } else if (id == R.id.history_optionsmenu_share) {
            Cursor data = historyDatabaseHelper.getData();
            StringBuilder codes = new StringBuilder();
            // Concatenates all the codes in a string separated by a newline
            while (data.moveToNext()) {
                codes.append(data.getString(1)); // column 1:code
                if (!data.isLast())
                    codes.append("\n");
            }
            shareTo(codes.toString(), activity);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method gets all the data from the table with the column codes and add it to a ArrayList.
     * The ArrayList will be handed over to a ListAdapter and the listview takes this ListAdapter.
     * Then set an onItemClickListener to the ListView.
     */
    private void showDataInListView() {
        Cursor data = historyDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(data.getString(1)); //column 0 = id; column 1 = code
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        historyListView.setAdapter(adapter);

        historyListView.setOnItemClickListener((adapterView, view, i, l) -> {
            String code = adapterView.getItemAtPosition(i).toString();
            try {
                Cursor data1 = historyDatabaseHelper.getItemID(code);
                int itemID = -1;
                while (data1.moveToNext()) {
                    itemID = data1.getInt(0);
                }
                if (itemID > -1) {
                    Intent historyDetails = new Intent(HistoryActivity.this, HistoryDetailsActivity.class);
                    historyDetails.putExtra("id", itemID);
                    historyDetails.putExtra("code", code);
                    startActivity(historyDetails);
                } else {
                    Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_not_in_database), Toast.LENGTH_LONG).show();
                }
                //Catch Exception for DataMatrix codes
            } catch (SQLException e) {
                Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_sqlexception), Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * This method will call the resetDatabase method of the DatabaseHelper
     */
    private void resetDatabase() {
        historyDatabaseHelper.resetDatabase();
        super.finish();
    }
}
