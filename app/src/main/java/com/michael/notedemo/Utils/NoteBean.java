package com.michael.notedemo.Utils;

import java.io.Serializable;

/**
 * Created by Michael on 2017/3/17.
 */

public class NoteBean implements Serializable{
    private int id;
    private String title;
    private String content;
    private String tag;
    private boolean isSelected;
    private int type;
    private String uri;
    private String list_content;
    private String picinfo;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getListContent() {
        return list_content;
    }

    public void setListContent(String list_content) {
        this.list_content = list_content;
    }

    public NoteBean(){
        this.id = 0;
        this.title = null;
        this.content = null;
        this.tag = null;
        this.isSelected = false;
        this.type = 0;
        this.list_content = null;
        this.uri = null;
        this.picinfo = null;
    }
    public NoteBean(NoteBean noteBean) {
        this.id = noteBean.getId();
        this.title = noteBean.getTitle();
        this.content = noteBean.getContent();
        this.tag = noteBean.getTag();
        this.isSelected = noteBean.isSelected();
        this.type = noteBean.getType();
        this.list_content = noteBean.getListContent();
        this.uri = noteBean.getUri();
        this.picinfo = noteBean.getPicinfo();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPicinfo() {
        return picinfo;
    }

    public void setPicinfo(String picinfo) {
        this.picinfo = picinfo;
    }
}
