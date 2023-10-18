package cn.westlan.coding.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.connect.PrintContext;
import cn.westlan.coding.core.bean.PrintStatus;
import cn.westlan.coding.control.ControlViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public class OperationFragment extends BaseFragment {

    private PrintContext printContext;
    private Disposable statusDisposable;
    @BindView(R.id.deviceStatus)
    TextView deviceStatusText;
    @BindView(R.id.currentPrintCount)
    TextView currentPrintCountText;
    @BindView(R.id.printCount)
    TextView printCountText;
    @BindView(R.id.packPerMinus)
    TextView packPerMinusText;
//    @BindView(R.id.remainLength)
//    TextView remainLengthText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_operation, container, false);
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
        //todo 初始化状态
        printContext.getPrintStatus().subscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        statusDisposable = printContext.observeStatusChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::refreshStatus);
    }

    @Override
    public void onPause() {
        super.onPause();
        statusDisposable.dispose();
    }

    private void refreshStatus(PrintStatus status){
        switch (status.getRunningState()){
            case Running:
                deviceStatusText.setText(R.string.running_state);
                deviceStatusText.setBackgroundColor(getResources().getColor(R.color.status_bar_running));
                break;
            case Stopped:
                deviceStatusText.setText(R.string.stopped_state);
                deviceStatusText.setBackgroundColor(getResources().getColor(R.color.status_bar_stopped));
                break;
            case Fault:
                deviceStatusText.setText(R.string.fault_state);
                deviceStatusText.setBackgroundColor(getResources().getColor(R.color.status_bar_fault));
                break;
            case Locked:
                deviceStatusText.setText(R.string.locked_state);
                deviceStatusText.setBackgroundColor(getResources().getColor(R.color.status_bar_locked));
                break;
        }
        currentPrintCountText.setText(String.valueOf(status.getCurrentPrintCount()));
        printCountText.setText(String.valueOf(status.getPrintCount()));
        packPerMinusText.setText(String.valueOf(status.getPackPerMinus()));
//        remainLengthText.setText(String.format("本次剩余色带长度%d%%", Integer.valueOf(status.getRemainLength())));
    }

    @OnClick(R.id.on)
    public void onOpenClick() {
        printContext.switchOn().flatMap(ignored->printContext.getPrintStatus()).subscribe();
    }

    @OnClick(R.id.off)
    public void onCloseClick() {
        printContext.switchOff().flatMap(ignored->printContext.getPrintStatus()).subscribe();
    }

    @OnClick(R.id.test)
    public void onTestClick() {
        printContext.print().subscribe();
    }

    @OnClick(R.id.printSettings)
    public void onSettingsClick() {
        NavHostFragment.findNavController(OperationFragment.this)
                .navigate(R.id.action_OperationFragment_to_SettingsFragment);
    }

    @OnClick(R.id.deviceInfo)
    public void onInfoClick() {
        NavHostFragment.findNavController(OperationFragment.this)
                .navigate(R.id.action_OperationFragment_to_DeviceInfoFragment);
    }
}