package cn.westlan.coding.control.view;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
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
import razerdp.util.animation.TranslationConfig;

import java.util.List;
import java.util.function.Consumer;

public final class OperatePopup<T> extends BasePopupWindow {


    @BindView(R.id.rvOperates)
    RecyclerView recyclerView;

    private final OperateSelectAdapter<T> adapter = new OperateSelectAdapter<>();

    public OperatePopup(Context context) {
        super(context);
        setContentView(R.layout.operates_popup);
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
        Animation toShow = ((AnimationHelper.AnimationBuilder) AnimationHelper.asAnimation().withTranslation(TranslationConfig.FROM_BOTTOM)).toShow();
        toShow.setDuration(200);
        return toShow;
    }

    protected Animation onCreateDismissAnimation() {
        Animation toDismiss = ((AnimationHelper.AnimationBuilder) AnimationHelper.asAnimation().withTranslation(TranslationConfig.TO_BOTTOM)).toDismiss();
        toDismiss.setDuration(200);
        return toDismiss;
    }

    @OnClick(R.id.tvCancel)
    public void onBack(){
        dismiss();
    }

    public final void show(List<SelectItem<T>> items, Consumer<T> callback) {
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
                    callback.accept(items.get(position).getData());
                }
                dismiss();
            }
        });
        adapter.setList(items);
        showPopupWindow();
    }
}