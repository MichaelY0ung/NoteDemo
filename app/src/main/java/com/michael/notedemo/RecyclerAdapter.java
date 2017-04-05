package com.michael.notedemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.michael.notedemo.Utils.NoteBean;

import java.util.ArrayList;

/**
 * Created by Michael on 2017/2/21.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecycleHolder> {

    private Context mContext;
    private ArrayList<NoteBean> mDatas;
    private int mLayoutId;
    private LayoutInflater mInflater;
    private boolean isSelected = false;
    private boolean swipeEnable = true;

    public boolean isShowCheck() {
        return showCheck;
    }

    public void setShowCheck(boolean showCheck) {
        this.showCheck = showCheck;
    }

    private boolean showCheck = false;

    public RecyclerAdapter(Context mContext, ArrayList<NoteBean> mDatas, int mLayoutId) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mLayoutId = mLayoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecycleHolder(mInflater.inflate(mLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleHolder holder, final int position) {
        holder.swipeMenuLayout.setSwipeEnable(swipeEnable);
        holder.content.setText(mDatas.get(position).getTitle());
        holder.rv_item.setBackgroundResource(R.drawable.touch_background);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSelected = isChecked;
            }
        });
        holder.rv_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!isShowCheck()){
                    setShowCheck(true);
                    if(null!=mOnSwipeListener){
                        mOnSwipeListener.onEdit(holder.getAdapterPosition());
                    }
                }
                return true;//true 会阻断click的响应
            }
        });
        switch (mDatas.get(position).getType()){
            case 0:
                holder.titleImage.setImageResource(R.drawable.ic_event_note_red_800_36dp);
                holder.bt_Done.setVisibility(View.GONE);
                break;
            case 1:
                holder.titleImage.setImageResource(R.drawable.ic_view_list_deep_purple_800_36dp);
                break;
            case 2:
                holder.titleImage.setImageResource(R.drawable.ic_color_lens_yellow_800_36dp);
                holder.bt_Done.setVisibility(View.GONE);
                break;
            case 3:
                holder.titleImage.setImageResource(R.drawable.ic_group_work_light_green_800_36dp);
                break;
            default:
                holder.titleImage.setImageResource(R.drawable.ic_event_note_red_800_36dp);
                holder.bt_Done.setVisibility(View.GONE);
                break;
        }
        holder.bt_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"Done"+position,Toast.LENGTH_SHORT).show();
                Log.d("TAG","Done onClick() called with: v = [" + v + "]");
                if(null!=mOnSwipeListener){
                    mOnSwipeListener.onDone(holder.getAdapterPosition());
                }
            }
        });
        holder.bt_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSwipeListener){
                    mOnSwipeListener.onDel(holder.getAdapterPosition());
                }
            }
        });

        holder.rv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showCheck){
//                    mDatas.get(holder.getAdapterPosition()).setSelected(!mDatas.get(holder.getAdapterPosition()).isSelected());
                    if(null!=mOnSwipeListener){
                        mOnSwipeListener.onCheck(holder.getAdapterPosition());
                    }
                }
                else{
                    if(null!=mOnSwipeListener){
                        mOnSwipeListener.onIntent(holder.getAdapterPosition());
                    }
                }

            }
        });
        holder.bt_Top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnSwipeListener){
                    mOnSwipeListener.onTop(holder.getAdapterPosition());
                }
            }
        });
        if(isShowCheck()){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(mDatas.get(holder.getAdapterPosition()).isSelected());
        }
        else{
            holder.checkBox.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return null!=mDatas?mDatas.size():0;
    }
    public onSwipeListener mOnSwipeListener;
    public onSwipeListener getOnDelListener(){
        return mOnSwipeListener;
    }
    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.swipeEnable = swipeEnable;
    }

    public interface onSwipeListener {
        void onDel(int pos);
        void onDone(int pos);
        void onTop(int pos);
        void onEdit(int pos);
        void onCheck(int pos);
        void onIntent(int pos);
    }

}
