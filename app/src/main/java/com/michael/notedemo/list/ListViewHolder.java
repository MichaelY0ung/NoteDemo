package com.michael.notedemo.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.michael.notedemo.R;

/**
 * Createdby Michael on 2017/3/30.
 */

public class ListViewHolder extends RecyclerView.ViewHolder{
    CheckBox select_check;
    EditText editText;
    ImageButton delete_bt;
    public ListViewHolder(View itemView) {
        super(itemView);
        select_check = (CheckBox)itemView.findViewById(R.id.list_select);
        editText = (EditText)itemView.findViewById(R.id.list_edit);
        delete_bt = (ImageButton)itemView.findViewById(R.id.delete_bt);
    }
}
