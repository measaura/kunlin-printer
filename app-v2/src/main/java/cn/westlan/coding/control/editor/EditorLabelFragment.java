package cn.westlan.coding.control.editor;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import org.jetbrains.annotations.NotNull;

public class EditorLabelFragment extends Fragment {

    private final LabelListener listener;


    public EditorLabelFragment(LabelListener listener) {
        super(R.layout.edit_func_label_fragment);
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }


    @OnClick(R.id.llNew)
    public void onNew(){
        listener.onNewLabel();
    }

    @OnClick(R.id.llTemplateSave)
    public void onSave(){
        listener.onLabelSave();
    }

    @OnClick(R.id.llTemplate)
    public void onHistory(){
        listener.onLabelHistory();
    }

}
