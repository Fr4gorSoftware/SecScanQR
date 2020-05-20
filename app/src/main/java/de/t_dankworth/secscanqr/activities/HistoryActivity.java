package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.DialogInterface;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.HistoryAdapter;
import de.t_dankworth.secscanqr.util.HistoryEntity;
import de.t_dankworth.secscanqr.util.GeneralHandler;
import de.t_dankworth.secscanqr.util.HistoryViewModel;

import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;


/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class is the HistoryActivity and communicates to the ViewModel to show all database entries and make changes to the database.
 */


public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "History";
    private GeneralHandler generalHandler;
    final Activity activity = this;
    private HistoryViewModel historyViewModel;
    private LiveData<List<HistoryEntity>> allItemsInHistory;
    private final HistoryAdapter adapter = new HistoryAdapter();

    /**
     * onCreate methods which holds the swipe functionality and the onClickListener
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_history);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        recyclerView.setAdapter(adapter);

        historyViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(HistoryViewModel.class);
        historyViewModel.getAllItemsInHistory().observe(this, new Observer<List<HistoryEntity>>() {
            @Override
            public void onChanged(List<HistoryEntity> historyEntities) {
                adapter.submitList(historyEntities);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT){
                    AlertDialog.Builder dialogBuilder;
                    dialogBuilder = new AlertDialog.Builder(HistoryActivity.this);
                    dialogBuilder.setMessage(R.string.delete_entry_history_dialog_message);
                    dialogBuilder.setPositiveButton(R.string.delete_entry_history_confirmation, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            historyViewModel.delete(adapter.getHistoryEntryAt(viewHolder.getAdapterPosition()));
                            Toast.makeText(activity, activity.getResources().getText(R.string.notice_deleted_from_database), Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogBuilder.setNegativeButton(R.string.delete_entry_history_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        }
                    });
                    dialogBuilder.show();
                } else {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    Intent intent = new Intent(activity, HistoryDetailsActivity.class);
                    intent.putExtra(HistoryDetailsActivity.EXTRA_FORMAT, adapter.getHistoryEntryAt(viewHolder.getAdapterPosition()).getFormat());
                    intent.putExtra(HistoryDetailsActivity.EXTRA_INFORMATION, adapter.getHistoryEntryAt(viewHolder.getAdapterPosition()).getInformation());
                    startActivity(intent);
                }

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HistoryEntity historyEntry) {
                Intent intent = new Intent(activity, HistoryDetailsActivity.class);
                intent.putExtra(HistoryDetailsActivity.EXTRA_FORMAT, historyEntry.getFormat());
                intent.putExtra(HistoryDetailsActivity.EXTRA_INFORMATION, historyEntry.getInformation());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.history_optionsmenu, menu);
        return true;
    }

    /**
     * functionality of the Buttons at the top right corner
     * @param item selected item in the menu
     * @return true or item itself
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.history_optionsmenu_delete:
                AlertDialog.Builder dialogBuilder;
                dialogBuilder = new AlertDialog.Builder(HistoryActivity.this);
                dialogBuilder.setMessage(R.string.delete_history_dialog_message);
                dialogBuilder.setPositiveButton(R.string.delete_history_dialog_confirmation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        historyViewModel.deleteAllInHistory();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.delete_history_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialogBuilder.show();
                return true;
            case R.id.history_optionsmenu_share:
                allItemsInHistory = historyViewModel.getAllItemsInHistory();
                String export = "";
                for(int i = 0; i < adapter.getItemCount(); i++){
                    export = export + "DATE:" + adapter.getHistoryEntryAt(i).getDate() + "\n";
                    export = export + "FORMAT:" + adapter.getHistoryEntryAt(i).getFormat() + "\n";
                    export = export + "INFO:" + adapter.getHistoryEntryAt(i).getInformation() + "\n";
                    export = export + "\n";
                }
                shareTo(export, activity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
