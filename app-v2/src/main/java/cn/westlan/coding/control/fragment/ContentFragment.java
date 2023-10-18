package cn.westlan.coding.control.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.westlan.coding.R;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.control.dialog.date.DateDialog;
import cn.westlan.coding.control.dialog.record.RecordDialogWrap;
import cn.westlan.coding.control.editor.*;
import cn.westlan.coding.control.view.TemplatesPopup;
import cn.westlan.coding.core.bean.Identifier;
import cn.westlan.coding.core.bean.SlaveContent;
import cn.westlan.coding.core.connect.PrintContext;
import cn.westlan.coding.core.panel.EditorPanel;
import cn.westlan.coding.core.panel.element.Element;
import cn.westlan.coding.core.panel.select.SelectListener;
import cn.westlan.coding.util.EditDialog;
import cn.westlan.coding.util.SqlDataHelper;
import cn.westlan.coding.util.WaitingDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContentFragment extends BaseFragment implements LabelListener{
    @BindView(R.id.tvChoiceMode)
    TextView choicModeText;
    @BindView(R.id.editorPanel)
    EditorPanel editorPanel;
    @BindView(R.id.tabIndicator)
    TabLayout tabLayout;
    @BindView(R.id.vpFunc)
    ViewPager2 viewPager2;
    private PrintContext printContext;
    private Identifier identifier;
    private SqlDataHelper sqlDataHelper;
    private DateDialog dateDialog = null;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ControlViewModel viewModel = new ViewModelProvider(getActivity()).get(ControlViewModel.class);
        printContext = viewModel.getPrintContext();
        identifier = viewModel.getIdentifier();
        editorPanel.post(new Runnable() {
            @Override
            public void run() {
                editorPanel.setCanvasFeatrue(identifier.getCanvasFeature());
            }
        });
        editorPanel.addSelectListener(new SelectListener<Element>() {
            @Override
            public void onSelectedChange(List<Element> selected) {
            }

            @Override
            public void onModeChange(boolean multi) {
                choicModeText.setText(multi?R.string.edit_ope_choice_mode_multiple:R.string.edit_ope_choice_mode_single);
            }
        });
        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            private final Fragment[] tabFragments = {new EditorLabelFragment(ContentFragment.this), new EditorInsertFragment(editorPanel), new EditorAttributeFragment(editorPanel), new EditorOperateFragment(editorPanel)};
            @Override
            public int getItemCount() {
                return tabFragments.length;
            }

            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                return tabFragments[position];
            }
        });
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            final String[] tabTitles = {getString(R.string.label), getString(R.string.insert), getString(R.string.attribute), getString(R.string.operate)};
            //就是将标题内容分别相对应的赋值
            tab.setText(tabTitles[position]);
        }).attach();
        EditorViewModel editorViewModel = new ViewModelProvider(getActivity()).get(EditorViewModel.class);
        editorViewModel.setEditorPanel(editorPanel);
        final Dialog waitingDialog = WaitingDialog.show(getActivity(), getString(R.string.load_print_content));
        Disposable disposable = printContext.readPrintContent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(printContent -> {
                            this.editorPanel.setContent(printContent);
                            waitingDialog.dismiss();
                        }, throwable -> {
                            waitingDialog.dismiss();
                            Log.e(getClass().getSimpleName(), getString(R.string.load_print_content_failed), throwable);
                            Toast.makeText(this.getContext(),  getString(R.string.load_print_content_failed)+":"+throwable.getMessage(),Toast.LENGTH_LONG).show();}
                );
        compositeDisposable.add(disposable);
        sqlDataHelper = SqlDataHelper.getInstance(this.getActivity(), identifier);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(dateDialog != null){
            dateDialog.dismiss();
            dateDialog = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_content, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.send_to_printer) {
            editorPanel.send((bitmap, dynamicNumbers) -> {
                final Dialog waitingDialog = WaitingDialog.show(getActivity(), getString(R.string.send_to_printer));
                Disposable disposable = printContext.sendSlaveContent(new SlaveContent(bitmap, dynamicNumbers))
                        .flatMap(result -> printContext.savePrintContent(editorPanel.getContent()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            waitingDialog.dismiss();
                            Log.i(getClass().getSimpleName(), getString(R.string.send_successfully));
                            Toast.makeText(this.getContext(), getString(R.string.send_successfully), Toast.LENGTH_LONG).show();
                        }, throwable -> {
                            waitingDialog.dismiss();
                            Log.e(getClass().getSimpleName(), getString(R.string.send_failed), throwable);
                            Toast.makeText(this.getContext(), getString(R.string.send_failed), Toast.LENGTH_LONG).show();
                        });
                compositeDisposable.add(disposable);
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewLabel() {
        editorPanel.clear();
    }

    @Override
    public void onLabelSave() {
        EditDialog.show(getActivity(), getString(R.string.label_name), editorPanel.getLabelName(), value -> {
            editorPanel.setLabelName(value);
            sqlDataHelper.addContent(value, editorPanel.getContent());
        });
    }

    @Override
    public void onLabelHistory() {
        new TemplatesPopup(getActivity(), sqlDataHelper).show(template -> {
            editorPanel.setLabelName(template.getName());
            editorPanel.setContent(template.getContent());
        });
    }
}