package cn.westlan.coding.control.editor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.panel.EditorPanel;
import cn.westlan.coding.core.panel.element.Element;
import cn.westlan.coding.core.panel.select.SelectListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EditorOperateFragment extends Fragment {

    private final EditorPanel editorPanel;
    @BindView(R.id.ivChoice)
    ImageView choiceButton;
    @BindView(R.id.ivLock)
    ImageView lockButton;

    public EditorOperateFragment(EditorPanel editorPanel) {
        super(R.layout.edit_func_ope_fragment);
        this.editorPanel = editorPanel;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        editorPanel.addSelectListener(new SelectListener<Element>() {
            @Override
            public void onSelectedChange(List<Element> selected) {
                if(selected.size() == 0){
                    lockButton.setSelected(false);
                }else{
                    Element element = selected.get(0);
                    lockButton.setSelected(element.isLocked());
                }
            }

            @Override
            public void onModeChange(boolean multi) {
                choiceButton.setSelected(multi);
            }
        });
    }

    @OnClick(R.id.ivCopy)
    public void onCopy(){
        editorPanel.copySelected();
    }

    @OnClick(R.id.ivChoice)
    public void onMultiChoice(){
        editorPanel.setMultiSelect(!editorPanel.isMultiSelect());
    }

    @OnClick(R.id.ivLock)
    public void onLock(){
        if(editorPanel.isLockedSelected()){
            lockButton.setSelected(false);
            editorPanel.lockSelected(false);
        }else{
            lockButton.setSelected(true);
            editorPanel.lockSelected(true);
        }
    }

    @OnClick(R.id.ivDelete)
    public void onDelete(){
        editorPanel.deleteSelected();
    }

    @OnClick(R.id.ivRotate)
    public void onRotate(){
        editorPanel.rotateSelected();
    }

    @OnClick(R.id.ivHorizontalLeftAlign)
    public void onHorizontalLeftAlign(){
        editorPanel.alignSelectedLeft();
    }

    @OnClick(R.id.ivHorizontalCenterAlign)
    public void onHorizontalCenterAlign(){
        editorPanel.alignSelectedMidHorizontal();

    }

    @OnClick(R.id.ivHorizontalRightAlign)
    public void onHorizontalRightAlign(){
        editorPanel.alignSelectedRight();

    }

    @OnClick(R.id.ivVerticalTopAlign)
    public void onVerticalTopAlign(){
        editorPanel.alignSelectedTop();

    }

    @OnClick(R.id.ivVerticalCenterAlign)
    public void onVerticalCenterAlign(){
        editorPanel.alignSelectedMidVertical();
    }

    @OnClick(R.id.ivVerticalBottomAlign)
    public void onVerticalBottomAlign(){
        editorPanel.alignSelectedBottom();
    }

    @OnClick(R.id.tvVerticalTopDistribution)
    public void onVerticalTopDistribution(){
        editorPanel.alignSelectedVertical();
    }

    @OnClick(R.id.tvHorizontalLeftDistribution)
    public void onHorizontalLeftDistribution(){
        editorPanel.alignSelectedHorizontal();
    }

    @OnClick(R.id.tvVerticalCenter)
    public void onVerticalCenter(){
        editorPanel.alignSelectedToMid();
    }

    @OnClick(R.id.tvEquidistantDistribution)
    public void onEquidistantDistribution(){
        editorPanel.alignSelectedIsometric();
    }


}
