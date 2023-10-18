package cn.westlan.coding.control.view;

import cn.westlan.coding.R;
import cn.westlan.coding.databinding.OperateSelectItemBinding;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import org.jetbrains.annotations.NotNull;

public final class OperateSelectAdapter<T> extends BaseQuickAdapter<SelectItem<T>, BaseDataBindingHolder<OperateSelectItemBinding>> {
    public OperateSelectAdapter() {
        super(R.layout.operate_select_item);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<OperateSelectItemBinding> optionsSelectItemBindingBaseDataBindingHolder, SelectItem<T> tOptionsSelectItem) {
        OperateSelectItemBinding dataBinding =  optionsSelectItemBindingBaseDataBindingHolder.getDataBinding();
        if (dataBinding != null) {
            dataBinding.setItem(tOptionsSelectItem);
        }
    }
}