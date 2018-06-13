package com.worker.facedetector;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.worker.facedetector.helpers.ConstantsHelper;
import com.worker.facedetector.helpers.YesConfirmHelper;

import java.io.File;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
    }
    private RecyclerView nameList;
    private NameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("All users");

        nameList = findViewById(R.id.names);
        nameList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NameAdapter(this);
        nameList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.add_new) {
            askForNewName();
        }
        return super.onOptionsItemSelected(item);
    }

    private void askForNewName() {
        View promptsView = LayoutInflater.from(this).inflate(R.layout.promt_text, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView
                .findViewById(R.id.text_input);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String name = userInput.getText().toString();
                                if (name.trim().length() > 0) {
                                    File newUserPath = new File(ConstantsHelper.FACES_DIRECTORY + "/" + name);
                                    Toast.makeText(UserListActivity.this, newUserPath.mkdirs() ? "Created new user" : "Can't create user! ", Toast.LENGTH_LONG).show();
                                    adapter.reloadFolder();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reloadFolder();
    }

    static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private static class NameAdapter extends RecyclerView.Adapter {
        ArrayList<String> names = new ArrayList<>();
        private UserListActivity activity;

        NameAdapter(UserListActivity activity) {
            this.activity = activity;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.name_item, parent, false);

            return new EmptyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final String name = names.get(position);
            View v = holder.itemView;

            TextView nameTV = v.findViewById(R.id.nameTV);
            ImageButton removeBtn = v.findViewById(R.id.removeBtn);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, UserImagesActivity.class);
                    intent.putExtra("name", name);
                    activity.startActivity(intent);
                }
            });
            nameTV.setText(name);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YesConfirmHelper.confirm(activity, "Are you sure to delete this user?", new Runnable() {
                        @Override
                        public void run() {
                            File userPath = new File(ConstantsHelper.FACES_DIRECTORY + "/" + name);
                            deleteRecursive(userPath);
                            Toast.makeText(activity, "Deleted user" , Toast.LENGTH_LONG).show();
                            activity.adapter.reloadFolder();
                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        void reloadFolder() {
            names.clear();
            File folder = new File(ConstantsHelper.FACES_DIRECTORY);
            if (!folder.isDirectory()) {
                Toast.makeText(activity, folder.mkdirs() ? "Created face folder" : "Can't create face folder", Toast.LENGTH_LONG).show();
            }

            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    names.add(file.getName());
                }
            }
            this.notifyDataSetChanged();
        }
    }

}
