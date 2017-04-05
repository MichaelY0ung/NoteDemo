package com.michael.notedemo.list;

/**
 * Created by Michael on 2017/3/30.
 */

public class ListBean{
    private boolean status;
    private String listContent;
    private int itemPos;

    public int getItemPos() {
        return itemPos;
    }

    public void setItemPos(int itemPos) {
        this.itemPos = itemPos;
    }

    public ListBean(int itemPos){
        this.status = true;
        this.listContent = null;
        this.itemPos = itemPos;
    }
    public ListBean(String content,int intStatus){
        this.listContent = content;
        if(intStatus==0){
            this.status = true;
        }
        else{
            this.status = false;
        }
    }
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getListContent() {
        return listContent;
    }

    public void setListContent(String listContent) {
        this.listContent = listContent;
    }

}
