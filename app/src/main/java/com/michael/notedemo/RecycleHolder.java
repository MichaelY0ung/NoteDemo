package com.michael.notedemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

public class RecycleHolder extends RecyclerView.ViewHolder {
    SwipeMenuLayout swipeMenuLayout;
    LinearLayout rv_item;
    ImageView titleImage;
    TextView content;
    Button bt_Top;
    Button bt_Done;
    Button bt_Delete;
    CheckBox checkBox;
    public RecycleHolder(View itemView) {
        super(itemView);
        rv_item = (LinearLayout)itemView.findViewById(R.id.rv_item);
        content = (TextView)itemView.findViewById(R.id.tv);
        titleImage = (ImageView)itemView.findViewById(R.id.image);
        bt_Top = (Button)itemView.findViewById(R.id.btnTop);
        bt_Done = (Button)itemView.findViewById(R.id.btnDone);
        bt_Delete = (Button)itemView.findViewById(R.id.btnDelete);
        checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
        swipeMenuLayout = (SwipeMenuLayout)itemView.findViewById(R.id.swipeLayout);
    }
}
