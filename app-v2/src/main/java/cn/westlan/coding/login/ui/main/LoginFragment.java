package cn.westlan.coding.login.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import cn.leancloud.LeanCloud;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;
import cn.westlan.coding.control.fragment.MainFragment;
import cn.westlan.coding.util.WaitingDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;

public class LoginFragment extends BaseFragment {

    private LoginViewModel mViewModel;
    @BindView(R.id.etAccount)
    EditText etAccount;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.tvLogin)
    TextView tvLogin;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTitle(R.string.login_title);
        tvLogin.setEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    @OnTextChanged({R.id.etAccount, R.id.etPwd})
    public void onTextChanged(){
        if(etAccount.getText().length() == 0 || etPwd.getText().length() == 0){
            tvLogin.setEnabled(false);
        }else{
            tvLogin.setEnabled(true);
        }
    }

    @OnClick(R.id.tvLogin)
    public void onLoginClick() {
        final Dialog waitingDialog = WaitingDialog.show(getActivity(), "正在登录...");
        LCUser.logIn(etAccount.getText().toString() ,etPwd.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LCUser>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(LCUser lcUser) {
                Toast.makeText(getContext(), R.string.login_sucessfully,  Toast.LENGTH_LONG).show();
                PrinterApplication.setUser(getContext(), lcUser);
                getActivity().finish();
//                Log.i(getClass().getName(), "登录成功 user：" + LCUser.toString());
            }
            @Override
            public void onError(Throwable e) {
                waitingDialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                Log.i(getClass().getName(), e.getMessage());
            }
            @Override
            public void onComplete() {
                waitingDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.tvForgetPwd)
    public void onForgetPwdClick() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_LoginFragment_to_ForgetPwdFragment);
    }

    @OnClick(R.id.tvRegister)
    public void onRegisterClick() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_LoginFragment_to_RegisterFragment);
    }

}