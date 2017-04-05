package com.michael.notedemo.paint;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.michael.notedemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Michael on 2017/3/25.
 */

public class PaintView extends View{
    private Paint mPaint;
    private Paint mEraserPaint;
    // width of screen
    private int width;
    // height of screen
    private int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Context context;
    public static boolean IsPaint = true;
    private float mX, mY;
    private boolean IsRecordPath = true;//是否储存笔画
    private OnPathListener listener;
    private final float TOUCH_TOLERANCE = 4;//移动误差
    private PathNode pathNode;
    private boolean IsShowing = false;
    private static final int INDIVIDE = 1;//handler判断的两种状态
    private static final int CHOOSEPATH = 0;

    public boolean isFirstTime() {
        return IsFirstTime;
    }
    public void setFirstTime(boolean isFirstTime){
        this.IsFirstTime = isFirstTime;
    }
    private boolean IsFirstTime = true;
    private Bitmap mBitmapInit;
    private int mBitmapBackGround = R.drawable.whitebackground;
    private ArrayList<PathNode.Node> ReDoNodes = new ArrayList<>();
    private boolean ReDoOrUnDoFlag = false;

    public PaintView(Context context) {
        this(context,null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mEraserPaint = new Paint();
        Init_Paint(PaintInfo.PaintColor,PaintInfo.PaintWidth);
        Init_Eraser(PaintInfo.EraserWidth);
        WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        width = manager.getDefaultDisplay().getWidth();
        height = manager.getDefaultDisplay().getHeight();
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    private void Init_Eraser(int width) {
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setColor(Color.WHITE);
        mEraserPaint.setStrokeWidth(width);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.SQUARE);
        // The most important
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    private void Init_Paint(int color, int width) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(width);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        if(IsPaint)
            canvas.drawPath(mPath, mPaint);
        else
            canvas.drawPath(mPath, mEraserPaint);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(IsPaint)
            Init_Paint(PaintInfo.PaintColor, PaintInfo.PaintWidth);
        else
            Init_Eraser(PaintInfo.EraserWidth);
    }
    private void Touch_Down(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        if(IsRecordPath) {
            listener.addNodeToPath(x, y, MotionEvent.ACTION_DOWN, IsPaint);
        }
    }


    private void Touch_Move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);//贝塞尔曲线
            mX = x;
            mY = y;
            if(IsFirstTime){
                IsFirstTime = false;
            }
            if(IsRecordPath) {
                listener.addNodeToPath(x, y, MotionEvent.ACTION_MOVE, IsPaint);
            }
        }
    }
    private void Touch_Up(Paint paint){
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, paint);
        mPath.reset();
        if(IsRecordPath) {
            listener.addNodeToPath(mX, mY, MotionEvent.ACTION_UP, IsPaint);
        }
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Touch_Down(x, y);
//                invalidate();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                Touch_Move(x, y);
//                invalidate();
//                break;
//
//            case MotionEvent.ACTION_UP:
//                if(IsPaint){
//                    Touch_Up(mPaint);
//                }else {
//                    Touch_Up(mEraserPaint);
//                }
//                invalidate();
//                break;
//        }
//        return true;
//    }
    //设置画笔颜色
    public void setColor(int color) {
        Toast.makeText(context,"已选择颜色" + colorToHexString(color),Toast.LENGTH_SHORT).show();
        mPaint.setColor(color);
        PaintInfo.PaintColor = color;
    }
    //设置画笔粗细
    public void setPenWidth(int width) {
        Toast.makeText(context,"设定笔粗为：" + width,Toast.LENGTH_SHORT).show();
        mPaint.setStrokeWidth(width);
        PaintInfo.PaintWidth = width;
    }
    //保存
    public void save(){
        mCanvas.save();
    }
    //设置为橡皮还是画笔
    public void setIsPaint(boolean isPaint) {
        IsPaint = isPaint;
    }
    //设置路径监听
    public void setOnPathListener(OnPathListener listener) {
        this.listener = listener;
    }
    //设置橡皮粗细
    public void setmEraserPaint(int width){
        Toast.makeText(context,"设定橡皮粗为："+width,Toast.LENGTH_SHORT).show();
        mEraserPaint.setStrokeWidth(width);
        PaintInfo.EraserWidth = width;
    }
    //设置是否储存画的路径及节点
    public void setIsRecordPath(boolean isRecordPath,PathNode pathNode) {
        this.pathNode = pathNode;
        IsRecordPath = isRecordPath;
    }
    //设置是否储存画的路径
    public void setIsRecordPath(boolean isRecordPath) {
        IsRecordPath = isRecordPath;
    }
    public boolean isShowing() {
        return IsShowing;
    }

    //int 转 16进制的图画颜色
    private static String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFFFF & color);
    }

    // switch eraser/paint
    public void Eraser(){
        Toast.makeText(context,"切换为橡皮",Toast.LENGTH_SHORT).show();
        IsPaint = false;
        Init_Eraser(PaintInfo.EraserWidth);
    }

    public void Paint(){
        Toast.makeText(context,"切换为铅笔",Toast.LENGTH_SHORT).show();
        IsPaint = true;
        Init_Paint(PaintInfo.PaintColor, PaintInfo.PaintWidth);
    }

    public Paint getmEraserPaint() {
        return mEraserPaint;
    }

    public Paint getmPaint() {
        return mPaint;
    }
    //reset画布
    public void clean() {
        mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        try {
            Message msg = new Message();
            msg.obj = PaintView.this;
            msg.what = INDIVIDE;
            handler.sendMessage(msg);
            Thread.sleep(0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        IsFirstTime = true;
    }
    //将图片设为画布背景
    public void setmBitmap(Uri uri){
        Log.e("图片路径", String.valueOf(uri));
        ContentResolver cr = context.getContentResolver();
        try {
            mBitmapInit = BitmapFactory.decodeStream(cr.openInputStream(uri));
            drawBitmapToCanvas(mBitmapInit);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        IsFirstTime = true;
        invalidate();
    }

    private void drawBitmapToCanvas(Bitmap bitmap){
        if(bitmap.getHeight() > height || bitmap.getWidth() > width){
            RectF rectF = new RectF(0,0,width,height);
            mCanvas.drawBitmap(bitmap, null, rectF, mBitmapPaint);
        }else {
            mCanvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
        }
    }
    public String BitmapToPicture(File file,int status){
        FileOutputStream fileOutputStream;
        File tempfile = null;
        try {
            if(status==0) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date now = new Date();
                tempfile = new File(file + "/" + formatter.format(now) + ".jpg");
            }
            else{
                tempfile = file;
            }
            fileOutputStream = new FileOutputStream(tempfile);
            Bitmap mBitmapbg = BitmapFactory.decodeResource(context.getResources(), mBitmapBackGround).
                    copy(Bitmap.Config.ARGB_8888, false);
            mBitmapbg = Bitmap.createScaledBitmap(mBitmapbg, width, height, false);
            if (mBitmapInit != null) {
                mBitmapbg = toConformBitmap(mBitmapbg, mBitmapInit);
                mBitmapbg = toConformBitmap(mBitmapbg, mBitmap);
            } else {
                mBitmapbg = toConformBitmap(mBitmapbg, mBitmap);
            }
            mBitmapbg.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tempfile.toString();
    }
    public void PathNodeToJson(PathNode pathNode,File file){
        ArrayList<PathNode.Node> arrayList = pathNode.getPathList();
        String json = "[";
        for(int i = 0;i < arrayList.size();i++){
            PathNode.Node node = arrayList.get(i);
            json += "{"+"\""+"x"+"\""+":"+node.x+"," +
                    "\""+"y"+"\""+":"+node.y+","+
                    "\""+"PenColor"+"\""+":"+node.PenColor+","+
                    "\""+"PenWidth"+"\""+":"+node.PenWidth+","+
                    "\""+"EraserWidth"+"\""+":"+node.EraserWidth+","+
                    "\""+"TouchEvent"+"\""+":"+node.TouchEvent+","+
                    "\""+"IsPaint"+"\""+":"+"\""+node.IsPaint+"\""+","+
                    "\""+"time"+"\""+":"+node.time+
                    "},";
        }
        json = json.substring(0,json.length()-1);
        json += "]";
        try {
            json = enCrypto(json, "michael");
        } catch (InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date now = new Date();
        File tempfile = new File(file+"/"+formatter.format(now)+".my");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(tempfile);
            byte[] bytes = json.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            Toast.makeText(context,tempfile.getName() + "已保存",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bitmap);
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        cv.drawBitmap(foreground, 0, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        cv.restore();//存储
        return bitmap;
    }
    public void clearReUnList(){
        ReDoNodes.clear();
        mBitmapInit = null;
    }

    public void JsonToPathNodeToHandle(Uri uri){
        Message message = new Message();
        message.obj = uri.getPath();
        message.what = CHOOSEPATH;
        handler.sendMessage(message);
    }
    //将pathnode从json转为PathNode
    private void JsonToPathNode(String file){
        String res = "";
        ArrayList<PathNode.Node> arrayList = new ArrayList<>();
        try {
            Log.e("绝对路径",file);
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for(int i = in.read(buffer, 0, buffer.length); i > 0 ; i = in.read(buffer, 0, buffer.length)) {
                bufferOut.write(buffer, 0, i);
            }
            res = new String(bufferOut.toByteArray(), Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            res = deCrypto(res, "michael");
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                PathNode.Node node = new PathNode().NewAnode();
                // dp
                node.x = jsonObject.getInt("x");
                node.y = jsonObject.getInt("y");
                node.TouchEvent = jsonObject.getInt("TouchEvent");
                node.PenWidth = jsonObject.getInt("PenWidth");
                node.PenColor = jsonObject.getInt("PenColor");
                node.EraserWidth = jsonObject.getInt("EraserWidth");
                node.IsPaint = jsonObject.getBoolean("IsPaint");
                node.time = jsonObject.getLong("time");
                arrayList.add(node);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pathNode.setPathList(arrayList);
    }
    //dp 像素转换
    public int px2dip(float pxValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public int dip2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(!isShowing()) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Touch_Down(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    Touch_Move(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    if (IsPaint) {
                        Touch_Up(mPaint);
                    } else {
                        Touch_Up(mEraserPaint);
                    }
                    invalidate();
                    break;
            }
        }
        return true;
    }
    public void preview(ArrayList<PathNode.Node> arrayList) {
        IsRecordPath = false;
        PreviewThread previewThread = new PreviewThread(this, arrayList);
        Thread thread = new Thread(previewThread);
        thread.start();
    }

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case INDIVIDE:
                    ((View) msg.obj).invalidate();
                    break;
                case CHOOSEPATH:
                    JsonToPathNode(msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }

    };
    class PreviewThread implements Runnable{
        private long time;
        private ArrayList<PathNode.Node> nodes;
        private View view;
        public PreviewThread(View view, ArrayList<PathNode.Node> arrayList) {
            this.view = view;
            this.nodes = arrayList;
        }
        public void run() {
            time = 0;
            IsShowing = true;
            clean();
            if(mBitmapInit != null){
                drawBitmapToCanvas(mBitmapInit);
            }
            for(int i = 0 ;i < nodes.size();i++) {
                PathNode.Node node = nodes.get(i);
                float x = dip2px(node.x);
                float y = dip2px(node.y);
                Log.e("pre"+x,"pre"+y);
                if(i < nodes.size() - 1) {
                    time = nodes.get(i+1).time - node.time;
                }
                IsPaint = node.IsPaint;
                if(node.IsPaint){
                    PaintInfo.PaintColor = node.PenColor;
                    PaintInfo.PaintWidth = node.PenWidth;
                    Init_Paint(node.PenColor,node.PenWidth);
                }else {
                    PaintInfo.EraserWidth = node.EraserWidth;
                    Init_Eraser(node.EraserWidth);
                }
                switch (node.TouchEvent) {
                    case MotionEvent.ACTION_DOWN:
                        Touch_Down(x,y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Touch_Move(x,y);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(node.IsPaint){
                            Touch_Up(mPaint);
                        }else {
                            Touch_Up(mEraserPaint);
                        }
                        break;
                }
                Message msg = new Message();
                msg.obj = view;
                msg.what = INDIVIDE;
                handler.sendMessage(msg);
                if(!ReDoOrUnDoFlag) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            ReDoOrUnDoFlag = false;
            IsShowing = false;
            IsRecordPath = true;
        }
    }
    //加密
    private static String enCrypto(String txt, String key)
            throws InvalidKeySpecException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {
        StringBuffer sb = new StringBuffer();
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory skeyFactory = null;
        Cipher cipher = null;
        try {
            skeyFactory = SecretKeyFactory.getInstance("DES");
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey deskey = skeyFactory != null ? skeyFactory.generateSecret(desKeySpec) : null;
        if (cipher != null) {
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
        }
        byte[] cipherText = cipher != null ? cipher.doFinal(txt.getBytes()) : new byte[0];
        for (int n = 0; n < cipherText.length; n++) {
            String stmp = (java.lang.Integer.toHexString(cipherText[n] & 0XFF));

            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString().toUpperCase();
    }
    //解密
    private static String deCrypto(String txt, String key)
            throws InvalidKeyException, InvalidKeySpecException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException {
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory skeyFactory = null;
        Cipher cipher = null;
        try {
            skeyFactory = SecretKeyFactory.getInstance("DES");
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey deskey = skeyFactory != null ? skeyFactory.generateSecret(desKeySpec) : null;
        if (cipher != null) {
            cipher.init(Cipher.DECRYPT_MODE, deskey);
        }
        byte[] btxts = new byte[txt.length() / 2];
        for (int i = 0, count = txt.length(); i < count; i += 2) {
            btxts[i / 2] = (byte) Integer.parseInt(txt.substring(i, i + 2), 16);
        }
        return (new String(cipher.doFinal(btxts)));
    }
    public void ReDoORUndo(boolean flag){
        if(!IsShowing) {
            ReDoOrUnDoFlag = true;
            try {
                if (flag) {
                    Log.e("redo","");
                    ReDoNodes.add(pathNode.getTheLastNote());
                    pathNode.deleteTheLastNote();
                    preview(pathNode.getPathList());
                    invalidate();
//					ReDoOrUnDoFlag = true;
//					if(!isShowing())
//						preview(pathNode.getPathList());
                } else {
                    Log.e("undo","");
                    pathNode.addNode(ReDoNodes.get(ReDoNodes.size() - 1));
                    ReDoNodes.remove(ReDoNodes.size() - 1);
                    preview(pathNode.getPathList());
//					ReDoOrUnDoFlag = true;
//					if(!isShowing())
//						preview(pathNode.getPathList());
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                Toast.makeText(context,"无法操作＝－＝",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void setPaintInfo(int color,int penWidth,int eraserWidth){
        PaintInfo.PaintColor = color;
        PaintInfo.PaintWidth = penWidth;
        PaintInfo.EraserWidth = eraserWidth;
    }
}
