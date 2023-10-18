package cn.westlan.coding.control.dialog.record;

import android.app.Dialog;
import android.content.Context;
import cn.westlan.coding.core.bean.Identifier;

public class RecordDialogWrap {

    private static Dialog dialog = null;

    public static void show(Context context, String title, Identifier identifier, Boolean showThumb, Callback callback){
        dismiss();
        dialog = new RecordDialog(context, title, identifier, showThumb, callback);
        dialog.show();
    }

    public static void dismiss(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}
