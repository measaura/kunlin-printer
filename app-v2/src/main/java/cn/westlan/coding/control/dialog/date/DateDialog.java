package cn.westlan.coding.control.dialog.date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import cn.westlan.coding.R;

public class DateDialog {
    private Dialog dialog = null;

    public static DateDialog show(final Context context, String title, Callback callback){
        DateDialog dateDialog = new DateDialog();
        dateDialog.showChoice(context, title, callback);
        return dateDialog;
    }

    public static DateDialog show(final Context context, int title, Callback callback){
        DateDialog dateDialog = new DateDialog();
        dateDialog.showChoice(context, title, callback);
        return dateDialog;
    }

    public void dismiss(){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    protected Boolean showChoice(final Context context, String title, Callback callback){
        String[] items =new String[]{context.getString(R.string.date_offset),context.getString(R.string.date_value)};
        this.dialog = new AlertDialog.Builder(context).setTitle(title)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            dialogInterface.dismiss();
                            dialog = new AdjustDialog(context,callback);
                            dialog.show();
                        }else{
                            dialogInterface.dismiss();
                            dialog = new FixedDialog(context,callback);
                            dialog.show();
                        }
                    }
                }).show();
        return true;
    }

    protected Boolean showChoice(final Context context, int title, Callback callback){
        String[] items =new String[]{context.getString(R.string.date_offset),context.getString(R.string.date_value)};
        this.dialog = new AlertDialog.Builder(context).setTitle(title)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            dialogInterface.dismiss();
                            dialog = new AdjustDialog(context,callback);
                            dialog.show();
                        }else{
                            dialogInterface.dismiss();
                            dialog = new FixedDialog(context,callback);
                            dialog.show();
                        }
                    }
                }).show();
        return true;
    }

}
