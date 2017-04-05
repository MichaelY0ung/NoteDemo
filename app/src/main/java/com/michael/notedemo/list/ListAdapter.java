package com.michael.notedemo.list;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

/**
 * Created by Michael on 2017/3/30.
 */

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder>{
    private final Context mContext;
    private final ArrayList<ListBean> mDatas;
    private final int mLayoutId;
    private final LayoutInflater mInflater;
    private ListListener listListener;
    private int focusPos = -1;
    private boolean checkStatus = true;

    public ListAdapter(Context mContext, ArrayList<ListBean> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(mInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, final int position) {
        holder.editText.setText(mDatas.get(position).getListContent());
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listListener!=null){
                    listListener.onUpdate(holder.getAdapterPosition(),holder.editText.getText().length()>0?holder.editText.getText().toString():null);
                }
            }
        });
        holder.select_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listListener!=null&&checkStatus){
                    Log.d("22222","test1");
                    listListener.onCheck(holder.getAdapterPosition());
                }
            }
        });
        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if(hasFocus){
                    holder.delete_bt.setVisibility(View.VISIBLE);
                }
                else{
                   holder.delete_bt.setVisibility(View.INVISIBLE);
               }
            }
        });
        holder.delete_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listListener!=null){
                    listListener.onDel(holder.getAdapterPosition());
                }
            }
        });
        if(mDatas.get(position).isStatus()){
            //holder.editText.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            checkStatus = false;
            holder.select_check.setChecked(false);
            checkStatus = true;
        }
        else{
            checkStatus = false;
            holder.editText.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG);
            holder.select_check.setChecked(true);
            checkStatus = true;
        }
        if(focusPos==position){
            holder.editText.setFocusable(true);
            focusPos = -1;
        }
    }

    @Override
    public int getItemCount() {
        return null!=mDatas?mDatas.size():0;
    }
    public interface ListListener{
        //void onDel(int pos);
        void onCheck(int pos);
        void onUpdate(int pos,String text);
        void onDel(int pos);
    }
    public ListListener getListListener(){
        return listListener;
    }
    public void setListListener(ListListener listListener){
        this.listListener = listListener;
    }
    public void setFocus(int pos){
        this.focusPos = pos;
    }

}
