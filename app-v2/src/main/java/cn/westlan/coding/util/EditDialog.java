package cn.westlan.coding.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import cn.westlan.coding.R;

public interface EditDialog {
    interface Callback{
        void callback(String value);
    }

    static AlertDialog show(Context context, String title,  Callback callback){
        return show(context, title, "", EditorInfo.TYPE_CLASS_TEXT, "", callback);
    }

    static AlertDialog show(Context context, int title,  Callback callback){
        return show(context, title, 0, EditorInfo.TYPE_CLASS_TEXT, "", callback);
    }

    static AlertDialog show(Context context, String title,  String content, Callback callback){
        return show(context, title, "", EditorInfo.TYPE_CLASS_TEXT, content, callback);
    }

    static AlertDialog show(Context context, int title,  String content, Callback callback){
        return show(context, title, 0, EditorInfo.TYPE_CLASS_TEXT, content, callback);
    }

    static AlertDialog show(Context context, String title, String hint, int inputType,  Callback callback){
        return show(context, title, hint, inputType, "", callback);
    }

    static AlertDialog show(Context context, String title, String hint, int inputType, String content, Callback callback){
        final EditText et = new EditText(context);
        et.setSingleLine(true);
        et.setHint(hint);
        et.setInputType(inputType);
        et.setText(content);
        return new AlertDialog.Builder(context).setTitle(title)
                .setView(et)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        callback.callback(et.getText().toString());
//                        Toast.makeText(context, et.getText().toString(),Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.cancel,null).show();
    }

    static AlertDialog show(Context context, int title, int hint, int inputType, String content, Callback callback){
        final EditText et = new EditText(context);
        et.setSingleLine(true);
        if(hint != 0){
            et.setHint(hint);
        }
        et.setInputType(inputType);
        et.setText(content);
        return new AlertDialog.Builder(context).setTitle(title)
                .setView(et)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        callback.callback(et.getText().toString());
//                        Toast.makeText(context, et.getText().toString(),Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton(R.string.cancel,null).show();
    }
}
