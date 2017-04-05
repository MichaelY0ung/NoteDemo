package com.michael.notedemo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michael.notedemo.Utils.NoteBean;
import com.michael.notedemo.Utils.NoteDao;
import com.michael.notedemo.Utils.NoteOpenHelper;
import com.michael.notedemo.Utils.TagAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.makeText;

public class CreateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mTagView;
    private LinearLayoutManager mLayoutManager;
    private Context mContext;
    private TagAdapter mTagAdapter;
    private ArrayList<String> mList;
    private TextView mEditTag;
    private NoteBean mDatas;
    private EditText mTitle;
    private EditText mContent;
    private String mTags;
    private TextView mToolbarTitle;
    private Button mSaveOrEdit;
    private MaterialDialog addTagDialog;
    private NoteListener noteListener;
    private String mTitleStr;
    private String mContentStr;
    private String newTags;
    private String[] tags;
    private static String[] defaultTags = new String[]{"工作", "学习", "随笔"};
    private boolean isTagSame = false;
    private int editStatus = 0;
    private NoteOpenHelper mNoteOpenHelper;
    private SQLiteDatabase db;
    private NoteDao noteDao;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mSaveOrEdit.setText("编辑");
                if (editStatus == 2) {
                    finish();
                } else {
                    editStatus = 0;
                    mContent.setEnabled(false);
                    mTitle.setEnabled(false);
                }
            }
        }
    };
    private boolean saveStatus = false;
    private boolean backPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mContext = this;
        mNoteOpenHelper = new NoteOpenHelper(mContext);
        db = mNoteOpenHelper.getWritableDatabase();
        noteDao = new NoteDao(mContext);
        mEditTag = (TextView) findViewById(R.id.tag_edit);
        mToolbarTitle = (TextView) findViewById(R.id.add_toolbar_title);
        mSaveOrEdit = (Button) findViewById(R.id.edit_or_save);
        mEditTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTag.getText().toString().equals("编辑")) {
                    mEditTag.setText("保存");
                    mTagAdapter.setShowDelete(true);
                    mTagAdapter.notifyDataSetChanged();
                } else {
                    mEditTag.setText("编辑");
                    mTagAdapter.setShowDelete(false);
                    mTagAdapter.notifyDataSetChanged();
                }
            }
        });

        init();
        initToolbar();
        initDatas();
        initTagView();
        mSaveOrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStatus == 0) {
                    mSaveOrEdit.setText("保存");
                    mSaveOrEdit.setEnabled(false);
                    Toast.makeText(mContext,"111",Toast.LENGTH_SHORT).show();
                    mTitle.setEnabled(true);
                    mContent.setEnabled(true);
                    backPress = false;
                    mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorGrey));
                    editStatus = 1;
                } else {
                    new MaterialDialog.Builder(mContext)
                            .title("注意")
                            .content("是否保存？")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    saveNotes();
                                }
                            })
                            .negativeText("取消")
                            .positiveText("确定")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (editStatus == 2) {
                                        Intent intent = new Intent();
                                        intent.putExtra("closeStatus", 0);
                                        CreateActivity.this.setResult(RESULT_CANCELED, intent);
                                        finish();
                                    } else {
                                        mSaveOrEdit.setText("编辑");
                                        mTitle.setText(mDatas.getTitle());
                                        mTitle.setEnabled(false);
                                        mContent.setText(mDatas.getContent());
                                        mContent.setEnabled(false);
                                    }
                                }
                            })
                            .show();
                }
            }
        });
    }

    private class NoteListener implements TextWatcher {
        private int editStart;
        private int editEnd;
        private EditText editText;

        public NoteListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (editText.getId()) {
                case R.id.edit_title:
                    mTitleStr = mTitle.getText().toString();
                    break;
                case R.id.edit_content:
                    mContentStr = mContent.getText().toString();
                    break;
            }
            if (mContentStr.length() > 0 || mTitleStr.length() > 0) {
                mSaveOrEdit.setEnabled(true);
                mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorBlueGrey));
                saveStatus = true;
            } else {
                mSaveOrEdit.setEnabled(false);
                Toast.makeText(mContext,"112",Toast.LENGTH_SHORT).show();
                mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorGrey));
            }

        }
    }

    class NoteEditListener implements TextWatcher {
        private final EditText editText;

        public NoteEditListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (editText.getId()) {
                case R.id.edit_title:
                    mTitleStr = editText.getText().toString();
                    break;
                case R.id.edit_content:
                    mContentStr = editText.getText().toString();
                    break;
            }
            if (mTitleStr.equals(mDatas.getTitle()) && mContentStr.equals(mDatas.getContent())) {
                if(!backPress) {
                    mSaveOrEdit.setEnabled(false);
                    Toast.makeText(mContext,"113",Toast.LENGTH_SHORT).show();
                    mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorGrey));
                    saveStatus = false;
                }
            } else {
                mSaveOrEdit.setEnabled(true);
                mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorBlueGrey));
                saveStatus = true;
            }
            if (mTitleStr.length() == 0 && mContentStr.length() == 0) {
                mSaveOrEdit.setEnabled(false);
                Toast.makeText(mContext,"114",Toast.LENGTH_SHORT).show();

                mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorGrey));
                saveStatus = false;
            }
        }
    }

    private void init() {
        mTitle = (EditText) findViewById(R.id.edit_title);
        mContent = (EditText) findViewById(R.id.edit_content);
        mTitleStr = mTitle.getText().toString();
        mContentStr = mContent.getText().toString();
    }

    private void initDatas() {
        Intent intent = getIntent();
        if (null == intent.getSerializableExtra("NoteBean")) {
            mDatas = new NoteBean();
            mToolbarTitle.setText("新建笔记");
            mSaveOrEdit.setText("保存");
            mSaveOrEdit.setEnabled(false);
            mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorGrey));
            mTitle.setEnabled(true);
            mContent.setEnabled(true);
            mList = new ArrayList<String>();
            editStatus = 2;
            mTitle.addTextChangedListener(new NoteListener(mTitle));
            mContent.addTextChangedListener(new NoteListener(mContent));
        } else {
            mToolbarTitle.setText("笔记");
            mDatas = (NoteBean) intent.getSerializableExtra("NoteBean");
            mTitle.setText(mDatas.getTitle());
            mContent.setText(mDatas.getContent());
            mTags = mDatas.getTag();
            mTitle.addTextChangedListener(new NoteEditListener(mTitle));
            mContent.addTextChangedListener(new NoteEditListener(mContent));
            initTags();
        }
    }

    private String getTags(ArrayList<String> mList) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mList.size() - 1; i++) {
            sb.append(mList.get(i) + "|");
        }
        if (mList.size() != 0) {
            sb.append(mList.get(mList.size() - 1));
            return sb.toString();
        } else {
            return null;
        }
    }

    private void initTags() {
        if (mTags != null) {
            tags = mTags.split("\\|");
            mList = new ArrayList<String>();
            for (int i = 0; i < tags.length; i++) {
                mList.add(tags[i]);
            }
        }
//        Log.d("11111",mTags+tags.length+mList.get(0));
    }

    private void initTagView() {
        mTagView = (RecyclerView) findViewById(R.id.tag_list);
        mTagView.setHasFixedSize(true);
        mTagView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTagView.setAdapter(mTagAdapter = new TagAdapter(mContext, mList, R.layout.tag_item));
        mTagAdapter.setOnDelListener(new TagAdapter.onTagListener() {
            @Override
            public void onDel(int pos) {
                if (pos >= 0 && pos < mList.size()) {
                    makeText(mContext, "删除:" + pos, Toast.LENGTH_SHORT).show();
                    mList.remove(pos);
                    saveStatus = true;
                    mTagAdapter.notifyItemRemoved(pos);//推荐用这个
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onEdit(int pos) {

            }

            @Override
            public void onAdd() {
                addTagDialog = new MaterialDialog.Builder(mContext)
                        .items(defaultTags)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                for (int i = 0; i < (null != mList ? mList.size() : 0); i++) {
                                    if (mList.get(i).equals(defaultTags[position])) {
                                        Toast.makeText(mContext, "重复标签", Toast.LENGTH_SHORT).show();
                                        isTagSame = true;
                                    }
                                }
                                if (!isTagSame) {
                                    mList.add(defaultTags[position]);
                                    mTagAdapter.notifyDataSetChanged();
                                    Log.d("11111", mList.toString());
                                }
                            }
                        })
                        .neutralText("自定义标签")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new MaterialDialog.Builder(mContext)
                                        .input("请输入标签", "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                newTags = input.toString();
                                                for (int i = 0; i < (null != mList ? mList.size() : 0); i++) {
                                                    Log.d("11111", newTags);
                                                    if (mList.get(i).equals(newTags)) {
                                                        Toast.makeText(mContext, "重复的tag", Toast.LENGTH_SHORT).show();
                                                        isTagSame = true;
                                                    }
                                                }
                                                Log.d("11111", "tag" + newTags);
                                                if (!isTagSame) {
                                                    Log.d("11111", newTags + "zzz");
                                                    mList.add(newTags);
                                                    mTagAdapter.notifyDataSetChanged();
                                                }

                                            }
                                        })
                                        .inputRange(1, 20)
                                        .title("输入标签")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                addTagDialog.show();
                                            }
                                        })
                                        .negativeText("返回")
                                        .positiveText("确定")
                                        .show();
                            }
                        })
                        .show();
                isTagSame = false;

            }
        });
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(mToolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorBlueGrey));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStatus == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("closeStatus", 0);
                    CreateActivity.this.setResult(RESULT_CANCELED, intent);
                    finish();
                } else if (editStatus == 1 && !saveStatus) {

                    Log.d("11111","here");
                    mSaveOrEdit.setText("编辑");
                    mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorBlueGrey));
                    mSaveOrEdit.setEnabled(true);
                    backPress = true;
                    Log.d("11111",mSaveOrEdit.isEnabled()+"");
                    mTitle.setText(mDatas.getTitle());
                    mTitle.setEnabled(false);
                    mContent.setText(mDatas.getContent());
                    mContent.setEnabled(false);
                    editStatus = 0;
                } else if (saveStatus) {
                    new MaterialDialog.Builder(mContext)
                            .title("注意")
                            .content("是否保存？")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    saveNotes();
                                }
                            })
                            .negativeText("取消")
                            .positiveText("确定")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (editStatus == 2) {
                                        Intent intent = new Intent();
                                        intent.putExtra("closeStatus", 0);
                                        CreateActivity.this.setResult(RESULT_CANCELED, intent);
                                        finish();
                                    } else {
                                        mSaveOrEdit.setText("编辑");
                                        editStatus = 0;
                                        mSaveOrEdit.setEnabled(true);
                                        mTitle.setText(mDatas.getTitle());
                                        mTitle.setEnabled(false);
                                        mContent.setText(mDatas.getContent());
                                        mContent.setEnabled(false);
                                        mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorBlueGrey));
                                    }
                                }
                            })
                            .show();
                }
                else if(editStatus==2){
                    Intent intent = new Intent();
                    intent.putExtra("closeStatus", 0);
                    CreateActivity.this.setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (editStatus == 0) {
            Intent intent = new Intent();
            intent.putExtra("closeStatus", 0);
            CreateActivity.this.setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        } else if (editStatus == 1 && !saveStatus) {

            Log.d("11111","here");
            mSaveOrEdit.setText("编辑");
            mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorBlueGrey));
            mSaveOrEdit.setEnabled(true);
            backPress = true;
            Log.d("11111",mSaveOrEdit.isEnabled()+"");
            mTitle.setText(mDatas.getTitle());
            mTitle.setEnabled(false);
            mContent.setText(mDatas.getContent());
            mContent.setEnabled(false);
            editStatus = 0;
        } else if (saveStatus) {
            new MaterialDialog.Builder(mContext)
                    .title("注意")
                    .content("是否保存？")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            saveNotes();
                        }
                    })
                    .negativeText("取消")
                    .positiveText("确定")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (editStatus == 2) {
                                Intent intent = new Intent();
                                intent.putExtra("closeStatus", 0);
                                CreateActivity.this.setResult(RESULT_CANCELED, intent);
                                finish();
                            } else {
                                mSaveOrEdit.setText("编辑");
                                editStatus = 0;
                                mSaveOrEdit.setEnabled(true);
                                mTitle.setText(mDatas.getTitle());
                                mTitle.setEnabled(false);
                                mContent.setText(mDatas.getContent());
                                mContent.setEnabled(false);
                                mSaveOrEdit.setTextColor(getResources().getColor(R.color.colorBlueGrey));
                            }
                        }
                    })
                    .show();
        }
        else if(editStatus==2){
            Intent intent = new Intent();
            intent.putExtra("closeStatus", 0);
            CreateActivity.this.setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }

    private void saveNotes() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        mDatas.setType(0);
        mDatas.setTitle(mTitleStr.equals("")?formatter.format(now):mTitleStr);
        mDatas.setContent(mContentStr);
        if(mList!=null) {
            mDatas.setTag(getTags(mList));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(editStatus==1){
                    noteDao.update(mDatas);
                }
                else{
                    noteDao.add(mDatas);
                }
                Message msg  = Message.obtain();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
