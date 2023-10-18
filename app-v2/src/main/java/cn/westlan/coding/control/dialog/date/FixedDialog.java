package cn.westlan.coding.control.dialog.date;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import butterknife.*;
import cn.forward.androids.views.StringScrollPicker;
import cn.westlan.coding.R;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

class FixedDialog extends AlertDialog {

    @BindView(R.id.fixedPattern)
    StringScrollPicker fixedPicker;
    @BindView(R.id.fixedDate)
    FrameLayout fixedDateView;
    @BindArray(R.array.datePattern)
    String [] datePatterns;
    private String pattern;
    private Callback callback;
    private TimePickerView timePickerView;

    protected FixedDialog(Context context, Callback callback) {
        super(context, true, dialogInterface->{});
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fixed);
        ButterKnife.bind(this);
        this.setTitle(R.string.date_value);
        this.pattern = datePatterns[0];
        this.fixedPicker.setData(Arrays.asList(datePatterns));
        this.fixedPicker.setOnSelectedListener((scrollPickerView, i) -> {
            this.pattern = datePatterns[i];
        });
        initTimePicker();
    }

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
//        startDate.set(2013, 0, 23);

        Calendar endDate = Calendar.getInstance();
        endDate.set(startDate.get(Calendar.YEAR)+31, 12, 31);
        //时间选择器
        timePickerView = new TimePickerBuilder(this.getContext(), (date, v) -> {
            if(callback!= null){
                callback.callback(pattern, date);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_time, v -> {})
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDecorView(fixedDateView)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setOutSideColor(0x00000000)
                .setOutSideCancelable(false)
                .build();
        timePickerView.setKeyBackCancelable(false);//系统返回键监听屏蔽掉
        timePickerView.show(fixedDateView, false);
    }
//
//    @OnItemSelected(R.id.fixedPattern)
//    public void onPatternSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//    }

    @OnClick(R.id.fixedConfirm)
    public void onConfirmClick() {
        timePickerView.returnData();
        dismiss();
    }

    @OnClick(R.id.fixedCancel)
    public void onCancelClick() {
        dismiss();
    }
}