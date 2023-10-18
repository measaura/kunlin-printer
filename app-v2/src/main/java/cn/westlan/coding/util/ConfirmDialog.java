package cn.westlan.coding.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import cn.westlan.coding.R;

public interface ConfirmDialog {
    interface Callback{
        void callback();
    }

    static AlertDialog show(Context context, String title, String message, Callback callback){
        return new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        callback.callback();
                    }
                }).setNegativeButton(R.string.cancel,null).show();
    }

    static AlertDialog show(Context context, int title, int message, Callback callback){
        return new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        callback.callback();
                    }
                }).setNegativeButton(R.string.cancel,null).show();
    }
}
