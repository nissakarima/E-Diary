package com.example.fp.activity.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fp.R;
import com.example.fp.activity.editor.EditorActivity;
import com.example.fp.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements HomeView{

    private static final int INTENT_EDIT = 200;
    private static final int INTENT_ADD = 100;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;

    HomePresenter presenter;
    HomeAdapter adapter;
    HomeAdapter.ItemClickListener itemClickListener;

    List<Note> note;
    TextView txtEmail;
    ImageView imgFoto;

    String email, foto;

//    Menu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtEmail = findViewById(R.id.email);
        imgFoto = findViewById(R.id.foto);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent inten = getIntent();
        email = inten.getStringExtra("email");
        foto = inten.getStringExtra("foto");
        txtEmail.setText(email);
        Picasso.get().load(foto).into(imgFoto);

        fab = findViewById(R.id.add);
        fab.setOnClickListener(view ->
            startActivityForResult(new Intent(this, EditorActivity.class), INTENT_ADD)
        );

        presenter = new HomePresenter(this);
        presenter.getData();

        swipeRefresh.setOnRefreshListener(
                () -> presenter.getData()
        );

        itemClickListener = ((view, position) -> {
            int id = note.get(position).getId();
            String title = note.get(position).getTitle();
            String notes = note.get(position).getNote();
            int color = note.get(position).getColor();

            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("note", notes);
            intent.putExtra("color", color);
            startActivityForResult(intent, INTENT_EDIT);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MainActivity main = new MainActivity();
        if (item.getItemId() == R.id.log_out){
            main.Logout();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==INTENT_ADD && resultCode == RESULT_OK){
            presenter.getData(); //reload data
        } else if(requestCode ==INTENT_EDIT && resultCode == RESULT_OK){
            presenter.getData(); //reload data
        }
    }

    @Override
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onGetResult(List<Note> notes) {
        adapter = new HomeAdapter(this, notes, itemClickListener);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        note = notes;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
