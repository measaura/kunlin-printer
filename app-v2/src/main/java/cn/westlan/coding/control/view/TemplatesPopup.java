package cn.westlan.coding.control.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.bean.PrintTemplate;
import cn.westlan.coding.util.ConfirmDialog;
import cn.westlan.coding.util.SqlDataHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import org.jetbrains.annotations.NotNull;
import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.AnimationHelper.AnimationBuilder;
import razerdp.util.animation.TranslationConfig;

import java.util.List;
import java.util.function.Consumer;

public final class TemplatesPopup extends BasePopupWindow {

    @BindView(R.id.rvTemplates)
    RecyclerView recyclerView;
    SqlDataHelper sqlDataHelper;


    private final TemplateSelectAdapter adapter = new TemplateSelectAdapter();

    public TemplatesPopup(Context context, SqlDataHelper sqlDataHelper) {
        super(context);
        setContentView(R.layout.templates_popup);
        this.sqlDataHelper = sqlDataHelper;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View contentView) {
        super.onViewCreated(contentView);
        ButterKnife.bind(this, contentView);
//        setBackgroundColor(0x0);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getContext(),3));
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

    public void show(Consumer<PrintTemplate> callback) {
        initView(callback);
        showPopupWindow();
    }

    private void initView(Consumer<PrintTemplate> callback){
        List<PrintTemplate> templates = sqlDataHelper.getTemplates();
        adapter.setNewInstance(templates);
        adapter.addChildClickViewIds(R.id.ivDelete, R.id.llTemplate);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                PrintTemplate template = templates.get(position);
                switch (view.getId()){
                    case R.id.ivDelete:
                        ConfirmDialog.show(TemplatesPopup.this.getContext(),R.string.tips, R.string.delete_label_confirm, ()->{
                            sqlDataHelper.delete(template.getId());
                            initView(callback);
                        });
                        break;
                    case R.id.llTemplate:
                        callback.accept(template);
                        dismiss();
                        break;

                }
            }
        });
    }
}