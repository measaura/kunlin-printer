package cn.westlan.coding.control.editor;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.westlan.coding.R;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.core.panel.EditorPanel;
import cn.westlan.coding.core.panel.element.*;
import org.jetbrains.annotations.NotNull;

public class EditorInsertFragment extends Fragment {
    private final EditorPanel editorPanel;
    private int ppi;

    public EditorInsertFragment(EditorPanel editorPanel) {
        super(R.layout.edit_func_insert_fragment);
        this.editorPanel = editorPanel;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ControlViewModel viewModel = new ViewModelProvider(getActivity()).get(ControlViewModel.class);
        this.ppi = viewModel.getIdentifier().getCanvasFeature().getPpi();
    }

    @OnClick(R.id.llText)
    public void insertText(){
        editorPanel.addElement(new TextElement());
    }

    @OnClick(R.id.llBarcode)
    public void insertBarcode(){
        editorPanel.addElement(new BarcodeElement());
    }

    @OnClick(R.id.llQrCode)
    public void insertQrCode(){
        editorPanel.addElement(new QrcodeElement());
    }

    @OnClick(R.id.llDate)
    public void insertDate(){
        editorPanel.addElement(new DateElement());
    }

    @OnClick(R.id.llLine)
    public void insertLine(){
        editorPanel.addElement(new LineElement());
    }

    @OnClick(R.id.llShape)
    public void insertShape(){
        editorPanel.addElement(new ShapeElement());
    }

    @OnClick(R.id.llSerial)
    public void insertSerialNumber(){
        editorPanel.addElement(new SerialNumberElement());
    }

    @OnClick(R.id.llTime)
    public void insertTime(){
        editorPanel.addElement(new TimeElement());
    }
}
