package com.slaruva.sollertiamonitoring;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.slaruva.sollertiamonitoring.ping.Ping;

import java.util.List;


public class TasksAdapter extends ArrayAdapter<Task> {
    private Context cont;
    TasksAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
        cont = context;
    }

    // returns number of types of tasks
    @Override
    public int getViewTypeCount() {
        return cont.getResources().getStringArray(R.array.tasks_array).length;
    }

    @Override
    public int getItemViewType (int position) {
        Task elem = getItem(position);
        if (elem instanceof Ping)
            return 0;
        else
            return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getRowView(getContext(), convertView, parent);
    }
}
