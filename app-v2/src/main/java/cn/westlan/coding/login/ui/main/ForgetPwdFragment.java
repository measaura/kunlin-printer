package cn.westlan.coding.login.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.leancloud.LCUser;
import cn.leancloud.sms.LCSMS;
import cn.leancloud.sms.LCSMSOption;
import cn.leancloud.types.LCNull;
import cn.leancloud.utils.StringUtil;
import cn.westlan.coding.R;
import cn.westlan.coding.control.view.OperatePopup;
import cn.westlan.coding.control.view.SelectItem;
import cn.westlan.coding.login.bean.UserInfo;
import cn.westlan.coding.login.bean.UserType;
import cn.westlan.coding.util.WaitingDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForgetPwdFragment extends BaseFragment {

    private LoginViewModel mViewModel;
    @BindView(R.id.etAccount)
    EditText etAccount;
    @BindView(R.id.etNewPwd)
    EditText etNewPwd;
    @BindView(R.id.etVCode)
    EditText etVCode;
    @BindView(R.id.tvGetVCode)
    TextView tvGetVCode;
    @BindView(R.id.tvModifyPwd)
    TextView tvModifyPwd;
    boolean requestVerify = false;

    public static ForgetPwdFragment newInstance() {
        return new ForgetPwdFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forget_pwd, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTitle(R.string.forget_pwd_title);
        tvGetVCode.setEnabled(false);
        tvModifyPwd.setEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    @OnTextChanged({R.id.etAccount})
    public void onAccountChanged(){
        if(StringUtil.isEmpty(etAccount.getText().toString())){
            tvGetVCode.setEnabled(false);
        }else{
            tvGetVCode.setEnabled(true);
        }
    }

    @OnTextChanged({R.id.etAccount, R.id.etVCode, R.id.etNewPwd})
    public void onTextChanged(){
        if(StringUtil.isEmpty(etAccount.getText().toString())
                ||StringUtil.isEmpty(etVCode.getText().toString())
                ||StringUtil.isEmpty(etNewPwd.getText().toString())){
            tvModifyPwd.setEnabled(false);
        }else{
            tvModifyPwd.setEnabled(true);
        }
    }

    @OnClick(R.id.tvGetVCode)
    public void onGetVCode() {
        final Dialog waitingDialog = WaitingDialog.show(getActivity(), "");
        LCUser.requestPasswordResetBySmsCodeInBackground(etAccount.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LCNull>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull LCNull lcNull) {
                        requestVerify = true;
                        Toast.makeText(getContext(), R.string.send_sms_successfully,  Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        waitingDialog.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        waitingDialog.dismiss();
                    }
                });
    }

    @OnClick(R.id.tvModifyPwd)
    public void onModifyPwd() {
        if(!requestVerify){
            Toast.makeText(getContext(), R.string.login_tip_send_sms, Toast.LENGTH_LONG).show();
        }
        final Dialog waitingDialog = WaitingDialog.show(getActivity(), getString(R.string.tip_waiting));
        LCUser.resetPasswordBySmsCodeInBackground(etVCode.getText().toString(), etNewPwd.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LCNull>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(LCNull lcNull) {
                        Toast.makeText(getContext(), R.string.modify_psw_successfully,  Toast.LENGTH_LONG).show();
                        NavHostFragment.findNavController(ForgetPwdFragment.this).popBackStack();
//                Log.i(getClass().getName(), "注册成功 user：" + LCUser.toString());
                    }
                    @Override
                    public void onError(Throwable e) {
                        waitingDialog.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e(getClass().getName(), "register exception",e.getCause());
                    }
                    @Override
                    public void onComplete() {
                        waitingDialog.dismiss();
                    }
                });
    }
}