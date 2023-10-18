package cn.westlan.coding.login.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import cn.westlan.coding.control.ControlViewModel;
import cn.westlan.coding.login.LoginActivity;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreate "+getClass().getName());
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        setDisplayHomeAsUpEnabled(true);
    }

    protected void setDisplayHomeAsUpEnabled(boolean showHomeAsUp){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
    }

    protected void setTitle(int titleId){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(titleId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if(!NavHostFragment.findNavController(this).popBackStack()){
                getActivity().finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
