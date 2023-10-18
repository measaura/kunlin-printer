package cn.westlan.coding.control.view;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import org.jetbrains.annotations.NotNull;
import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.AnimationHelper.AnimationBuilder;
import razerdp.util.animation.TranslationConfig;

import java.util.List;
import java.util.function.Consumer;

public final class OptionsPopup<T> extends BasePopupWindow {

    @BindView(R.id.rvOptions)
    RecyclerView recyclerView;
    @BindView(R.id.tvPopupTitle)
    TextView tvPopupTitle;

    private final OptionSelectAdapter<T> adapter = new OptionSelectAdapter<>();

    public OptionsPopup(Context context) {
        super(context);
        setContentView(R.layout.options_popup);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View contentView) {
        super.onViewCreated(contentView);
        ButterKnife.bind(this, contentView);
        setBackgroundColor(0x0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(adapter);
    }

    protected Animation onCreateShowAnimation() {
        Animation toShow = ((AnimationBuilder) AnimationHelper.asAnimation().withTranslation(TranslationConfig.FROM_RIGHT)).toShow();
        toShow.setDuration(200);
        return toShow;
    }

    protected Animation onCreateDismissAnimation() {
        Animation toDismiss = ((AnimationBuilder) AnimationHelper.asAnimation().withTranslation(TranslationConfig.TO_RIGHT)).toDismiss();
        toDismiss.setDuration(200);
        return toDismiss;
    }

    @OnClick(R.id.ivPopupBack)
    public void onBack(){
        dismiss();
    }

//    public void show(String title, List<OptionsSelectItem<T>> list) {
//        tvPopupTitle.setText(title);
//        super.showPopupWindow();
//        adapter.setNewInstance(list);
//    }


    public void show(int title, List<SelectItem<T>> items, Consumer<Integer> callback) {
        tvPopupTitle.setText(title);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                for(int i =0 ; i < items.size(); ++i){
                    boolean selected = i==position;
                    SelectItem<T> item = items.get(i);
                    if(selected != item.isSelected()){
                        items.get(i).setSelected(selected);
                        adapter.notifyItemChanged(i);
                    }
                }
                if(callback != null){
                    callback.accept(position);
                }
            }
        });
        adapter.setList(items);
        showPopupWindow();
    }
    public void show(String title, List<SelectItem<T>> items, Consumer<Integer> callback) {
        tvPopupTitle.setText(title);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                for(int i =0 ; i < items.size(); ++i){
                    boolean selected = i==position;
                    SelectItem<T> item = items.get(i);
                    if(selected != item.isSelected()){
                        items.get(i).setSelected(selected);
                        adapter.notifyItemChanged(i);
                    }
                }
                if(callback != null){
                    callback.accept(position);
                }
            }
        });
        adapter.setList(items);
        showPopupWindow();
    }
}