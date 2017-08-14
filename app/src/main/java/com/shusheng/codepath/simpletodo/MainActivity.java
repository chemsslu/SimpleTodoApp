package com.shusheng.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shusheng.codepath.simpletodo.adapter.TasksAdapter;
import com.shusheng.codepath.simpletodo.data.Task;

import org.parceler.Parcels;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  ArrayList<Task> tasksList;
  TasksAdapter tasksAdapter;
  ListView tasksListView;

  private final int REQUEST_CODE = 10;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tasksList = new ArrayList<>(SQLite.select().from(Task.class).queryList());

    tasksAdapter = new TasksAdapter(this, tasksList);
    tasksListView = (ListView) findViewById(R.id.tasks_list_view);
    tasksListView.setAdapter(tasksAdapter);
    setupListViewListener();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  private void setupListViewListener() {
    tasksListView.setOnItemLongClickListener(
        new AdapterView.OnItemLongClickListener() {
          @Override
          public boolean onItemLongClick(AdapterView<?> adapter,
                                         View item, int pos, long id) {

            Task selectedTask = (Task)tasksListView.getItemAtPosition(pos);
            selectedTask.delete();
            tasksList.remove(pos);
            tasksAdapter.notifyDataSetChanged();
            return true;
          }
        });

    tasksListView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapter,
                                  View item, int pos, long id) {
            Intent i = new Intent(MainActivity.this, EditItemActivity.class);
            i.putExtra("task", Parcels.wrap((Task) tasksListView.getItemAtPosition(pos)));
            i.putExtra("pos", pos);
            startActivityForResult(i, REQUEST_CODE);
          }
        }
    );
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
      Task task = Parcels.unwrap(data.getParcelableExtra("task"));
      int pos = data.getExtras().getInt("pos", 0);
      if (pos == tasksList.size()) {
        task.save();
        tasksList.add(task);
      } else {
        task.save();
        Task oldTask = tasksList.get(pos);
        oldTask.setTitle(task.getTitle());
        oldTask.setDueDate(task.getDueDate());
      }
      tasksAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.miAddTask:
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("pos", tasksList.size());
        startActivityForResult(i, REQUEST_CODE);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
