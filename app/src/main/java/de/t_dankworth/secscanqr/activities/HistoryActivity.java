package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.DatabaseHelper;

/**
 * Created by Thore Dankworth
 * Last Update: 05.09.2017
 * Last Update by Thore Dankworth
 *
 * This class is the HistoryActivity and lists all scanned qr-codes
 */


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "History";
    DatabaseHelper historyDatabaseHelper;
    private ListView historyListView;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyDatabaseHelper = new DatabaseHelper(this);
        historyListView = (ListView) findViewById(R.id.listView);

        showDataInListView();


    }

    /**
     * Refreshes the History after comming back from the HistoryDetailsActivity
     */
    @Override
    public void onResume(){
        super.onResume();

        showDataInListView();
    }

    /**
     * This method gets all the data from the table with the column codes and add it to a ArrayList.
     * The ArrayList will be handed over to a ListAdapter and the listview takes this ListAdapter.
     * Then set an onItemClickListener to the ListView.
     */
    private void showDataInListView(){
        Cursor data = historyDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1)); //column 0 = id; column 1 = code
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        historyListView.setAdapter(adapter);

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String code = adapterView.getItemAtPosition(i).toString();

                Cursor data = historyDatabaseHelper.getItemID(code);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Intent historyDetails = new Intent(HistoryActivity.this, HistoryDetailsActivity.class);
                    historyDetails.putExtra("id", itemID);
                    historyDetails.putExtra("code", code);
                    startActivity(historyDetails);
                } else {
                    Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_generate), Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
