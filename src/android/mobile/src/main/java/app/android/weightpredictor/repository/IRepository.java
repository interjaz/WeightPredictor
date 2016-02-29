package app.android.weightpredictor.repository;

import android.content.Context;

import java.util.List;

import app.android.weightpredictor.database.SqliteAdapter;


public interface IRepository<T> {

    T get(String key);
    List<T> get();
    List<T> get(int take);
    List<T> get(int take, int skip);

    void insert(T item);
    void update(T item);
    void delete(String key);
}

