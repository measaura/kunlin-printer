package cn.westlan.coding.control.editor;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.westlan.coding.R;
import cn.westlan.coding.control.dialog.date.DateDialog;
import cn.westlan.coding.control.view.LongClickImageView;
import cn.westlan.coding.control.view.OptionsPopup;
import cn.westlan.coding.control.view.SelectItem;
import cn.westlan.coding.core.panel.EditorPanel;
import cn.westlan.coding.core.panel.attribute.Attribute;
import cn.westlan.coding.core.panel.attribute.OptionsAttribute;
import cn.westlan.coding.core.panel.attribute.RangeAttribute;
import cn.westlan.coding.core.panel.attribute.StepAttribute;
import cn.westlan.coding.core.panel.block.AdjustDate;
import cn.westlan.coding.core.panel.block.impl.AdjustDateBlock;
import cn.westlan.coding.core.panel.element.Element;
import cn.westlan.coding.core.panel.select.SelectListener;
import cn.westlan.coding.util.EditDialog;
import com.xw.repo.BubbleSeekBar;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EditorAttributeFragment extends Fragment {

    private final EditorPanel editorPanel;
    @BindView(R.id.llGroup)
    public LinearLayout viewGroup;

    public EditorAttributeFragment(EditorPanel editorPanel) {
        super(R.layout.edit_func_attr_fragment);
        this.editorPanel = editorPanel;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        this.editorPanel.addSelectListener(new SelectListener<Element>() {
            @Override
            public void onSelectedChange(List<Element> selected) {
                if(selected.size() == 0){
                    setAttributes(editorPanel.attributes());
                }else{
                    setAttributes(selected.get(0).attributes());
                }
            }
        });
    }

    private void setAttributes(List<Attribute<?>> attributes){
        viewGroup.removeAllViews();
        for(Attribute<?> attribute : attributes){
            if(attribute instanceof StepAttribute){
                addStepAttribute((StepAttribute)attribute);
            }else if(attribute instanceof OptionsAttribute){
                addOptionsAttribute((OptionsAttribute<?>)attribute);
            }else if(attribute instanceof RangeAttribute){
                addRangeAttribute((RangeAttribute)attribute);
            }else {
                if(attribute.readonly()){
                    addReadonlyAttribute(attribute);
                }else if(attribute.get() instanceof String){
                    addStringAttribute((Attribute<String>)attribute);
                }else if(attribute.get() instanceof Integer){
                    addIntegerAttribute((Attribute<Integer>)attribute);
                }else if(attribute.get() instanceof Boolean){
                    addBooleanAttribute((Attribute<Boolean>)attribute);
                }else if(attribute.get() instanceof AdjustDateBlock){
                    addDateConfigAttribute((Attribute<AdjustDateBlock>)attribute);
                }
            }
        }
    }

    private <T> void addRangeAttribute(RangeAttribute attribute) {
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_range, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvRangeLabel);
        tvLabel.setText(attribute.name());
        BubbleSeekBar sbRangeValue = view.findViewById(R.id.sbRangeValue);
        sbRangeValue.setProgress(attribute.getProgress());
        sbRangeValue.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                try {
                    if(attribute.setProgress(progressFloat)){
                        editorPanel.invalidate();
                    }
                }catch (RuntimeException e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private <T> void addOptionsAttribute(OptionsAttribute<T> attribute) {
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_string, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvStringLabel);
        tvLabel.setText(attribute.name());
        TextView tvValue = view.findViewById(R.id.tvStringValue);
        tvValue.setText(attribute.getSelected());
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<SelectItem<T>> items = attribute.getOptions().entrySet().stream()
                        .map(e->new SelectItem<T>(e.getKey(), e.getValue(), e.getKey().equals(attribute.getSelected())))
                        .collect(Collectors.toList());
                new OptionsPopup<T>(EditorAttributeFragment.this.getActivity()).show(attribute.name(), items, position->{
                    String selected = items.get(position).getShowName();
                    if(attribute.setSelected(selected)){
                        tvValue.setText(selected);
                        editorPanel.invalidate();
                    }
                });
            }
        });
    }

    private void addReadonlyAttribute(Attribute<?> attribute){
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_string, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvStringLabel);
        tvLabel.setText(attribute.name());
        TextView tvValue = view.findViewById(R.id.tvStringValue);
        tvValue.setText(attribute.get().toString());
        ImageView imageView = view.findViewById(R.id.ivEdit);
        imageView.setVisibility(View.INVISIBLE);
    }

    private void addStringAttribute(Attribute<String> attribute){
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_string, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvStringLabel);
        tvLabel.setText(attribute.name());
        TextView tvValue = view.findViewById(R.id.tvStringValue);
        tvValue.setText(attribute.get());
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialog.show(getActivity(), attribute.name(), attribute.get(), value -> {
                    tvValue.setText(value);
                    if(attribute.set(value)){
                        editorPanel.invalidate();
                    }
                });
            }
        });
    }

    private void addIntegerAttribute(Attribute<Integer> attribute){
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_string, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvStringLabel);
        tvLabel.setText(attribute.name());
        TextView tvValue = view.findViewById(R.id.tvStringValue);
        tvValue.setText(String.valueOf(attribute.get()));
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialog.show(getActivity(), attribute.name(), attribute.hint(), EditorInfo.TYPE_CLASS_NUMBER, String.valueOf(attribute.get()), value -> {
                    Integer intVal = 0;
                    try {
                        intVal = Integer.parseInt(value);
                    }catch (Exception e){
                        Toast.makeText(getContext(), R.string.illegal_input, Toast.LENGTH_LONG).show();
                    }
                    if(!attribute.isLegal(intVal)){
                        Toast.makeText(getContext(), attribute.errorMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(attribute.set(intVal)){
                        tvValue.setText(String.format(Locale.CHINA, "%d", intVal));
                        editorPanel.invalidate();
                    }
                });
            }
        });
    }

    private void addBooleanAttribute(Attribute<Boolean> attribute){
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_boolean, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvBooleanLabel);
        tvLabel.setText(attribute.name());
        Switch sbValue = view.findViewById(R.id.sbBooleanValue);
        sbValue.setChecked(attribute.get());
        sbValue.setOnCheckedChangeListener(((compoundButton, b) -> {
            if(attribute.set(b)){
                editorPanel.invalidate();
            }
        }));
    }

    private void addDateConfigAttribute(Attribute<AdjustDateBlock> attribute) {
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_string, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvStringLabel);
        tvLabel.setText(attribute.name());
        TextView tvValue = view.findViewById(R.id.tvStringValue);
        tvValue.setText(attribute.get().demo());
        tvValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog.show(getActivity(), attribute.name(), (pattern, date) -> {
                    String pattern1 = pattern.replace("YY", "yy").replace("DD", "dd");
                    if(date instanceof Date){
                        AdjustDateBlock dateConfig = new AdjustDateBlock(pattern1, AdjustDate.calc((Date) date));
                        tvValue.setText(dateConfig.demo());
                        if(attribute.set(dateConfig)){
                            editorPanel.invalidate();
                        }
                    }else if(date instanceof AdjustDate){
                        AdjustDateBlock dateConfig = new AdjustDateBlock(pattern1, (AdjustDate) date);
                        tvValue.setText(dateConfig.demo());
                        if(attribute.set(dateConfig)){
                            editorPanel.invalidate();
                        }
                    }
                });

            }
        });
    }


    private void addStepAttribute(StepAttribute attribute){
        View view = getLayoutInflater().inflate(R.layout.edit_func_attr_step, viewGroup, false);
        viewGroup.addView(view);
        TextView tvLabel = view.findViewById(R.id.tvStepLabel);
        tvLabel.setText(attribute.name());
        TextView tvValue = view.findViewById(R.id.tvStepValue);
        tvValue.setText(String.valueOf(attribute.get()));
        LongClickImageView ivStepSub = view.findViewById(R.id.ivStepSub);
        LongClickImageView ivStepAdd = view.findViewById(R.id.ivStepAdd);
        if (attribute.status() == 1){
            ivStepAdd.setVisibility(View.INVISIBLE);
        }else if(attribute.status() == -1){
            ivStepSub.setVisibility(View.INVISIBLE);
        }
        ivStepSub.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean update = attribute.sub();
                tvValue.setText(String.valueOf(attribute.get()));
                if(update){
                    editorPanel.invalidate();
                }
                if (attribute.status() != 1){
                    ivStepAdd.setVisibility(View.VISIBLE);
                    if(attribute.status() == -1){
                        ivStepSub.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        ivStepAdd.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean update = attribute.add();
                tvValue.setText(String.valueOf(attribute.get()));
                if(update){
                    editorPanel.invalidate();
                }
                if (attribute.status() != -1){
                    ivStepSub.setVisibility(View.VISIBLE);
                    if(attribute.status() == 1){
                        ivStepAdd.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
