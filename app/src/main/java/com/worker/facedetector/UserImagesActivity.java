package com.worker.facedetector;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.worker.facedetector.helpers.ConstantsHelper;
import com.worker.facedetector.helpers.YesConfirmHelper;

import java.io.File;
import java.util.ArrayList;

public class UserImagesActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
    }
    private String name;
    private RecyclerView imagesList;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_images);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        getSupportActionBar().setTitle(name);
        initViews();
    }

    private void initViews() {
        imagesList = findViewById(R.id.images);
        imagesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserImagesActivity.ImageAdapter(this);
        imagesList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_name, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reloadFolder();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.add_new) {
            Intent intent = new Intent(this, GetNewFaceActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ImageAdapter extends RecyclerView.Adapter {
        ArrayList<File> images = new ArrayList<>();
        private UserImagesActivity activity;

        ImageAdapter(UserImagesActivity activity) {
            this.activity = activity;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_item, parent, false);

            return new EmptyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final File imageFile = images.get(position);
            View v = holder.itemView;

            ImageView imageIV = v.findViewById(R.id.imageIV);

            imageIV.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            ImageButton removeBtn = v.findViewById(R.id.removeBtn);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YesConfirmHelper.confirm(activity, "Are you sure to delete this image?", new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, imageFile.delete() ? "Deleted image" : "Can't delete image!", Toast.LENGTH_LONG).show();
                            activity.adapter.reloadFolder();
                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        void reloadFolder() {
            images.clear();
            File folder = new File(ConstantsHelper.FACES_DIRECTORY + "/" + activity.name);
            if (!folder.isDirectory()) {
                Toast.makeText(activity, folder.mkdirs() ? "Created face folder" : "Can't create face folder", Toast.LENGTH_LONG).show();
            }

            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".png")) {
                    images.add(file);
                }
            }
            this.notifyDataSetChanged();
        }
    }
}
