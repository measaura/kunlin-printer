package cn.westlan.coding.control.fragment;

import android.app.Activity;
import android.view.View;
import cn.westlan.coding.R;
import com.dilusense.customkeyboard.BaseKeyboard;
import com.dilusense.customkeyboard.MyKeyBoardView;

import java.lang.reflect.Field;

public class MyKeyboard extends BaseKeyboard {

    public MyKeyboard(Activity activity, View source) {
        super.init(activity, R.xml.keyboard_identity);
        try {
            MyKeyBoardView view = source.findViewById(R.id.keyboard_view);
            Field field = BaseKeyboard.class.getDeclaredField("mKeyboardView");
            field.setAccessible(true);
            field.set(this, view);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
