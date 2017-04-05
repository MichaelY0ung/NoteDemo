package com.michael.notedemo.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Michael on 2017/3/20.
 */

public class TagAdapter extends RecyclerView.Adapter<TagViewHolder>{
    private LayoutInflater mInflater;
    private int mLayoutId;
    private Context mContext;
    private ArrayList<String> mList;
    private boolean showDelete= false;

    public TagAdapter(Context context, ArrayList<String> list,int layoutId){
        mContext = context;
        mList = list;
        mLayoutId = layoutId;
        mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagViewHolder(mInflater.inflate(mLayoutId,parent,false));
    }
    @Override
    public void onBindViewHolder(final TagViewHolder holder, final int position) {
        if(position<getItemCount()-1&&null!=mList) {
            holder.tag.setText(mList.get(position));
            holder.tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnTagListener) {
                        mOnTagListener.onEdit(holder.getAdapterPosition());
                    }
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnTagListener) {
                        mOnTagListener.onDel(holder.getAdapterPosition());
                    }
                }
            });
            if (getShowDelete()) {
                holder.delete.setVisibility(View.VISIBLE);
            } else {
                holder.delete.setVisibility(View.GONE);
            }
        }
        else if(holder.getAdapterPosition()<10){
            holder.tag.setText("添加标签");
            holder.tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mOnTagListener){
                        mOnTagListener.onAdd();
                    }
                }
            });
            holder.delete.setVisibility(View.GONE);
            if (getShowDelete()) {
                holder.tag.setVisibility(View.GONE);
            } else {
                holder.tag.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.tag.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        Log.d("11111",getItemCount()+"");
    }

    @Override
    public int getItemCount() {
        return null!=mList?mList.size()+1:1;
    }
    public onTagListener mOnTagListener;
    public onTagListener getOnDelListener(){
        return mOnTagListener;
    }
    public void setOnDelListener(onTagListener mOnTagListener) {
        this.mOnTagListener = mOnTagListener;
    }

    public interface onTagListener {
        void onDel(int pos);
        void onEdit(int pos);
        void onAdd();
    }
    public boolean getShowDelete(){
        Log.d("11111","get"+showDelete);
        return showDelete;
    }
    public void setShowDelete(boolean status){
        showDelete = status;
        Log.d("11111","set"+showDelete);
    }
}
