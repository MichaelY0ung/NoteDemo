package com.michael.notedemo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Michael on 2017/3/17.
 */

public class NoteDao {
    private NoteOpenHelper mNoteOpenHelper;
    public NoteDao(Context context) {
        mNoteOpenHelper = new NoteOpenHelper(context);
    }
    public boolean add(NoteBean bean){
        SQLiteDatabase db = mNoteOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        switch (bean.getType()){
            case 0:
                values.put("title",bean.getTitle());
                values.put("content",bean.getContent());
                values.put("visible","visible");
                if(bean.getTag()!=null) {
                    values.put("tags", bean.getTag());
                }
                values.put("datatype",0);
                break;
            case 1:
                values.put("title",bean.getTitle());
                values.put("listcontent",bean.getListContent());
                values.put("visible","visible");
                values.put("datatype",1);
                break;
            case 2:
                values.put("title",bean.getTitle());
                values.put("picuri",bean.getUri());
                values.put("visible","visible");
                values.put("datatype",2);
                values.put("picinfo",bean.getPicinfo());
                break;
            case 3:
                values.put("title",bean.getTitle());
                if(bean.getContent()!=null){
                    values.put("content",bean.getContent());
                }
                if(bean.getListContent()!=null){
                    values.put("listcontent",bean.getListContent());
                }
                if(bean.getUri()!=null){
                    values.put("picuri",bean.getUri());
                    values.put("picinfo",bean.getPicinfo());
                }
                if(bean.getTag()!=null) {
                    values.put("tags", bean.getTag());
                }
                values.put("visible","visible");
                values.put("datatype",3);
                break;
        }
        long result = db.insert("note_content",null,values);
        db.close();
        return result!= -1 ? true:false;
    }
    public int del(int id){
        SQLiteDatabase db = mNoteOpenHelper.getReadableDatabase();
        //int result = db.delete("note_content","_id = ?",new String[]{String.valueOf(id)});
        ContentValues values = new ContentValues();
        values.put("visible","invisible");
        int result = db.update("note_content",values,"_id = ?",new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
    public int update(NoteBean bean){
        SQLiteDatabase db = mNoteOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        switch (bean.getType()){
            case 0:
                values.put("title",bean.getTitle());
                values.put("content",bean.getContent());
                if(bean.getTag()!=null) {
                    values.put("tags", bean.getTag());
                }
                values.put("visible","visible");
                break;
            case 1:
                values.put("title",bean.getTitle());
                values.put("listcontent",bean.getListContent());
                values.put("visible","visible");
                break;
            case 2:
                values.put("title",bean.getTitle());
                values.put("picuri",bean.getUri());
                values.put("visible","visible");
                values.put("picinfo",bean.getPicinfo());
                break;
            case 3:
                values.put("title",bean.getTitle());
                if(bean.getContent()!=null){
                    values.put("content",bean.getContent());
                }
                if(bean.getListContent()!=null){
                    values.put("listcontent",bean.getListContent());
                }
                if(bean.getUri()!=null){
                    values.put("picuri",bean.getUri());
                }
                if(bean.getTag()!=null) {
                    values.put("tags", bean.getTag());
                }
                values.put("visible","visible");
                break;
        }
        Log.d("11111","update"+values.get("title"));
        int result = db.update("note_content",values,"_id = ?",new String[]{String.valueOf(bean.getId())});
        Log.d("11111",result+"result");
        db.close();
        return result;
    }
//    public int exchange(NoteBean formar,NoteBean latter){
//        SQLiteDatabase db = mNoteOpenHelper.getReadableDatabase();
//        ContentValues values1 = new ContentValues();
//        ContentValues values2 = new ContentValues();
//        values1.put("title",latter.getTitle());
//        values1.put("content",latter.getContent());
//        values1.put("tags",latter.getTag());
//        values2.put("title",formar.getTitle());
//        values2.put("content",formar.getContent());
//        values2.put("tags",formar.getTag());
//        Log.d("11111",formar.getTag());
//        int result1 = db.update("note_content",values1,"itemId = ?",new String[]{String.valueOf(formar.getId())});
//        int result2 = db.update("note_content",values2,"_id = ?",new String[]{String.valueOf(latter.getId())});
//        values1.remove("_id");
//        values1.put("_id",latter.getId());
//        db.close();
//        return false!=((result1>0)&&(result2>0))?1:0;
//    }
    public ArrayList<NoteBean> queryAll(){
        ArrayList<NoteBean> list = new ArrayList<NoteBean>();
        SQLiteDatabase db = mNoteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("note_content",new String[]{"_id","title","content","tags","datatype","listcontent","picuri","picinfo"},"visible=?",new String[]{"visible"},null,null,null);
        Log.d("11111","query"+cursor.getCount());
        if(cursor != null && cursor.getCount() >0){//判断cursor中是否存在数据

            //循环遍历结果集，获取每一行的内容
            while(cursor.moveToNext()){//条件，游标能否定位到下一行
                //获取数据
                NoteBean bean = new NoteBean();
                bean.setId(cursor.getInt(0));
                bean.setTitle(cursor.getString(1));
                bean.setType(cursor.getInt(4));
                switch (bean.getType()){
                    case 0:
                        bean.setContent(cursor.getString(2));
                        if(null!=cursor.getString(3)){
                            bean.setTag(cursor.getString(3));
                        }
                        break;
                    case 1:
                        bean.setListContent(cursor.getString(5));
                        break;
                    case 2:
                        bean.setUri(cursor.getString(6));
                        bean.setPicinfo(cursor.getString(7));
                        break;
                    case 3:
                        if(null!=cursor.getString(2)){
                            bean.setContent(cursor.getString(2));
                        }
                        if(null!=cursor.getString(3)){
                            bean.setTag(cursor.getString(3));
                        }
                        if(null!=cursor.getString(5)){
                            bean.setListContent(cursor.getString(5));
                        }
                        if (null != cursor.getString(6)) {
                            bean.setUri(cursor.getString(6));
                        }
                        if (null != cursor.getString(7)) {
                            bean.setUri(cursor.getString(7));
                        }
                        break;
                }

                if(null!=cursor.getString(3)) {
                     bean.setTag(cursor.getString(3));
                }
                bean.setSelected(false);
                list.add(bean);
            }
            cursor.close();//关闭结果集
        }
        //关闭数据库对象
        db.close();
        return list;
    }
}
