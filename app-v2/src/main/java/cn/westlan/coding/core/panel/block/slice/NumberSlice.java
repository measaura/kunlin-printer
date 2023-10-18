package cn.westlan.coding.core.panel.block.slice;

import cn.westlan.coding.core.panel.block.Accum;
import cn.westlan.coding.core.panel.block.AdjustDate;
import cn.westlan.coding.core.panel.block.Slice;
import cn.westlan.coding.core.panel.block.NumberType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Calendar;
import java.util.Locale;

@Data
public class NumberSlice implements Slice {
    private NumberType numberType;
    private AdjustDate adjustDate;
    private Accum accum;

    public NumberSlice(NumberType numberType, AdjustDate adjustDate) {
        this.numberType = numberType;
        this.adjustDate = adjustDate;
        this.accum = new Accum();
    }

    public NumberSlice(NumberType numberType, Accum accum) {
        this.numberType = numberType;
        this.adjustDate = new AdjustDate();
        this.accum = accum;
    }

    @Override
    public String demo() {
        Calendar calendar = adjustDate.getCalendar();
        switch (numberType){
            case HighYear:
                return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.YEAR)/100);
            case LowYear:
                return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.YEAR)%100);
            case Month:
                return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH)+1);
            case Day:
                return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.DAY_OF_MONTH));
            case Hour:
                return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.HOUR_OF_DAY));
            case Minute:
                return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MINUTE));
            case Serial:
                return String.valueOf(accum.getStart());
        }
        return "00";
    }

    @Override
    public int length() {
        return demo().length();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
