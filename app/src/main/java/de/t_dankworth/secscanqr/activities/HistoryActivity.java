package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.DatabaseHelper;
import de.t_dankworth.secscanqr.util.HistoryListAdapter;
import de.t_dankworth.secscanqr.util.GeneralHandler;

import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;


/**
 * Created by Thore Dankworth
 * Last Update: 12.12.2019
 * Last Update by Thore Dankworth
 *
 * This class is the HistoryActivity and lists all scanned qr-codes
 */


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "History";
    DatabaseHelper historyDatabaseHelper;
    private ListView historyListView;
    private GeneralHandler generalHandler;
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.history_optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.history_optionsmenu_delete) {
            AlertDialog.Builder dialogBuilder;
            dialogBuilder = new AlertDialog.Builder(HistoryActivity.this);
            dialogBuilder.setMessage(R.string.delete_history_dialog_message);
            dialogBuilder.setPositiveButton(R.string.delete_history_dialog_confirmation, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    resetDatabase();
                }
            });
            dialogBuilder.setNegativeButton(R.string.delete_history_dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialogBuilder.show();

        } else if (id == R.id.history_optionsmenu_share) {
            Cursor data = historyDatabaseHelper.getData();
            String codes = "";
            // Concatenates all the codes in a string separated by a newline
            while(data.moveToNext()){
                codes += data.getString(1); // column 1:code
                if (!data.isLast())
                    codes += "\n";
            }
            shareTo(codes, activity);
        }

        return super.onOptionsItemSelected(item);
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

        HistoryListAdapter listAdapter = new HistoryListAdapter(HistoryActivity.this, R.layout.listview_row, listData);

        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        historyListView.setAdapter(listAdapter);

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String code = adapterView.getItemAtPosition(i).toString();
                int itemID = -1;
                try{
                    code = code.replace("'", "''");
                    Cursor data = historyDatabaseHelper.getItemID(code);
                    while(data.moveToNext()){
                        itemID = data.getInt(0);
                    }
                    if(itemID > -1){
                        Intent historyDetails = new Intent(HistoryActivity.this, HistoryDetailsActivity.class);
                        historyDetails.putExtra("id", itemID);
                        startActivity(historyDetails);
                    } else {
                        Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_not_in_database), Toast.LENGTH_LONG).show();
                    }
                    //Catch Exception for DataMatrix codes
                } catch (SQLException e){
                    Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_sqlexception), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * This method will call the resetDatabase method of the DatabaseHelper
     */
    private void resetDatabase(){
        historyDatabaseHelper.resetDatabase();
        super.finish();
    }

}
