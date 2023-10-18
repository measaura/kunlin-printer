package cn.westlan.coding.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.core.connect.PrintContext;
import com.dilusense.customkeyboard.KeyboardIdentity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public class PasswordModifyFragment extends BaseFragment {

    private PrintContext printContext;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    @BindView(R.id.password1)
    EditText password1;
    @BindView(R.id.password2)
    EditText password2;
    private KeyboardIdentity keyboardIdentity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_modify, container, false);
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
        keyboardIdentity = new KeyboardIdentity(getActivity());

    }

//    @OnTouch(R.id.password1)
//    public void onClickPassword1(){
//        keyboardIdentity.attachTo(password1);
//    }
//
//    @OnTouch(R.id.password2)
//    public void onClickPassword2(){
//        keyboardIdentity.attachTo(password2);
//    }

    @OnClick(R.id.confirmModify)
    public void onModify() {
        CharSequence newPassword = password1.getText();
        CharSequence newPassword2 = password2.getText();
        if(!isNotEmpty(newPassword)||!isNotEmpty(newPassword2)){
            Toast.makeText(this.getContext(),  getString(R.string.new_password_illegal_tip),Toast.LENGTH_LONG).show();
            return;
        }
        if(!newPassword.toString().contentEquals(newPassword2)){
            Toast.makeText(this.getContext(),  getString(R.string.entered_passwords_differ),Toast.LENGTH_LONG).show();
            return;
        }
        Disposable disposable = printContext.modifyPassword(newPassword.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    String text = result?getString(R.string.password_changed):getString(R.string.change_password_failed);
                    Toast.makeText(this.getContext(),  text,Toast.LENGTH_LONG).show();
                    if(result){
                        NavHostFragment.findNavController(this).popBackStack();
                    }
                });
        compositeDisposable.add(disposable);
    }

    private boolean isNotEmpty(CharSequence charSequence){
        return charSequence!=null&&charSequence.length()==4;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        compositeDisposable.dispose();
    }
}