package com.michael.notedemo.paint;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michael.notedemo.R;
import com.skydoves.colorpickerview.ColorPickerView;

import java.util.ArrayList;

/**
 * Created by Michael on 2017/3/24.
 */

public class colorSel_fragment extends Fragment{
    private RadioGroup radioGroup_color;
    private CircleView paintColor1;
    private CircleView paintColor2;
    private CircleView paintColor3;
    private CircleView paintColor4;
    private CircleView paintColor5;
    private CircleView paintColor6;
    private CircleView paintColor7;
    private CircleView selectCircle;
    private RadioButton radiobt_1;
    private RadioButton radiobt_2;
    private RadioButton radiobt_3;
    private RadioButton radiobt_4;
    private RadioButton radiobt_5;
    private RadioButton radiobt_6;
    private RadioButton radiobt_7;
    public OnChangeColorListener onChangeColorListener;
    private ImageButton paintSelect;
    private int colorPicked;
    private String colorMsg;
    private ArrayList<CircleView> circleLsit = new ArrayList<>();
    private ArrayList<RadioButton> radioList = new ArrayList<>();
    private String[] colorsData;
    private String[] colors = new String[7];
    private RadioButton selectRadio;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paint_colorsel, container,false);
        paintColor1 = (CircleView)view.findViewById(R.id.paint_color1);
        paintColor2 = (CircleView)view.findViewById(R.id.paint_color2);
        paintColor3 = (CircleView)view.findViewById(R.id.paint_color3);
        paintColor4 = (CircleView)view.findViewById(R.id.paint_color4);
        paintColor5 = (CircleView)view.findViewById(R.id.paint_color5);
        paintColor6 = (CircleView)view.findViewById(R.id.paint_color6);
        paintColor7 = (CircleView)view.findViewById(R.id.paint_color7);
        circleLsit.add(paintColor1);
        circleLsit.add(paintColor2);
        circleLsit.add(paintColor3);
        circleLsit.add(paintColor4);
        circleLsit.add(paintColor5);
        circleLsit.add(paintColor6);
        circleLsit.add(paintColor7);
        radioList.add(radiobt_1);
        radioList.add(radiobt_2);
        radioList.add(radiobt_3);
        radioList.add(radiobt_4);
        radioList.add(radiobt_5);
        radioList.add(radiobt_6);
        radioList.add(radiobt_7);
        final Bundle bundle = getArguments();
        if(bundle!=null&&bundle.getString("color")!=null) {
            colorMsg = bundle.getString("color");
            colorsData = colorMsg.split("\\|");
        }
        if(colorsData!=null) {
            for (int i = 0; i < colorsData.length; i++) {
                circleLsit.get(i).setBackgroundColor(Color.parseColor(colorsData[i]));
            }
        }
        for (int i=0;i<circleLsit.size();i++){
            colors[i] = "#"+Integer.toHexString(circleLsit.get(i).getBackgroundColor());
        }
        paintSelect = (ImageButton)view.findViewById(R.id.paint_colorpick);
        radiobt_1 = (RadioButton)view.findViewById(R.id.paint_radio1);
        radiobt_2 = (RadioButton)view.findViewById(R.id.paint_radio2);
        radiobt_3 = (RadioButton)view.findViewById(R.id.paint_radio3);
        radiobt_4 = (RadioButton)view.findViewById(R.id.paint_radio4);
        radiobt_5 = (RadioButton)view.findViewById(R.id.paint_radio5);
        radiobt_6 = (RadioButton)view.findViewById(R.id.paint_radio6);
        radiobt_7 = (RadioButton)view.findViewById(R.id.paint_radio7);
        radioGroup_color = (RadioGroup)view.findViewById(R.id.radio_group_color);
        radiobt_1.setChecked(true);
        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            onChangeColorListener = (OnChangeColorListener) getActivity();
