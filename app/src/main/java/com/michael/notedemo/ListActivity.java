package com.michael.notedemo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michael.notedemo.Utils.JsonUtils;
import com.michael.notedemo.Utils.NoteBean;
import com.michael.notedemo.Utils.NoteDao;
import com.michael.notedemo.Utils.NoteOpenHelper;
import com.michael.notedemo.list.ListAdapter;
import com.michael.notedemo.list.ListBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Michael on 2017/3/30.
 */

public class ListActivity extends AppCompatActivity{
    private RecyclerView rv_list;
    private LinearLayoutManager mLinearLayoutManager;
    private ListAdapter mListAdapter;
    private ArrayList<ListBean> mDatas;
    private ArrayList<ListBean> mDoneDatas;
    private FloatingActionButton add_fab;
    private android.support.v7.widget.Toolbar mToolbar;
    private TextView mToolbarTitile;
    private Context mContext;
    private int itemPos;
    private RecyclerView rv_done_list;
    private LinearLayout second_list;
    private CardView second_show_hide;
    private ImageView second_image;
    private TextView second_text;
    private boolean show_status;
    private ListAdapter mDoneListAdapter;
    private ListBean saveBean;
    private CoordinatorLayout root_layout;
    private EditText et_title;
    private NoteBean noteBean;
    private NoteOpenHelper mNoteOpenHelper;
    private SQLiteDatabase db;
    private NoteDao mNoteDao;
    private int openStatus = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mContext = this;
        mDatas = new ArrayList<ListBean>();
        mDoneDatas = new ArrayList<ListBean>();
        noteBean = new NoteBean();
        mNoteOpenHelper = new NoteOpenHelper(mContext);
        db = mNoteOpenHelper.getWritableDatabase();
        mNoteDao = new NoteDao(mContext);
        initView();
        initToolbar();
        Intent intent = getIntent();
        if(null == intent.getSerializableExtra("NoteBean")){
            //新建
            openStatus = 1;
            noteBean.setType(1);
            itemPos = 0;
            ListBean listBean = new ListBean(itemPos);
            itemPos++;
            mDatas.add(listBean);
        }
        else{
            //打开清单
            NoteBean dataBean = (NoteBean)intent.getSerializableExtra("NoteBean");
            String[] datas = dataBean.getListContent().split("\\\\");
            if(datas[0].length()>0){
                mDatas = JsonUtils.stringToList(datas[0],ListBean.class);
            }

            if(datas[2].length()>0) {
                mDoneDatas = JsonUtils.stringToList(datas[2],ListBean.class);
            }
            itemPos = Integer.parseInt(datas[4]);
            Log.d("33333",datas[2]+datas[1]+datas[0]+datas.length);
            et_title.setText(dataBean.getTitle());
            noteBean = dataBean;
        }
        initRecycleView();
        if(mDoneDatas.size()>0){

        }
        else{
            second_list.setVisibility(View.GONE);
        }
    }

    private void initToolbar() {
        mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.list_toolbar);
        mToolbarTitile = (TextView)findViewById(R.id.list_toolbar_title);
        mToolbarTitile.setText("清单");
        setSupportActionBar(mToolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorBlueGrey));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatas();
                saveAndFinished();
            }
        });
    }

    private void initView() {
        et_title = (EditText)findViewById(R.id.edit_title);
        root_layout = (CoordinatorLayout)findViewById(R.id.root_layout);
        rv_list = (RecyclerView)findViewById(R.id.recycle_list);
        rv_done_list = (RecyclerView)findViewById(R.id.recycle_done_list);
        second_list = (LinearLayout)findViewById(R.id.second_list);
        second_show_hide = (CardView)findViewById(R.id.second_show_hide);
        second_image = (ImageView)findViewById(R.id.show_hide_image);
        second_text = (TextView)findViewById(R.id.show_hide_text);
        add_fab = (FloatingActionButton)findViewById(R.id.add_fab);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListBean listBean = new ListBean(itemPos);
                mDatas.add(listBean);
                itemPos++;
                mListAdapter.setFocus(mDatas.size());
                mListAdapter.notifyDataSetChanged();
                Log.d("11111",mDatas.size()+"");
            }
        });
        show_status = true;
        second_image.setImageResource(R.drawable.ic_keyboard_arrow_up_blue_grey_600_24dp);
        second_text.setText("隐藏"+(mDoneDatas!=null?mDoneDatas.size():0)+"项已完成的清单");
        second_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show_status){
                    show_status = false;
                    rv_done_list.setVisibility(View.GONE);
                    second_image.setImageResource(R.drawable.ic_keyboard_arrow_down_blue_grey_600_24dp);
                    second_text.setText("显示"+mDoneDatas.size()+"项已完成的清单");
                }
                else{
                    show_status = true;
                    rv_done_list.setVisibility(View.VISIBLE);
                    second_image.setImageResource(R.drawable.ic_keyboard_arrow_up_blue_grey_600_24dp);
                    second_text.setText("隐藏"+mDoneDatas.size()+"项已完成的清单");
                }
            }
        });
    }

    private void initRecycleView() {
        rv_list.setLayoutManager(mLinearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_list.setAdapter(mListAdapter = new ListAdapter(this, mDatas, R.layout.list_item));
        rv_list.setHasFixedSize(true);
        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case 0:
                        add_fab.animate().alpha(1).setInterpolator(new DecelerateInterpolator(2));
                        add_fab.setEnabled(true);
                        break;
                    case 1:
                    case 2:
                        add_fab.animate().alpha(0.1f).setInterpolator(new DecelerateInterpolator(2));
                        add_fab.setEnabled(false);
                        break;
                }
            }
        });
        rv_done_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_done_list.setAdapter(mDoneListAdapter = new ListAdapter(this, mDoneDatas, R.layout.list_item));
        rv_done_list.setHasFixedSize(true);
        rv_done_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case 0:
                        add_fab.animate().alpha(1).setInterpolator(new DecelerateInterpolator(2));
                        add_fab.setEnabled(true);
                        break;
                    case 1:
                    case 2:
                        add_fab.animate().alpha(0.1f).setInterpolator(new DecelerateInterpolator(2));
                        add_fab.setEnabled(false);
                        break;
                }
            }
        });
        mListAdapter.setListListener(new ListAdapter.ListListener() {
            Handler handler = new Handler();
            @Override
            public void onCheck(final int pos) {
                ListBean listBean = mDatas.get(pos);
                listBean.setStatus(false);
                mDatas.remove(pos);
                mDoneDatas.add(listBean);
                sortDataList(mDoneDatas);
                handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_done_list,mDoneListAdapter);
                handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_list,mListAdapter);
                if(second_list.getVisibility()!=View.VISIBLE){
                    second_list.setVisibility(View.VISIBLE);
                }
                if(show_status){
                    second_text.setText("隐藏"+mDoneDatas.size()+"项已完成的清单");
                }
                else{
                    second_text.setText("显示"+mDoneDatas.size()+"项已完成的清单");
                }
            }

            @Override
            public void onUpdate(int pos, String text) {
                ListBean listBean = mDatas.get(pos);
                listBean.setListContent(text);
                mDatas.remove(pos);
                mDatas.add(pos,listBean);
            }

            @Override
            public void onDel(final int pos) {
                saveBean = mDatas.get(pos);
                mDatas.remove(pos);
                handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_list,mListAdapter);
                if(show_status){
                    second_text.setText("隐藏"+mDoneDatas.size()+"项已完成的清单");
                }
                else{
                    second_text.setText("显示"+mDoneDatas.size()+"项已完成的清单");
                }
                Snackbar.make(root_layout,"删除了一项清单，是否撤销？",2500)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undo(0,pos);
                            }
                        }).show();
            }
        });
        mDoneListAdapter.setListListener(new ListAdapter.ListListener() {
            Handler handler = new Handler();
            @Override
            public void onCheck(final int pos) {
                ListBean listBean = mDoneDatas.get(pos);
                listBean.setStatus(true);
                mDoneDatas.remove(pos);
                mDatas.add(listBean);
                sortDataList(mDatas);
                handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_list,mListAdapter);
                handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_done_list,mDoneListAdapter);
                if(mDoneDatas.size()>0) {
                    if (show_status) {
                        second_text.setText("隐藏" + mDoneDatas.size() + "项已完成的清单");
                    } else {
                        second_text.setText("显示" + mDoneDatas.size() + "项已完成的清单");
                    }
                }
                else{
                    second_list.setVisibility(View.GONE);
                }
            }

            @Override
            public void onUpdate(int pos, String text) {
                ListBean listBean = mDoneDatas.get(pos);
                listBean.setListContent(text);
                mDoneDatas.remove(pos);
                mDoneDatas.add(pos,listBean);
            }

            @Override
            public void onDel(int pos) {
                mDoneDatas.remove(pos);
                handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_done_list,mDoneListAdapter);
                if(mDoneDatas.size()>0) {
                    if (show_status) {
                        second_text.setText("隐藏" + mDoneDatas.size() + "项已完成的清单");
                    } else {
                        second_text.setText("显示" + mDoneDatas.size() + "项已完成的清单");
                    }
                }
                else{
                    second_list.setVisibility(View.GONE);
                }
            }
        });
    }
    protected void handlerPostAndNotifyAdapterNotifyDataSetChanged(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    Log.d("22222","test5");
                    adapter.notifyDataSetChanged();

                } else {
                    handlerPostAndNotifyAdapterNotifyDataSetChanged(handler, recyclerView, adapter);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        saveAndFinished();
        super.onBackPressed();
    }
    private void sortDataList(ArrayList<ListBean> list){
        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                ListBean l1 = (ListBean) o1;
                ListBean l2 = (ListBean) o2;
                if (l1.getItemPos() < l2.getItemPos())
                    return -1;
                else if (l1.getItemPos()==l2.getItemPos())
                    return 0;
                else if (l1.getItemPos()>l2.getItemPos())
                    return 1;
                return 0;
            }
        };
        Collections.sort(list,comp);
    }
    private void undo(int status,int pos){
        Handler handler = new Handler();
        if(status==0){
            mDatas.add(pos,saveBean);
            handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_list,mListAdapter);
        }else{
            mDoneDatas.add(pos,saveBean);
            handlerPostAndNotifyAdapterNotifyDataSetChanged(handler,rv_done_list,mDoneListAdapter);
            if(second_list.getVisibility()!=View.VISIBLE){
                second_list.setVisibility(View.VISIBLE);
            }
        }
    }
    private void getDatas(){
        String json1 = JsonUtils.objectToString( mDatas  ) ;
        String json2 = JsonUtils.objectToString(mDoneDatas);
        StringBuffer sb = new StringBuffer();
        sb.append(json1+"\\\\"+json2+"\\\\"+itemPos);
        noteBean.setListContent(sb.toString());
        if(et_title.getText().toString().length()>0){
            noteBean.setTitle(et_title.getText().toString());
        }
        else{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date now = new Date();
            noteBean.setTitle(formatter.format(now));
        }
        Log.d("33333",noteBean.getListContent());
    }
    private void saveAndFinished(){
        getDatas();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(openStatus==1){
                    mNoteDao.add(noteBean);
                }
                else{
                    mNoteDao.update(noteBean);
                }
                Message msg = Message.obtain();
                msg.what=1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
