package com.example.fp.activity.editor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fp.R;
import com.example.fp.api.ApiInterface;
import com.thebluealliance.spectrum.SpectrumPalette;


public class EditorActivity extends AppCompatActivity implements EditorView{

    EditText judul, notes;
    ProgressDialog progressDialog;
    SpectrumPalette palette;

    EditorPresenter presenter;
    ApiInterface apiInterface;

    int color, id;
    String title, note;
    Menu actionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        judul = findViewById(R.id.title);
        notes = findViewById(R.id.note);
        palette = findViewById(R.id.palette);


        palette.setOnColorSelectedListener(
                clr -> color = clr
        );

        //progress diaolog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");

        presenter = new EditorPresenter(this);
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        title = intent.getStringExtra("title");
        note = intent.getStringExtra("note");
        color = intent.getIntExtra("color",0);

        setDataFromIntentExtra();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        actionMenu = menu;

        if (id != 0){
            actionMenu.findItem(R.id.edit).setVisible(true);
            actionMenu.findItem(R.id.delete).setVisible(true);
            actionMenu.findItem(R.id.update).setVisible(false);
            actionMenu.findItem(R.id.save).setVisible(false);
        } else {
            actionMenu.findItem(R.id.edit).setVisible(false);
            actionMenu.findItem(R.id.delete).setVisible(false);
            actionMenu.findItem(R.id.update).setVisible(false);
            actionMenu.findItem(R.id.save).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = judul.getText().toString().trim();
        String note = notes.getText().toString().trim();
        int color = this.color;

        switch (item.getItemId()){
            case R.id.save:
                //save
                if (title.isEmpty()){
                    judul.setError("Please fill the title");
                } else if (note.isEmpty()){
                    notes.setError("Please fill the note");
                } else {
                    presenter.saveNote(title, note, color);
                }
                return true;

            case R.id.edit:
                editMode();
                actionMenu.findItem(R.id.edit).setVisible(false);
                actionMenu.findItem(R.id.delete).setVisible(false);
                actionMenu.findItem(R.id.update).setVisible(true);
                actionMenu.findItem(R.id.save).setVisible(false);
                return true;

            case R.id.update:
                //update
                if (title.isEmpty()){
                    judul.setError("Please fill the title");
                } else if (note.isEmpty()){
                    notes.setError("Please fill the note");
                } else {
                    presenter.updateNote(id,title, note, color);
                }
                return true;

            case R.id.delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setNegativeButton("Yes",
                        (dialog, which) -> {dialog.dismiss();presenter.deleteNote(id);});
                alertDialog.setPositiveButton("Cancel",
                        (dialog, which) -> dialog.dismiss());

                alertDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }

    @Override
    public void onRequestSuccess(String message) {
        Toast.makeText(EditorActivity.this,message,Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestError(String message) {
        Toast.makeText(EditorActivity.this,message,Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setDataFromIntentExtra() {
        if (id != 0){
            judul.setText(title);
            notes.setText(note);
            palette.setSelectedColor(color);

            getSupportActionBar().setTitle("Update Note");
            readMode();
        } else {
            //default color
            palette.setSelectedColor(getResources().getColor(R.color.white));
            color = getResources().getColor(R.color.white);
            editMode();
        }
    }

    private void editMode() {
        judul.setFocusableInTouchMode(true);
        notes.setFocusableInTouchMode(true);
        palette.setEnabled(true);
    }

    private void readMode() {
        judul.setFocusableInTouchMode(false);
        notes.setFocusableInTouchMode(false);
        judul.setFocusable(false);
        notes.setFocusable(false);
        palette.setEnabled(false);
    }
}
