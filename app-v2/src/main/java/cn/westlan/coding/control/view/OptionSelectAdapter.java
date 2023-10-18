package cn.westlan.coding.control.view;

import androidx.databinding.DataBindingUtil;
import cn.westlan.coding.R;
import cn.westlan.coding.databinding.OptionSelectItemBinding;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import org.jetbrains.annotations.NotNull;

public final class OptionSelectAdapter<T> extends BaseQuickAdapter<SelectItem<T>, BaseDataBindingHolder<OptionSelectItemBinding>> {
    public OptionSelectAdapter() {
        super(R.layout.option_select_item);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<OptionSelectItemBinding> optionsSelectItemBindingBaseDataBindingHolder, SelectItem<T> tOptionsSelectItem) {
        OptionSelectItemBinding dataBinding =  optionsSelectItemBindingBaseDataBindingHolder.getDataBinding();
        if (dataBinding != null) {
            dataBinding.setItem(tOptionsSelectItem);
        }
    }
}