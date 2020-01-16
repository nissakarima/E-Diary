package com.example.fp.activity.main;

import com.example.fp.model.Note;

import java.util.List;

public interface HomeView {
    void showLoading();
    void hideLoading();
    void onGetResult(List<Note> notes);
    void onErrorLoading(String message);
}
