package ru.ifmo.md.colloquium2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView listView;
    ArrayList<String> arrayList;
    ArrayList<Integer> votes;
    MyAdapter adapter;
    DBHelper helper;
    SQLiteDatabase db;
    Cursor c;
    boolean voting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<String>();
        votes = new ArrayList<Integer>();
        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        listView = (ListView) findViewById(R.id.listView);
        voting = false;

        c = db.query("CANDIDATES", null, null, null, null, null, null);
        adapter = new MyAdapter(arrayList);
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            do {
                adapter.add(c.getString(nameColIndex));
            } while (c.moveToNext());
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int ind, long l) {
                String candidateName = ((TextView) view.findViewById(R.id.name)).getText().toString();
                if(voting) {
                    adapter.addVote(ind);
                    Toast.makeText(getApplicationContext(), "You voted for " + candidateName, Toast.LENGTH_SHORT).show();
                } else {
                    LayoutInflater layoutInflater = LayoutInflater.from(listView.getContext());
                    View addCandidateDialog = layoutInflater.inflate(R.layout.activity_add_candidate_dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(listView.getContext());
                    final EditText name = (EditText) addCandidateDialog.findViewById(R.id.name);
                    name.setText(candidateName);
                    builder.setView(addCandidateDialog)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ContentValues cv = new ContentValues();
                                    cv.put("name", name.getText().toString());
                                    db.update("CANDIDATES", cv, "id=?", new String[]{Integer.toString(ind + 1)});
                                    arrayList.set(ind, name.getText().toString());
                                    adapter.notifyDataSetChanged();
                                }
                            }).setNegativeButton("Cancel", null)
                            .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    db.delete("CANDIDATES", "id=" + Integer.toString(ind + 1), null);
                                    arrayList.remove(ind);
                                    adapter.notifyDataSetChanged();
                                }
                            }).create().show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        if(voting) {
            Toast.makeText(getApplicationContext(), "Can’t change candidates list while voting", Toast.LENGTH_SHORT).show();
            return;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addCandidateDialog = layoutInflater.inflate(R.layout.activity_add_candidate_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText name = (EditText) addCandidateDialog.findViewById(R.id.name);
        builder.setView(addCandidateDialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues cv = new ContentValues();
                        cv.put("name", name.getText().toString());
                        db.insert("CANDIDATES", null, cv);
                        adapter.add(name.getText().toString());
                    }
                }).setNegativeButton("Cancel", null)
                .create().show();
    }
    public void toggleVoting(View view) {
        Button sender = (Button) view;
        voting = !voting;
        sender.setText(voting ? "Stop voting" : "Start voting");
    }

    public void discard(View view) {
        for(int i = 0; i < arrayList.size(); i++)
            adapter.zeroVote(i);
        adapter.totalVotes = 0;
    }
}
