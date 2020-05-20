package de.t_dankworth.secscanqr.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import de.t_dankworth.secscanqr.R;

/**
 * Created by Thore Dankworth
 * Last Update: 20.05.2020
 * Last Update by Thore Dankworth
 *
 * This class represents the ListAdapter for the RecyclerView.
 */


public class HistoryAdapter extends ListAdapter<HistoryEntity, HistoryAdapter.HistoryHolder> {
    private OnItemClickListener listener;

    public HistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Check if Items and Contents are the same.
     */
    private static final DiffUtil.ItemCallback<HistoryEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<HistoryEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull HistoryEntity oldItem, @NonNull HistoryEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull HistoryEntity oldItem, @NonNull HistoryEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        HistoryEntity currentEntry = getItem(position);
        holder.tvHistoryDate.setText(currentEntry.getDate());
        holder.tvHistoryFormat.setText(currentEntry.getFormat());
        holder.tvHistoryCode.setText(currentEntry.getInformation());
    }

    public HistoryEntity getHistoryEntryAt(int position){
        return getItem(position);
    }

    /**
     * ViewHolder for RecyclerView which is responsible for a single item in the RecyclerView.
     */
    class HistoryHolder extends RecyclerView.ViewHolder{
        private TextView tvHistoryDate;
        private TextView tvHistoryFormat;
        private TextView tvHistoryCode;

        public HistoryHolder(View itemView){
            super(itemView);
            tvHistoryCode = itemView.findViewById(R.id.tvHistoryCode);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryFormat = itemView.findViewById(R.id.tvHistoryFormat);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position));
                    }

                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(HistoryEntity historyEntry);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
