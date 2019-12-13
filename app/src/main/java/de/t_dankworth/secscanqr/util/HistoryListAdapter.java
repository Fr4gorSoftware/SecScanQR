package de.t_dankworth.secscanqr.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.t_dankworth.secscanqr.R;

/**
 * Created by Thore Dankworth
 * Last Update: 13.12.2019
 * Last Update by Thore Dankworth
 *
 * This class is the ListAdapter for the HistoryActivity
 */

public class HistoryListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private int lastPosition = -1;
    private ArrayList<String> scans;


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView tvScanned;
    }


    /**
     * Constructor for GeneratorListAdapter
     * @param context
     * @param resource
     * @param scans
     */
    public HistoryListAdapter(Context context, int resource, ArrayList<String> scans) {
        super(context, resource, scans);
        this.context = context;
        this.resource = resource;
        this.scans = scans;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String scan = scans.get(position);

        final View resultAnimation;
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvScanned = (TextView) convertView.findViewById(R.id.listViewRowItem);

            resultAnimation = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resultAnimation = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.load_down_animation : R.anim.load_up_animation);
        resultAnimation.startAnimation(animation);
        lastPosition = position;

        holder.tvScanned.setText(scan);

        return convertView;

    }
}
