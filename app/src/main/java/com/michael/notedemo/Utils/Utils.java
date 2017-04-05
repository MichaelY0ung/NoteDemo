package com.michael.notedemo.Utils;

/**
 * Created by Michael on 2017/2/21.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.michael.notedemo.R;

public class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getAddTabHeight(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
        LinearLayout linearlayout = (LinearLayout)view.findViewById(R.id.linearlayout);
        //measure方法的参数值都设为0即可
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        linearlayout.measure(w, h);
        Log.d("11111",linearlayout.getMeasuredHeight()+""+Utils.getToolbarHeight(context)+""+context.getResources().getDisplayMetrics().density);
        //获取组件宽度
        //获取组件高度
        return 2*linearlayout.getMeasuredHeight();
    }

//    public static int getTabsHeight(Context context) {
//        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
//    }
}
