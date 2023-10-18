package cn.westlan.coding.control.view;

import android.widget.ImageView;
import android.widget.TextView;
import cn.westlan.coding.R;
import cn.westlan.coding.core.bean.PrintTemplate;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import org.jetbrains.annotations.NotNull;

public final class TemplateSelectAdapter extends BaseQuickAdapter<PrintTemplate, BaseViewHolder> {
    public TemplateSelectAdapter() {
        super(R.layout.template_select_item);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PrintTemplate printTemplate) {
        ImageView imageView = baseViewHolder.findView(R.id.template_iv_thumb);
        imageView.setImageBitmap(printTemplate.getDemo());
        TextView textView = baseViewHolder.findView(R.id.template_tv_name);
        textView.setText(printTemplate.getName());
    }
}