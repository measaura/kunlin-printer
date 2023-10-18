package cn.westlan.coding.login;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.westlan.coding.R;

public class LoginActivity extends AppCompatActivity {

//    @BindView(R.id.tbTitle)
//    Toolbar tbTitle;
//    @BindView(R.id.tvTitle)
//    TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}