package cn.westlan.coding.control.dialog.date;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import butterknife.*;
import cn.forward.androids.views.StringScrollPicker;
import cn.westlan.coding.R;
import cn.westlan.coding.core.panel.block.AdjustDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AdjustDialog extends AlertDialog {

    @BindView(R.id.adjustPattern)
    StringScrollPicker adjustPatternPicker;
    @BindArray(R.array.datePattern)
    String [] datePatterns;
    private final int adjustSize = 3;
    private final TextView[] textViews = new TextView[adjustSize];
    private final int[] adjustValues = new int[adjustSize];
    @BindView(R.id.adjustPicker)
    StringScrollPicker adjustPicker;
    private String pattern;
    private Callback callback;
    private int currentAdjustItem = 0;

    protected AdjustDialog(Context context, Callback callback) {
        super(context, true, dialogInterface->{});
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_adjust);
        ButterKnife.bind(this);
        this.setTitle(R.string.date_offset);
        this.pattern = datePatterns[0];
        textViews[0] = findViewById(R.id.yyyy);
        textViews[1] = findViewById(R.id.mm);
        textViews[2] = findViewById(R.id.dd);
        for(int i = 0 ; i < adjustSize; ++i){
            textViews[i].setBackgroundResource(R.drawable.color_unchecked);
            textViews[i].getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            adjustValues[i] = 0;
        }
        onAdjustItemChanged(1);
        initAdjustPicker(0);
        adjustPicker.setOnSelectedListener((scrollPickerView, i) -> {
            adjustValues[currentAdjustItem] = i;
        });

        this.adjustPatternPicker.setData(Arrays.asList(datePatterns));
        this.adjustPatternPicker.setOnSelectedListener((scrollPickerView, i) -> {
            this.pattern = datePatterns[i];
        });
    }

    @OnClick({R.id.yyyy, R.id.mm, R.id.dd})
    public void onItemClick(View view) {
        for(int i = 0; i < textViews.length; ++i){
            if(view.equals(textViews[i])){
                onAdjustItemChanged(i);
                return;
            }
        }
    }

    private void onAdjustItemChanged(int newValue){
        textViews[currentAdjustItem].setBackgroundResource(R.drawable.color_unchecked);
        textViews[newValue].setBackgroundResource(R.drawable.color_checked);
        currentAdjustItem = newValue;
        initAdjustPicker(adjustValues[newValue]);
    }

    private void initAdjustPicker(int position){
        List<String> adjustList = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            adjustList.add("+"+i);
        }
        adjustPicker.setData(adjustList);
        adjustPicker.setSelectedPosition(position);
    }

    @OnClick(R.id.adjustConfirm)
    public void onConfirmClick() {
        if(callback != null){
            callback.callback(pattern, new AdjustDate(adjustValues[0], adjustValues[1], adjustValues[2]));
        }
        dismiss();
    }

    @OnClick(R.id.adjustCancel)
    public void onCancelClick() {
        dismiss();
    }
}