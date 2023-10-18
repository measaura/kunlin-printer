package cn.westlan.coding.control.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.bean.Firmware;
import cn.westlan.coding.core.bean.Identifier;
import cn.westlan.coding.core.connect.PrintContext;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.update.UpdateActivity;
import cn.westlan.coding.util.EditDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public class MainFragment extends BaseFragment {
    private PrintContext printContext;
    private Firmware firmware;
    private Identifier identifier;
    private boolean locked;

    @BindView(R.id.control)
    TextView controlButton;
    @BindView(R.id.content)
    TextView contentButton;
    @BindView(R.id.disconnect)
    TextView disconnectButton;
    @BindView(R.id.update)
    TextView updateButton;
    @BindView(R.id.passwordModify)
    TextView passwordModifyButton;
    @BindView(R.id.unlock)
    TextView unlockButton;

    @Override
    public void onResume() {
        super.onResume();
        this.setHasOptionsMenu(false);
        setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView "+getClass().getName());
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    //onCreate ->  onCreateView -> onActivityCreated
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ControlViewModel viewModel = new ViewModelProvider(getActivity()).get(ControlViewModel.class);
        printContext = viewModel.getPrintContext();
        firmware = viewModel.getFirmware();
        identifier = viewModel.getIdentifier();
        setLockState(viewModel.isLocked());
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.control)
    public void onControlClick() {
        NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_MainFragment_to_OperationFragment);
    }

    @OnClick(R.id.content)
    public void onContentClick() {
        NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_MainFragment_to_ContentFragment);
    }

    @OnClick(R.id.disconnect)
    public void onDisconnectClick() {
        printContext.close();
    }

    @OnClick(R.id.update)
    public void onUpdateClick() {
        this.getActivity().finish();
        startActivity(UpdateActivity.startActivityIntent(
                this.getActivity().getApplicationContext(),
                printContext.getBleDevice().getMacAddress(),
                printContext.getBleDevice().getName(),
                firmware.getVersion(), identifier.getCheckUpdateUrl()));
    }

    @OnClick(R.id.passwordModify)
    public void onPasswordModifyClick() {
        NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_MainFragment_to_PasswordModifyFragment);
    }

    @OnClick(R.id.unlock)
    public void onUnlockClick() {
        EditDialog.show(this.getContext(), getString(R.string.unlock), value -> {
            Disposable disposable = printContext.unlock(value)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(result->{
                if(!result){
                    Toast.makeText(this.getContext(),  getString(R.string.unlock_failed),Toast.LENGTH_LONG).show();
                }else{
                    setLockState(false);
                }
            }, throwable -> {
                Log.i(getClass().getSimpleName(), getString(R.string.unlock_failed), throwable);
                Toast.makeText(this.getContext(),  getString(R.string.unlock_failed),Toast.LENGTH_LONG).show();
            });
        });
    }

    private void setLockState(boolean locked){
        this.locked = locked;
        if(locked){
            controlButton.setVisibility(View.GONE);
            contentButton.setVisibility(View.GONE);
            disconnectButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.GONE);
            passwordModifyButton.setVisibility(View.GONE);
            unlockButton.setVisibility(View.VISIBLE);
        }else{
            controlButton.setVisibility(View.VISIBLE);
            contentButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.VISIBLE);
            passwordModifyButton.setVisibility(View.VISIBLE);
            unlockButton.setVisibility(View.INVISIBLE);
        }
    }
}