//        }
//        catch (ClassCastException e){
//            throw new ClassCastException(getActivity().toString() + " must implementOnChangeColorListener");
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectCircle = paintColor1;
        if(onChangeColorListener!=null) {
            //Log.d("22222",selectCircle.getBackgroundColor()+"");
            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
        }
        radioGroup_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.paint_radio1:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor1.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor1;
                        selectRadio = radiobt_1;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                    case R.id.paint_radio2:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor2.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor2;
                        selectRadio = radiobt_2;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                    case R.id.paint_radio3:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor3.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor3;
                        selectRadio = radiobt_3;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                    case R.id.paint_radio4:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor4.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor4;
                        selectRadio = radiobt_4;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                    case R.id.paint_radio5:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor5.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor5;
                        selectRadio = radiobt_5;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                    case R.id.paint_radio6:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor6.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor6;
                        selectRadio = radiobt_6;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                    case R.id.paint_radio7:
                        Toast.makeText(getActivity(),"#"+Integer.toHexString(paintColor7.getBackgroundColor()).substring(2),Toast.LENGTH_SHORT).show();
                        selectCircle = paintColor7;
                        selectRadio = radiobt_7;
                        if(onChangeColorListener!=null){
                            onChangeColorListener.onColorChange(selectCircle.getBackgroundColor());
                        }
                        break;
                }
            }
        });
        paintSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker, null,false);
                TextView textViewBefore = (TextView)view.findViewById(R.id.colorbeforeText);
                View colorBefore = (View)view.findViewById(R.id.colorbefore);
                final View colorlatter = (View)view.findViewById(R.id.colorlatter);
                colorBefore.setBackgroundColor(selectCircle.getBackgroundColor());
                textViewBefore.setText("目前颜色：\n#"+exChange(Integer.toHexString(selectCircle.getBackgroundColor()).substring(2)));
                final TextView textViewLatter = (TextView)view.findViewById(R.id.colorlatterText);
                final ColorPickerView colorPicker = (ColorPickerView)view.findViewById(R.id.colorPicker);
                colorPicker.setColorListener(new ColorPickerView.ColorListener() {
                    @Override
                    public void onColorSelected(int color) {
                        textViewLatter.setText("选择颜色：\n#"+colorPicker.getColorHtml());
                        colorlatter.setBackgroundColor(colorPicker.getColor());
                        colorPicked = colorPicker.getColor();
                        //Log.d("111111",colorPicker.getColorHtml()+"  "+Integer.toHexString(colorPicked));
                    }
                });
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("请选择颜色")
                        .customView(view,true)
                        .positiveText("确定")
                        .negativeText("取消")
                        .cancelable(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                selectCircle.setBackgroundColor(colorPicked);
                                onChangeColorListener.onColorChange(colorPicked);
                                changeColorMsg(colorPicked,selectCircle);
                                createColorMsg();
                                onChangeColorListener.onGetColor(colorMsg);
                            }
                        })
                        .build();
                dialog.show();
            }
        });
    }

    private void changeColorMsg(int colorPicked, CircleView selectCircle) {
        switch (selectCircle.getId()){
            case R.id.paint_color1:
                colors[0] = "#"+Integer.toHexString(colorPicked);
                break;
            case R.id.paint_color2:
                colors[1] = "#"+Integer.toHexString(colorPicked);
                break;
            case R.id.paint_color3:
                colors[2] = "#"+Integer.toHexString(colorPicked);
                break;
            case R.id.paint_color4:
                colors[3] = "#"+Integer.toHexString(colorPicked);
                break;
            case R.id.paint_color5:
                colors[4] = "#"+Integer.toHexString(colorPicked);
                break;
            case R.id.paint_color6:
                colors[5] = "#"+Integer.toHexString(colorPicked);
                break;
            case R.id.paint_color7:
                colors[6] = "#"+Integer.toHexString(colorPicked);
                break;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        onChangeColorListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void createColorMsg() {
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<colors.length-1;i++){
            sb.append(colors[i]+"|");
        }
        sb.append(colors[colors.length-1]);
        colorMsg = sb.toString();
    }

    public interface OnChangeColorListener{
        void onColorChange(int color);
        void onGetColor(String color);
    }
    public void setOnChangeColorListener(OnChangeColorListener onChangeColorListener){
        this.onChangeColorListener = onChangeColorListener;
    }
    public OnChangeColorListener getOnChangeColorListener(){
        return onChangeColorListener;
    }
    public static String exChange(String str){
        StringBuffer sb = new StringBuffer();
        if(str!=null){
            for(int i=0;i<str.length();i++){
                char c = str.charAt(i);
                if(Character.isUpperCase(c)){
                    sb.append(Character.toLowerCase(c));
                }else if(Character.isLowerCase(c)){
                    sb.append(Character.toUpperCase(c));
                }
                else{
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

}
