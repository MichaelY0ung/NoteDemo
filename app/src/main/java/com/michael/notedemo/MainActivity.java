package com.michael.notedemo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.michael.notedemo.Utils.NoteBean;
import com.michael.notedemo.Utils.NoteDao;
import com.michael.notedemo.Utils.NoteOpenHelper;
import com.michael.notedemo.Utils.Utils;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Context mContext;
    private Toolbar mToolbar;
    private int mToolbarHeight;
    private SparseArray<RecyclerView.ItemDecoration> mDividers = new SparseArray<>();
    private RecyclerAdapter TextAdapter;
    private ArrayList<NoteBean> mDatas;
    private LinearLayoutManager mLinearLayoutManager;
    private NoteOpenHelper mNoteOpenHelper;
    private NoteDao noteDao;
    private SQLiteDatabase db;
    private int mAddTabHeight;
    private LinearLayout mLinearLayout;
    private TextView mAddNote;
    private ImageButton mAddList;
    private ImageButton mAddPaint;
    private ImageButton mAddProject;
    private LinearLayout mEditLayout;
    private Button mDeleteItem;
    private Button mSelectAll;
    private SwipeMenuLayout mSwipeMenuLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rv_list;
    private final static int GET_DATA = 1;
    private ArrayList<NoteBean> changeData;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            freshStatus = false;
                            mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(0.5f)).start();
                            mLinearLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(0.5f)).start();
                            rv_list.animate().translationY(0).setInterpolator(new AccelerateInterpolator(0.5f)).start();
                            Log.d("11111",mAddTabHeight+" "+mToolbarHeight);
                            mDatas.clear();
                            mDatas.addAll(getData());
                            TextAdapter.notifyDataSetChanged();
                        }
                    },1000);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private int closeStatus =1;
    private int intentPos;
    private boolean freshStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueGrey));
        mSwipeRefreshLayout.setDistanceToTriggerSync(400);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        mEditLayout = (LinearLayout)findViewById(R.id.editLayout);
        mNoteOpenHelper = new NoteOpenHelper(mContext);
        db = mNoteOpenHelper.getWritableDatabase();
        noteDao = new NoteDao(mContext);
        mDeleteItem = (Button)findViewById(R.id.deleteItem);
        mSelectAll = (Button)findViewById(R.id.selectAll);
        mSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=mDatas.size()-1;i>=0;i--){
                    mDatas.get(i).setSelected(true);
                }
                TextAdapter.notifyDataSetChanged();
            }
        });
        mDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"11111",Toast.LENGTH_SHORT).show();
                int length = mDatas.size();
                for(int i=length-1;i>=0;i--){
                    if(mDatas.get(i).isSelected()) {
                        noteDao.del(mDatas.get(i).getId());
                        mDatas.remove(i);
                    }
                }
                TextAdapter.setShowCheck(false);
                mLinearLayout.setVisibility(View.VISIBLE);
                mEditLayout.setVisibility(View.GONE);
                TextAdapter.setSwipeEnable(true);
                TextAdapter.notifyDataSetChanged();
            }
        });
        mDatas = getData();
        initToolbar();
        initAddTab();
        initRecycleView();
    }

    private void initRecycleView() {
        rv_list = (RecyclerView)findViewById(R.id.recycleview_list);
        rv_list.setLayoutManager(mLinearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        int paddingTop = Utils.getToolbarHeight(MainActivity.this);
        int paddingBottom = Utils.getAddTabHeight(mContext);
        rv_list.setPadding(rv_list.getPaddingLeft(), paddingTop, rv_list.getPaddingRight(), rv_list.getPaddingBottom());
        rv_list.setAdapter(TextAdapter = new RecyclerAdapter(this, mDatas, R.layout.recycler_item));
        TextAdapter.setOnDelListener(new RecyclerAdapter.onSwipeListener() {
            @Override
            public void onDel(int pos) {
                if (pos >= 0 && pos < mDatas.size()) {
                    makeText(MainActivity.this, "删除:" + pos, Toast.LENGTH_SHORT).show();
                    noteDao.del(mDatas.get(pos).getId());
                    Log.d("11111",mDatas.get(pos).getId()+"");
                    mDatas.remove(pos);
                    TextAdapter.notifyItemRemoved(pos);//推荐用这个
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onDone(int pos) {

            }

            @Override
            public void onTop(int pos) {
                if (pos > 0 && pos < mDatas.size()) {
                    NoteBean data = mDatas.get(pos);
                    ArrayList<NoteBean> saveList = new ArrayList<NoteBean>();
                    saveList.addAll(mDatas);
                    mDatas.remove(data);
                    Log.d("11111",pos+"pos");
                    TextAdapter.notifyItemInserted(0);
                    mDatas.add(0, data);
                    for(int i=0;i<=pos;i++){
                        NoteBean changeBean = new NoteBean(mDatas.get(i));
                        changeBean.setId(saveList.get(i).getId());
                        mDatas.remove(mDatas.get(i));
                        mDatas.add(i,changeBean);

                    }
                    changData(pos);
                    TextAdapter.notifyItemRemoved(pos + 1);
                    if (mLinearLayoutManager.findFirstVisibleItemPosition() == 0) {
                        rv_list.scrollToPosition(0);
                    }
                    //notifyItemRangeChanged(0,holder.getAdapterPosition()+1);
                }
            }

            @Override
            public void onEdit(int pos) {
                NoteBean data = mDatas.get(pos);
                mDatas.remove(data);
                data.setSelected(true);
                mDatas.add(pos,data);
                TextAdapter.setShowCheck(true);
                TextAdapter.setSwipeEnable(false);
                mLinearLayout.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.VISIBLE);
                TextAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCheck(int pos) {
                NoteBean data = mDatas.get(pos);
                mDatas.remove(data);
                data.setSelected(!data.isSelected());
                mDatas.add(pos,data);
                TextAdapter.setSwipeEnable(false);
                TextAdapter.notifyDataSetChanged();
            }

            @Override
            public void onIntent(int pos) {
                if(!freshStatus) {
                    Intent intent = null;
                    intentPos = pos;
                    switch (mDatas.get(pos).getType()) {
                        case 0:
                            intent = new Intent(mContext, CreateActivity.class);
                            intent.putExtra("NoteBean", mDatas.get(pos));
                            startActivityForResult(intent, 1);
                            break;
                        case 1:
                            intent = new Intent(mContext, ListActivity.class);
                            intent.putExtra("NoteBean", mDatas.get(pos));
                            startActivityForResult(intent, 1);
                            break;
                        case 2:
                            intent = new Intent(mContext, PaintActivity.class);
                            intent.putExtra("NoteBean", mDatas.get(pos));
                            startActivityForResult(intent, 1);
                            break;
                        case 3:
                            break;
                    }
                }

            }

        });
        rv_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                }
                return false;
            }
        });

        rv_list.setHasFixedSize(true);
        RecyclerView.ItemDecoration divider = new InsetDivider.Builder(this)
                .orientation(InsetDivider.VERTICAL_LIST)
                .dividerHeight(getResources().getDimensionPixelSize(R.dimen.divider_height))
                .color(getResources().getColor(R.color.colorAlphaBlack))
                .insets(getResources().getDimensionPixelSize(R.dimen.divider_inset), 0)
                .overlay(true)
                .build();
        rv_list.addItemDecoration(divider);
        rv_list.addOnScrollListener(new HidingScrollListener(this) {

            @Override
            public void onMoved(int distance) {
                mToolbar.setTranslationY(-distance);
                mLinearLayout.setTranslationY(distance*mAddTabHeight/mToolbarHeight);
            }

            @Override
            public void onShow() {
                mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                mLinearLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void onHide() {
                mToolbar.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
                mLinearLayout.animate().translationY(mAddTabHeight).setInterpolator(new AccelerateInterpolator(2)).start();
            }

        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                freshStatus = true;
                TextAdapter.setShowCheck(false);
                TextAdapter.notifyDataSetChanged();
                rv_list.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(0.5f)).start();
                mToolbar.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(0.5f)).start();
                mLinearLayout.animate().translationY(mAddTabHeight).setInterpolator(new AccelerateInterpolator(0.5f)).start();
                int[] location = new int[2];
                mToolbar.getLocationInWindow(location);
                Log.d("11111",location[0]+" "+location[1]);
                threadForGetData();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(closeStatus!=0) {
            mSwipeRefreshLayout.setRefreshing(true);
            TextAdapter.setShowCheck(false);
            TextAdapter.notifyDataSetChanged();
            freshStatus = true;
            rv_list.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(0.1f)).start();
            mToolbar.animate().translationY(-mToolbarHeight).setInterpolator(new AccelerateInterpolator(0.1f)).start();
            mLinearLayout.animate().translationY(mAddTabHeight).setInterpolator(new AccelerateInterpolator(0.1f)).start();
            threadForGetData();
        }
        closeStatus = 1;
    }

    private void threadForGetData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
                Message msg = Message.obtain();
                msg.what = GET_DATA;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void changData(int pos) {
        int cosavePos = pos;
        Log.d("11111",cosavePos+"");
        for(int i=0;i<=cosavePos;i++){
            NoteBean noteBean1 = new NoteBean(mDatas.get(i));
            noteDao.update(noteBean1);
            Log.d("11111","pos:"+pos+"noteBean"+noteBean1.getTitle());
            pos--;
        }
    }

    public ArrayList<NoteBean> getData() {
        return noteDao.queryAll();
    }

    public <T extends View> T findView(int ViewId) {
        return (T) findViewById(ViewId);
    }

    private void initToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        mToolbarHeight = Utils.getToolbarHeight(this);

    }
    private void initAddTab(){
        mAddTabHeight = Utils.getAddTabHeight(this);
        mAddNote = (TextView)findViewById(R.id.add_note);
        mAddList = (ImageButton)findViewById(R.id.add_list);
        mAddPaint = (ImageButton)findViewById(R.id.add_paint);
        mAddProject = (ImageButton)findViewById(R.id.add_project);
        mAddList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int[] offset = new int[2];
                mAddList.getLocationInWindow(offset);
                Toast toast = Toast.makeText(mContext,"新建清单",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.LEFT,offset[0]-36,offset[1]-240);
                toast.show();
                return true;
            }
        });
        mAddPaint.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int[] offset = new int[2];
                mAddPaint.getLocationInWindow(offset);
                Toast toast = Toast.makeText(mContext,"新建绘图",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.LEFT,offset[0]-36,offset[1]-240);
                toast.show();
                return true;
            }
        });
        mAddPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PaintActivity.class);
                startActivityForResult(intent,1);
            }
        });
        mAddProject.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int[] offset = new int[2];
                mAddProject.getLocationInWindow(offset);
                Toast toast = Toast.makeText(mContext,"新建项目",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.LEFT,offset[0]-36,offset[1]-240);
                toast.show();
                return true;
            }
        });
        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CreateActivity.class);
                startActivityForResult(intent,1);
            }
        });
        mAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ListActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
    private int mBackKeyPressedTimes = 0;
    @Override
    public void onBackPressed() {
        if(TextAdapter.isShowCheck()){
            TextAdapter.setShowCheck(false);
            mLinearLayout.setVisibility(View.VISIBLE);
            mEditLayout.setVisibility(View.GONE);
            TextAdapter.setSwipeEnable(true);
            TextAdapter.notifyDataSetChanged();
        }
        else{
            if(mBackKeyPressedTimes==0){
                mBackKeyPressedTimes = 1;
                Toast.makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            mBackKeyPressedTimes = 0;
                        }
                    }
                }.start();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null){
            return;
        }
        switch (resultCode){
            case RESULT_OK:
                closeStatus = data.getIntExtra("closeStatus",1);
                break;
            case RESULT_CANCELED:
                closeStatus = data.getIntExtra("closeStatus",1);
                break;
            default:
                break;
        }
    }
}
