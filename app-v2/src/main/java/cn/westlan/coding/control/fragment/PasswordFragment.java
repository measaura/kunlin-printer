package cn.westlan.coding.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.westlan.coding.R;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.core.connect.PrintContext;
import com.al.open.OnInputListener;
import com.al.open.SplitEditTextView;
import com.dilusense.customkeyboard.BaseKeyboard;
import com.dilusense.customkeyboard.KeyboardUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public class PasswordFragment extends BaseFragment {

    private PrintContext printContext;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.splitEdit1)
    SplitEditTextView passwordView;
    private BaseKeyboard keyboardIdentity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        keyboardIdentity = new MyKeyboard(this.getActivity(), view);
        KeyboardUtils.bindEditTextEvent(keyboardIdentity, passwordView);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printContext = new ViewModelProvider(getActivity()).get(ControlViewModel.class).getPrintContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        passwordView.callOnClick();
        passwordView.setOnInputListener(new OnInputListener() {
            @Override
            public void onInputFinished(String content) {
                Disposable disposable = printContext.verifyPassword(content)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result->{
                            if(result){
                                NavHostFragment.findNavController(PasswordFragment.this)
                                        .navigate(R.id.action_PasswordFragment_to_MainFragment);
                            }else {
                                Toast.makeText(PasswordFragment.this.getContext(),  getString(R.string.password_incorrect_tip),Toast.LENGTH_LONG).show();
                            }
                        });
                compositeDisposable.add(disposable);
            }
        });
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