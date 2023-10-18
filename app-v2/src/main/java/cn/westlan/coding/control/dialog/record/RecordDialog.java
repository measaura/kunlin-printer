package cn.westlan.coding.control.dialog.record;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.core.bean.Identifier;
import cn.westlan.coding.core.bean.PrintContent;
import cn.westlan.coding.core.panel.EditorPanel;
import com.google.android.material.chip.ChipGroup;

class RecordDialog extends AlertDialog {

    @BindView(R.id.contentThumb)
    EditorPanel editorPanel;
    @BindView(R.id.recordGroup)
    ChipGroup chipGroup;
    private String title;
    private Identifier identifier;
    private Boolean showThumb;
    private Callback callback;
    private final int[] chipIds =  {R.id.record1, R.id.record2, R.id.record3};
    private int selectedId;

    protected RecordDialog(Context context, String title, Identifier identifier, Boolean showThumb, Callback callback) {
        super(context, true, dialogInterface->{});
        this.title = title;
        this.identifier = identifier;
        this.showThumb = showThumb;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_record);
        ButterKnife.bind(this);
        this.editorPanel.setCanvasFeatrue(identifier.getCanvasFeature());
        chipGroup.setOnCheckedChangeListener(((group, checkedId) -> {
            for(int i = 0; i < chipIds.length; ++i){
                if(chipIds[i] == checkedId){
                    selectedId = i;
                    PrintContent printContent = callback.onSelected(selectedId, false);
                    if(showThumb){
                        editorPanel.setContent(printContent);
                    }
                }
            }
        }));
        chipGroup.check(chipIds[0]);
    }

    @OnClick(R.id.recordConfirm)
    public void onConfirmClick() {
        if(callback != null){
            callback.onSelected(selectedId, true);
        }
        dismiss();
    }

    @OnClick(R.id.recordCancel)
    public void onCancelClick() {
        dismiss();
    }
}