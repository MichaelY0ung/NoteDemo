package com.michael.notedemo.paint;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.michael.notedemo.R;

/**
 * Created by Michael on 2017/3/24.
 */

public class sizeSel_fragment extends Fragment{
    private RadioButton radiobt_2;
    private RadioGroup radioGroup_size;
    private OnChangeSizeListener onChangeSizeListener;
    private int selectSize = 12;
    private int penSize;
    private int eraserSize;
    private int[] sizes = {6,12,18,24,30,36,42};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paint_sizesel, container,false);
        ImageButton imageButton = (ImageButton)view.findViewById(R.id.describe_image);
        final Bundle bundle = getArguments();
        imageButton.setImageResource(bundle.getInt("image"));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),bundle.getInt("image")==R.drawable.ic_create_blue_grey_600_24dp?"画笔粗细"+selectSize:"橡皮粗细"+selectSize,Toast.LENGTH_SHORT).show();
            }
        });
        radioGroup_size = (RadioGroup)view.findViewById(R.id.radio_group_size);
        radiobt_2 = (RadioButton)view.findViewById(R.id.paint_radio2);
        radiobt_2.setChecked(true);
        radioGroup_size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.paint_radio1:
                        selectSize = 6;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                    case R.id.paint_radio2:
                        selectSize = 12;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                    case R.id.paint_radio3:
                        selectSize = 18;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                    case R.id.paint_radio4:
                        selectSize = 24;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                    case R.id.paint_radio5:
                        selectSize = 30;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                    case R.id.paint_radio6:
                        selectSize = 36;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                    case R.id.paint_radio7:
                        selectSize = 42;
                        if(onChangeSizeListener!=null){
                            onChangeSizeListener.onSizeChange(selectSize);
                        }
                        break;
                }
            }
        });
        return view;
    }
    public interface OnChangeSizeListener{
        void onSizeChange(int width);
    }
    public void setOnChangeSizeListener(OnChangeSizeListener onChangeSizeListener){
        this.onChangeSizeListener = onChangeSizeListener;
    }
    public OnChangeSizeListener getOnChangeSizeListener(){
        return onChangeSizeListener;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            onChangeSizeListener = (OnChangeSizeListener) getActivity();
//        }
//        catch (ClassCastException e){
//            throw new ClassCastException(getActivity().toString() + " must implementOnChangeSizeListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        onChangeSizeListener = null;
    }
}
