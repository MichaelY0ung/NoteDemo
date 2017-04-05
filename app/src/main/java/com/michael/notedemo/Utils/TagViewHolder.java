package com.michael.notedemo.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.michael.notedemo.R;

/**
 * Created by Michael on 2017/3/20.
 */

public class TagViewHolder extends RecyclerView.ViewHolder{
    TextView tag;
    ImageButton delete;
    public TagViewHolder(View itemView) {
        super(itemView);
        tag = (TextView)itemView.findViewById(R.id.tag_item);
        delete = (ImageButton)itemView.findViewById(R.id.tag_delete);
    }
}
