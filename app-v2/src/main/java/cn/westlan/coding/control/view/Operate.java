package cn.westlan.coding.control.view;

import android.content.Context;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.R;

import java.util.LinkedList;
import java.util.List;

public enum Operate {
    Open,
    Delete;

    public static List<SelectItem<Operate>> getSelectItems(Context context){
        return new LinkedList<SelectItem<Operate>>(){{
            add(new SelectItem<>(context.getString(R.string.open), Open, false));
            add(new SelectItem<>(context.getString(R.string.delete), Delete, false));
        }};
    }
}
