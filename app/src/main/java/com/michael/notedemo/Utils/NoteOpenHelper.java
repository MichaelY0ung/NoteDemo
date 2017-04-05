package com.michael.notedemo.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 2017/3/17.
 */

public class NoteOpenHelper extends SQLiteOpenHelper{
    public NoteOpenHelper(Context context) {
        super(context, "note.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists note_content(_id integer primary key autoincrement,title varchar(40),content varchar(255),visible varchar(10),tags varchar(100),datatype integer,listcontent varchar(255),picuri varchar(100),picinfo varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
