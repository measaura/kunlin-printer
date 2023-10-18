package cn.westlan.coding.core.panel.block;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class AdjustDate {
    private final int yearOffset;
    private final int monthOffset;
    private final int dayOffset;

    public AdjustDate() {
        this(0, 0,0);
    }

    public Date getDate(){
        return getCalendar().getTime();
    }

    public Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR,yearOffset);
        calendar.add(Calendar.MONTH,monthOffset);
        calendar.add(Calendar.DAY_OF_MONTH,dayOffset);
        return calendar;
    }

    public static AdjustDate calc(Date setDate){
        Calendar calendar = Calendar.getInstance();
        long timeDiff = setDate.getTime() - calendar.getTimeInMillis();
        int dayDiff = (int)((timeDiff+(1000*60*60*24-1))/1000/60/60/24);
        int yOff=0;
        int mOff=0;
        int year = calendar.get(Calendar.YEAR);
        while(true){
            int daysOfYear = daysOfYear(year+yOff);
            if(dayDiff>=daysOfYear){
                dayDiff -= daysOfYear;
                yOff++;
            }else{
                break;
            }
        }
        int month = calendar.get(Calendar.MONTH)+1;
        while(true){
            int daysOfMonth = daysOfMonth(year+yOff, month+mOff);
            if(dayDiff>=daysOfMonth){
                dayDiff -= daysOfMonth;
                mOff++;
            }else{
                break;
            }
        }
        if(mOff >= 12){
            yOff++;
            mOff -= 12;
        }
        return new AdjustDate(yOff, mOff, dayDiff);
    }

    private static int daysOfYear(int year){
        int days = 365;
        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
            days = 366;
        }
        return days;
    }

    private static int daysOfMonth(int year, int month){
        if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
            return 31;
        }
        if(month == 2){
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)){
                return 29;
            }
            return 28;
        }
        return 30;
    }
}
