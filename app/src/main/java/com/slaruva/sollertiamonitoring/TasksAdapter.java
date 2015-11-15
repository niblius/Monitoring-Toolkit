package com.slaruva.sollertiamonitoring;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by enterix on 11/14/2015.
 */
public class TasksAdapter extends BaseAdapter {
    private List<Task> tasks;

    TasksAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return tasks.get(position).getRowView(parent.getContext(), convertView);
    }
}
