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


import de.t_dankworth.secscanqr.R;

public class GeneratorListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private int lastPosition = -1;
    private String generators[];

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView tvGenerator;
    }


    /**
     * Constructor for GeneratorListAdapter
     * @param context
     * @param resource
     * @param generators
     */
    public GeneratorListAdapter(Context context, int resource, String generators[]) {
        super(context, resource, generators);
        this.context = context;
        this.resource = resource;
        this.generators = generators;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String generator = generators[position];

        final View resultAnimation;
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvGenerator = (TextView) convertView.findViewById(R.id.listViewRowItem);

            resultAnimation = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            resultAnimation = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.load_down_animation : R.anim.load_up_animation);
        resultAnimation.startAnimation(animation);
        lastPosition = position;

        holder.tvGenerator.setText(generator);

        return convertView;

    }
}
