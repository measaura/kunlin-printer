package cn.westlan.coding.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.connect.PrintContext;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.util.EditDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public class DeviceInfoFragment extends BaseFragment {

    private PrintContext printContext;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    @BindView(R.id.deviceName)
    TextView deviceNameText;
    @BindView(R.id.productLine)
    TextView productLineText;
    @BindView(R.id.factoryNo)
    TextView factoryNoText;
    @BindView(R.id.resistance)
    TextView resistanceText;
    @BindView(R.id.serialNO)
    TextView serialNameText;
    @BindView(R.id.firmware)
    TextView firmwareText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deviceinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printContext = new ViewModelProvider(getActivity()).get(ControlViewModel.class).getPrintContext();

        Disposable disposable = printContext.getDeviceName()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceName->deviceNameText.setText(deviceName));
        compositeDisposable.add(disposable);
        disposable = printContext.getProductLine()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(productLine->productLineText.setText(productLine));
        compositeDisposable.add(disposable);
        disposable = printContext.getFactoryNo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(factoryNo->factoryNoText.setText(factoryNo));
        compositeDisposable.add(disposable);
        disposable = printContext.getSerialNumber()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(serialName->serialNameText.setText(serialName));
        compositeDisposable.add(disposable);
        disposable = printContext.getFirmware()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(firmware->firmwareText.setText(firmware.getVersion()));
        compositeDisposable.add(disposable);
        disposable = printContext.getResistance()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resistance->resistanceText.setText(String.valueOf(resistance)));
        compositeDisposable.add(disposable);
    }

    @OnClick(R.id.editDeviceName)
    public void onEditDeviceName() {
        EditDialog.show(getActivity(), "打印机名称", value -> {
            Disposable disposable = printContext.setDeviceName(value)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result->{
                        if(result){
                            deviceNameText.setText(value);
                        }
                    });
            compositeDisposable.add(disposable);
        });
    }

    @OnClick(R.id.editFactoryNo)
    public void onEditFactoryNo() {
        EditDialog.show(getActivity(), "工厂编号", value -> {
            Disposable disposable = printContext.setFactoryNo(value)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result->{
                        if(result){
                            factoryNoText.setText(value);
                        }
                    });
            compositeDisposable.add(disposable);
        });
    }

    @OnClick(R.id.editProductLine)
    public void onEditProductLine() {
        EditDialog.show(getActivity(), "生产线编号", value -> {
            Disposable disposable = printContext.setProductLine(value)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result->{
                        if(result){
                            productLineText.setText(value);
                        }
                    });
            compositeDisposable.add(disposable);
        });
    }

    @OnClick(R.id.editResistance)
    public void onEditResistance() {
        EditDialog.show(getActivity(), getString(R.string.resistance), getString(R.string.resistance_hint), EditorInfo.TYPE_CLASS_NUMBER, value -> {
            int number = Integer.parseInt(value);
            if(number < 850 ){
                Toast.makeText(getActivity(), getString(R.string.tip_resistance_small), Toast.LENGTH_LONG).show();
                return;
            }else if(number > 1455){
                Toast.makeText(getActivity(), getString(R.string.tip_resistance_big), Toast.LENGTH_LONG).show();
                return;
            }
            Disposable disposable = printContext.setResistance((short) number)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result->{
                        if(result){
                            resistanceText.setText(value);
                        }
                    });
            compositeDisposable.add(disposable);
        });
    }
}