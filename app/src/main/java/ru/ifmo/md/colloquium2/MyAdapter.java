package ru.ifmo.md.colloquium2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class MyAdapter extends BaseAdapter {
    private final ArrayList<String> data;
    private final ArrayList<Integer> votes;
    public int totalVotes;

    public MyAdapter(ArrayList<String> data) {
        this.data = data;
        this.votes = new ArrayList<Integer>(data.size());
        totalVotes = 0;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int ind) {
        return data.get(ind);
    }

    @Override
    public long getItemId(int ind) {
        return ind;
    }

    @Override
    public View getView(final int ind, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        }
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView numOfVotes = (TextView) view.findViewById(R.id.numberOfVotes);
        TextView percent = (TextView) view.findViewById(R.id.percent);
        name.setText(getItem(ind));
        numOfVotes.setText(votes.get(ind).toString());
        if(totalVotes == 0)
            percent.setText(" 0%");
        else
            percent.setText(" " + Integer.toString(100 * votes.get(ind) / totalVotes) + "%");

        return view;
    }

    public void add(String o) {
        data.add(o);
        votes.add(0);
        notifyDataSetChanged();
    }

    public void addVote(final int ind) {
        votes.set(ind, votes.get(ind) + 1);
        totalVotes++;
        notifyDataSetChanged();
    }
    public void zeroVote(final int ind) {
        votes.set(ind, 0);
        notifyDataSetChanged();
    }
}
