package cn.westlan.coding.control.editor;

import androidx.lifecycle.ViewModel;
import cn.westlan.coding.core.panel.EditorPanel;

public class EditorViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private EditorPanel editorPanel;

    public EditorPanel getEditorPanel() {
        return editorPanel;
    }

    public void setEditorPanel(EditorPanel editorPanel) {
        this.editorPanel = editorPanel;
    }
}