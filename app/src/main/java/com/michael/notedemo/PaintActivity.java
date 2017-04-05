package com.michael.notedemo;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michael.notedemo.Utils.NoteBean;
import com.michael.notedemo.Utils.NoteDao;
import com.michael.notedemo.Utils.NoteOpenHelper;
import com.michael.notedemo.paint.OnPathListener;
import com.michael.notedemo.paint.PaintInfo;
import com.michael.notedemo.paint.PaintView;
import com.michael.notedemo.paint.PathNode;
import com.michael.notedemo.paint.colorSel_fragment;
import com.michael.notedemo.paint.sizeSel_fragment;

import java.io.File;

/**
 * Created by Michael on 2017/3/24.
 */

public class PaintActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton show_hide_bt;
    private sizeSel_fragment penSize;
    private sizeSel_fragment eraser;
    private colorSel_fragment colorSel;
    private boolean second_status = true;
    private FrameLayout second_floor_layout;
    private ImageButton penSize_bt;
    private ImageButton colorSel_bt;
    private ImageButton eraser_bt;
    private View latestBtView;
    private PaintView paintView;
    private int PenWidth;
    private int EraserWidth;
    private PathNode pathNode;
    private File dir;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath()+"/DrawAPicture";
    private Context mContext;
    private View colorSelectView;
    private ImageButton redo_bt;
    private ImageButton undo_bt;
    private ImageButton clear_bt;
    private ImageButton save_bt;
    private String paintMsg;
    private String fileName;
    private EditText et_title;
    private final static int SAVE_DONE = 1;
    private final static int SAVE_DATABASE =2;
    private final static int SAVING = 0;
    private NoteBean noteBean;
    private int status = 0;
    private static String colordefaultinfo = "#ffff4081|#ff3f51b5|#ff607d8b|#ff000000|#ffffffff|#ffffffff|#ffffffff";
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SAVE_DONE:
                    noteBean = new NoteBean();
                    noteBean.setTitle(temp!=null?temp.toString():fileName.substring(fileName.length()-23,fileName.length()));
                    noteBean.setUri(fileName);
                    noteBean.setType(2);
                    noteBean.setPicinfo(paintMsg);
                    Log.d("11111",paintMsg+"picinfo");
                    status = 1;
                    break;
                case SAVE_DATABASE:
                    saveDialog.dismiss();
                    status = 0;
                    if(editStatus ==0) {
                        Intent intent = new Intent();
                        intent.putExtra("closeStatus",1);
                        PaintActivity.this.setResult(RESULT_OK, intent);
                    }
                    else{
                        Log.d("11111",noteBean.getTitle());
                        Intent intent = new Intent();
                        intent.putExtra("closeStatus",1);
                        PaintActivity.this.setResult(RESULT_OK, intent);
                    }
                    finish();
                    break;
            }
            if(status==1) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(editStatus==0) {
                            noteDao.add(noteBean);
                        }
                        else{
                            noteBean.setId(mDatas.getId());
                            noteDao.update(noteBean);
                        }
                        Message msg = Message.obtain();
                        msg.what = SAVE_DATABASE;
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
            super.handleMessage(msg);
        }
    };
    private MaterialDialog saveDialog;
    private CharSequence temp;
    private NoteOpenHelper mNoteOpenHelper;
    private SQLiteDatabase db;
    private NoteDao noteDao;
    private NoteBean mDatas;
    private int editStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        mContext = this;
        mNoteOpenHelper = new NoteOpenHelper(mContext);
        db = mNoteOpenHelper.getWritableDatabase();
        noteDao = new NoteDao(mContext);
        penSize_bt = (ImageButton)findViewById(R.id.pensize_paint);
        colorSel_bt = (ImageButton)findViewById(R.id.pencolor_paint);
        colorSelectView = (View)findViewById(R.id.colorSelectView);
        colorSelectView.setBackgroundColor(PaintInfo.PaintColor);
        eraser_bt = (ImageButton)findViewById(R.id.eraser_paint);
        redo_bt = (ImageButton)findViewById(R.id.redo_paint);
        undo_bt = (ImageButton)findViewById(R.id.undo_paint);
        clear_bt = (ImageButton)findViewById(R.id.clear_paint);
        save_bt = (ImageButton)findViewById(R.id.save_paint);
        paintView = (PaintView)findViewById(R.id.paint_view);
        et_title = (EditText)findViewById(R.id.edit_title);
        et_title.addTextChangedListener(new TextWatcher() {
            private int editStart;
            private int editEnd;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editStart = et_title.getSelectionStart();
                editEnd = et_title.getSelectionEnd();
                if (temp.length() > 40) {//限制长度
                    Toast.makeText(mContext,
                            "输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                            .show();
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    et_title.setText(s);
                    et_title.setSelection(tempSelection);
                }
                paintView.setFirstTime(false);
                temp = et_title.getText().toString();
            }
        });
        pathNode = new PathNode();
        paintView.setIsRecordPath(true,pathNode);
        Intent intent = getIntent();
        if(intent.getData() != null){
            paintView.JsonToPathNodeToHandle(intent.getData());
            paintView.preview(pathNode.getPathList());
        }
        dir = new File(PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
        initDatas();
        paintView.setOnPathListener(new OnPathListener() {
            @Override
            public void addNodeToPath(float x, float y, int event, boolean IsPaint) {
                PathNode.Node tempnode = pathNode.new Node();
                tempnode.x = paintView.px2dip(x);
                tempnode.y = paintView.px2dip(y);
                if (IsPaint) {
                    tempnode.PenColor = PaintInfo.PaintColor;
                    tempnode.PenWidth = PaintInfo.PaintWidth;
                } else {
                    tempnode.EraserWidth = PaintInfo.EraserWidth;
                }
                tempnode.IsPaint = IsPaint;
                tempnode.TouchEvent = event;
                tempnode.time = System.currentTimeMillis();
                pathNode.addNode(tempnode);
            }
        });
        initFragment();
        second_floor_layout = (FrameLayout)findViewById(R.id.second_floor);
        show_hide_bt = (ImageButton)findViewById(R.id.show_hide_paint);
        show_hide_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second_status = !second_status;
                second_floor_layout.setVisibility(second_status==true?View.VISIBLE:View.GONE);
                show_hide_bt.setImageResource(second_status==true?R.drawable.ic_keyboard_arrow_down_blue_grey_600_24dp:R.drawable.ic_keyboard_arrow_up_blue_grey_600_24dp);
            }
        });
        colorSel_bt.setOnClickListener(this);
        eraser_bt.setOnClickListener(this);
        penSize_bt.setOnClickListener(this);
        redo_bt.setOnClickListener(this);
        undo_bt.setOnClickListener(this);
        clear_bt.setOnClickListener(this);
        save_bt.setOnClickListener(this);
    }

    private void initDatas() {
        Intent intent =  getIntent();
        if(null==intent.getSerializableExtra("NoteBean")){
            editStatus = 0;
            paintMsg = colordefaultinfo;
        }
        else{
            editStatus = 1;
            mDatas = (NoteBean)intent.getSerializableExtra("NoteBean");
            et_title.setText(mDatas.getTitle());
            paintMsg = mDatas.getPicinfo();
            paintView.setmBitmap(Uri.fromFile(new File(mDatas.getUri())));
        }
    }

    private void initFragment() {
        FragmentManager fm = getFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        latestBtView = penSize_bt;
        penSize = new sizeSel_fragment();
        Bundle bundle = new Bundle();
        bundle.putInt("image", R.drawable.ic_create_blue_grey_600_24dp);
        penSize.setArguments(bundle);
        penSize.setOnChangeSizeListener(new sizeSel_fragment.OnChangeSizeListener() {
            @Override
            public void onSizeChange(int width) {
                paintView.setPenWidth(width);
            }
        });
        transaction.replace(R.id.second_floor, penSize);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        final Bundle bundle = new Bundle();
        switch (v.getId()){
            case R.id.pensize_paint:
                penSize_bt.setImageResource(R.drawable.ic_create_white_24dp);
                if(penSize==null){
                    penSize = new sizeSel_fragment();
                    bundle.putInt("image", R.drawable.ic_create_blue_grey_600_24dp);
                    penSize.setArguments(bundle);
                }
                paintView.setIsPaint(true);
                changeImagButton(latestBtView,penSize_bt);
                latestBtView = penSize_bt;
                penSize.setOnChangeSizeListener(new sizeSel_fragment.OnChangeSizeListener() {
                    @Override
                    public void onSizeChange(int width) {
                        paintView.setPenWidth(width);
                    }
                });
                transaction.replace(R.id.second_floor,penSize);
                break;
            case R.id.pencolor_paint:
                colorSel_bt.setImageResource(R.drawable.ic_colorize_white_24dp);
                colorSel = new colorSel_fragment();
                bundle.putString("color",paintMsg);
                colorSel.setArguments(bundle);
                paintView.setIsPaint(true);
                changeImagButton(latestBtView,colorSel_bt);
                latestBtView = colorSel_bt;
                colorSel.setOnChangeColorListener(new colorSel_fragment.OnChangeColorListener() {
                    @Override
                    public void onColorChange(int color) {
                        paintView.setColor(color);
                        colorSelectView.setBackgroundColor(color);
                    }

                    @Override
                    public void onGetColor(String color) {
                        paintMsg = color;
                    }
                });
                transaction.replace(R.id.second_floor,colorSel);
                break;
            case R.id.eraser_paint:
                eraser_bt.setImageResource(R.drawable.ic_layers_white_24dp);
                if(eraser==null){
                    eraser = new sizeSel_fragment();
                    bundle.putInt("image",R.drawable.ic_layers_blue_grey_600_24dp);
                    eraser.setArguments(bundle);
                }
                paintView.setIsPaint(false);
                changeImagButton(latestBtView,eraser_bt);
                latestBtView = eraser_bt;
                eraser.setOnChangeSizeListener(new sizeSel_fragment.OnChangeSizeListener() {
                    @Override
                    public void onSizeChange(int width) {
                        paintView.setmEraserPaint(width);
                    }
                });
                transaction.replace(R.id.second_floor,eraser);
                break;
            case R.id.undo_paint:
                paintView.ReDoORUndo(true);
                break;
            case R.id.redo_paint:
                paintView.ReDoORUndo(false);
                break;
            case R.id.clear_paint:
                new MaterialDialog.Builder(mContext)
                        .positiveText("取消")
                        .negativeText("确定")
                        .content("清空画布将失去为保存的图片，确定要清空吗？")
                        .title("注意！")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                paintView.clean();
                                pathNode.clearList();
                                paintView.clearReUnList();
                            }
                        })
                        .show();
                break;
            case R.id.save_paint:
                    if (paintView.isFirstTime()) {
                        Toast.makeText(mContext, "你还什么都没画呀", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    new MaterialDialog.Builder(mContext)
                            .positiveText("确定")
                            .negativeText("取消")
                            .content("确定要将文件保存在" + PATH)
                            .title("注意！")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    savePicture();
                                }
                            })
                            .show();
                break;
        }
        transaction.commit();
    }

    private void savePicture() {
        saveDialog = new MaterialDialog.Builder(mContext)
                .title("注意！")
                .content("正在保存中，请不要关闭程序")
                .progress(true, 0)
                .cancelable(false)
                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(editStatus==0) {
                    fileName = paintView.BitmapToPicture(dir, editStatus);
                }
                else{
                    fileName = paintView.BitmapToPicture(new File(mDatas.getUri()),editStatus);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = SAVE_DONE;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    private void changeImagButton(View latestBtView,View recentView) {
        if(latestBtView==recentView){
            return;
        }
        switch (latestBtView.getId()){
            case R.id.pensize_paint:
                penSize_bt.setImageResource(R.drawable.ic_create_blue_grey_600_24dp);
                break;
            case R.id.pencolor_paint:
                colorSel_bt.setImageResource(R.drawable.ic_colorize_blue_grey_600_24dp);
                break;
            case R.id.eraser_paint:
                eraser_bt.setImageResource(R.drawable.ic_layers_blue_grey_600_24dp);
                break;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        paintView.save();
    }
    @Override
    public void onBackPressed() {
            if (paintView.isFirstTime()) {
                Intent intent = new Intent();
                intent.putExtra("closeStatus", 0);
                PaintActivity.this.setResult(RESULT_CANCELED, intent);
                super.onBackPressed();
            } else {
                Log.d("11111", paintView.isFirstTime() + "");
                new MaterialDialog.Builder(mContext)
                        .cancelable(false)
                        .positiveText("保存")
                        .negativeText("取消")
                        .title("注意！")
                        .content("即将退出该界面，请问是否保存内容")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent();
                                intent.putExtra("closeStatus", 0);
                                PaintActivity.this.setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                savePicture();
                            }
                        })
                        .show();
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
