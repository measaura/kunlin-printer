package cn.westlan.coding.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.forward.androids.views.StringScrollPicker;
import cn.westlan.coding.R;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.core.bean.PrintDirection;
import cn.westlan.coding.core.bean.PrintParams;
import cn.westlan.coding.core.bean.PrintTemp;
import cn.westlan.coding.core.connect.PrintContext;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends BaseFragment {

    private PrintContext printContext;
    private PrintParams printParams = new PrintParams();
    private Disposable disposable;
    @BindView(R.id.printTemp)
    StringScrollPicker printTemp;
    @BindView(R.id.printPress)
    StringScrollPicker printPress;
    @BindView(R.id.printDirection)
    Switch printDirection;
    @BindView(R.id.printDelay)
    StringScrollPicker printDelay;
    @BindView(R.id.printPosition)
    StringScrollPicker printPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        //设置范围 -> view对应;
        //view 修改监听 动态修改设置
//        printTemp.setData(Arrays.asList("小", "中", "大"));
//        printTemp.setOnSelectedListener((scrollPickerView, i) -> {
//            if(i == 0){
//                onModifyTemp(PrintTempBak.Low);
//            }else if(i == 1){
//                onModifyTemp(PrintTempBak.Middle);
//            }else if(i == 2){
//                onModifyTemp(PrintTempBak.High);
//            }
//        });
        printTemp.setData(generateList(1, 9, 1, ""));
        printTemp.setOnSelectedListener((scrollPickerView, i) -> {
            onModifyTemp(PrintTemp.parse((byte) (i+1)));
        });
        printPress.setData(generateList(1, 20, 1, ""));
        printPress.setOnSelectedListener((scrollPickerView, i) -> {
            onModifyPressure(i+1);
        });
        printDirection.setOnCheckedChangeListener(((compoundButton, b) -> {
            onModifyDirection(b? PrintDirection.Negative:PrintDirection.Positive);
        }));
        String unit = "";
        printDelay.setData(generateList(0, 200, 5, unit));
        printDelay.setOnSelectedListener((scrollPickerView, i) -> {
            String selectText = (String) scrollPickerView.getData().get(i);
            String selectValue = selectText.substring(0, selectText.length()-unit.length());
            onModifyDelay(Integer.valueOf(selectValue));
        });
        printPosition.setData(generateList(0, 10, 0.1, ""));
        printPosition.setOnSelectedListener((scrollPickerView, i) -> {
            onModifyPosition(i);
        });

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printContext = new ViewModelProvider(getActivity()).get(ControlViewModel.class).getPrintContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        //初始化打印设置
        disposable = printContext.getPrintParams()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::initParams);
    }

    @Override
    public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    private void initParams(PrintParams printParams){
        this.printParams = printParams;
//        switch (printParams.getTemperature()){
//            case Low:
//                printTemp.setSelectedPosition(0);
//                break;
//            case Middle:
//                printTemp.setSelectedPosition(1);
//                break;
//            case High:
//                printTemp.setSelectedPosition(2);
//                break;
//        }
        printTemp.setSelectedPosition(printParams.getTemperature().value()-1);
        printPress.setSelectedPosition(printParams.getPressure()-1);
        printDirection.setChecked(PrintDirection.Negative.equals(printParams.getDirection()));
        printDelay.setSelectedPosition(getPosition(printDelay.getData(), String.format(Locale.US,"%d", 0x00ff&printParams.getDelay())));
        printPosition.setSelectedPosition(printParams.getPosition());
    }

    private void onModifyTemp(PrintTemp newVal){
        if(newVal.equals(this.printParams.getTemperature()))return;
        this.printParams.setTemperature(newVal);
        printContext.setPrintParams(printParams).subscribe();
    }

    private void onModifyPressure(Integer newVal){
        if(newVal.byteValue()==this.printParams.getPressure())return;
        this.printParams.setPressure(newVal.byteValue());
        printContext.setPrintParams(printParams).subscribe();
    }
    private void onModifyDirection(PrintDirection newVal){
        if(newVal.equals(this.printParams.getDirection()))return;
        this.printParams.setDirection(newVal);
        printContext.setPrintParams(printParams).subscribe();
    }
    private void onModifyDelay(Integer newVal){
        if(newVal.byteValue()==this.printParams.getDelay())return;
        this.printParams.setDelay(newVal.byteValue());
        printContext.setPrintParams(printParams).subscribe();
    }
    private void onModifyPosition(Integer newVal){
        if(newVal.byteValue() == this.printParams.getPosition())return;
        this.printParams.setPosition(newVal.byteValue());
        printContext.setPrintParams(printParams).subscribe();
    }

    private List<String> generateList(double minVal, double maxVal, double interval, String unit){
        List<String> list = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        for(double i = minVal; i <= maxVal; i+=interval){
            list.add(decimalFormat.format(i)+unit);
        }
        return list;
    }

    private <T> int getPosition(List<T> list, T val){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).equals(val)){
                return i;
            }
        }
        return 0;
    }
}