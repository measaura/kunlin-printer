package cn.westlan.coding.login.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.leancloud.LCCloud;
import cn.leancloud.LCUser;
import cn.leancloud.LeanCloud;
import cn.leancloud.sms.LCSMS;
import cn.leancloud.sms.LCSMSOption;
import cn.leancloud.types.LCNull;
import cn.leancloud.utils.StringUtil;
import cn.westlan.coding.R;
import cn.westlan.coding.control.editor.EditorAttributeFragment;
import cn.westlan.coding.control.view.OperatePopup;
import cn.westlan.coding.control.view.OptionsPopup;
import cn.westlan.coding.control.view.SelectItem;
import cn.westlan.coding.databinding.FragmentRegisterBinding;
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

public class RegisterFragment extends BaseFragment {

    private LoginViewModel mViewModel;
    private FragmentRegisterBinding binding;
    @BindView(R.id.etVCode)
    EditText etVCode;
    @BindView(R.id.tvGetVCode)
    TextView tvGetVCode;
    @BindView(R.id.tvUserType)
    TextView tvUserType;
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    boolean requestVerify = false;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        binding.setUser(new UserInfo());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTitle(R.string.register_title);
        tvGetVCode.setEnabled(false);
        tvRegister.setEnabled(false);
//        UserInfo userInfo = new UserInfo();
//        userInfo.setAccount("15158113800");
////        userInfo.setPassword("123456");
//        binding.setUser(userInfo);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    @OnTextChanged({R.id.etAccount})
    public void onAccountChanged(){
        UserInfo userInfo = binding.getUser();
        if(StringUtil.isEmpty(userInfo.getAccount())){
            tvGetVCode.setEnabled(false);
        }else{
            tvGetVCode.setEnabled(true);
        }
    }

    @OnTextChanged({R.id.etAccount, R.id.etVCode, R.id.etPwd, R.id.etName, R.id.etEmail, R.id.etCompanyName, R.id.tvUserType})
    public void onTextChanged(){
        UserInfo userInfo = binding.getUser();
        if(StringUtil.isEmpty(userInfo.getAccount())
                ||StringUtil.isEmpty(etVCode.getText().toString())
                ||StringUtil.isEmpty(userInfo.getPassword())
                ||StringUtil.isEmpty(userInfo.getName())
                ||StringUtil.isEmpty(userInfo.getEmail())
                ||StringUtil.isEmpty(userInfo.getCompanyName())
                ||StringUtil.isEmpty(userInfo.getUserType())){
            tvRegister.setEnabled(false);
        }else{
            tvRegister.setEnabled(true);
        }
    }

    @OnClick(R.id.tvUserType)
    public void onUserTypeSelect() {
        List<SelectItem<String>> items = Arrays.stream(UserType.values())
                .map(e->new SelectItem<>(e.getValue(), e.getValue(), false))
                .collect(Collectors.toList());
        new OperatePopup<String>(getActivity()).show(items, value->{
            tvUserType.setText(value);
        });
    }

    @OnClick(R.id.tvGetVCode)
    public void onGetVCode() {
        UserInfo userInfo = binding.getUser();
        final Dialog waitingDialog = WaitingDialog.show(getActivity(), "");
//        LCUser.requestMobilePhoneVerifyInBackground(userInfo.getAccount())
        LCSMS.requestSMSCodeInBackground(userInfo.getAccount(), new LCSMSOption())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LCNull>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull LCNull lcNull) {
                        requestVerify = true;
                        Toast.makeText(getContext(), R.string.send_sms_successfully, Toast.LENGTH_LONG).show();
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

    @OnClick(R.id.tvRegister)
    public void onRegister() {
        if(!requestVerify){
            Toast.makeText(getContext(), R.string.login_tip_send_sms, Toast.LENGTH_LONG).show();
        }
        UserInfo userInfo = binding.getUser();
        final Dialog waitingDialog = WaitingDialog.show(getActivity(), getString(R.string.tip_waiting));
//        LCUser.verifyMobilePhoneInBackground(etVCode.getText().toString())
        LCSMS.verifySMSCodeInBackground(etVCode.getText().toString(), userInfo.getAccount())
                .flatMap(lcNull->{
                    final LCUser user = new LCUser();
                    user.setUsername(userInfo.getAccount());
                    user.setPassword(userInfo.getPassword());
                    user.setEmail(userInfo.getEmail());
                    user.setMobilePhoneNumber(userInfo.getAccount());
                    user.put("name", userInfo.getName());
                    user.put("companyName", userInfo.getCompanyName());
                    user.put("userType", userInfo.getUserType());
                    return user.signUpInBackground();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LCUser>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(LCUser LCUser) {
                Toast.makeText(getContext(), R.string.register_successfully,  Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
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