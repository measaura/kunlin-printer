package cn.westlan.coding.core.panel.block.impl;

import android.text.format.DateUtils;
import cn.westlan.coding.core.panel.block.AdjustDate;
import cn.westlan.coding.core.panel.block.Block;
import cn.westlan.coding.core.panel.block.NumberType;
import cn.westlan.coding.core.panel.block.Slice;
import cn.westlan.coding.core.panel.block.slice.NumberSlice;
import cn.westlan.coding.core.panel.block.slice.TextSlice;
import cn.westlan.coding.core.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdjustDateBlock implements Block {
    private String pattern;
    private AdjustDate adjustDate;
    private List<Slice> slices;

    public AdjustDateBlock() {
        this("yyyy/MM/dd", new AdjustDate());
    }

    public AdjustDateBlock(String pattern, AdjustDate adjustDate) {
        this.pattern = pattern;
        this.adjustDate = adjustDate;
        this.slices = slice(pattern);
    }

    @Override
    public boolean backspace() {
        return false;
    }

    @Override
    public List<Slice> slices() {
        return slices;
    }

    @Override
    public String demo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return simpleDateFormat.format(adjustDate.getCalendar().getTime());
    }

    private List<Slice> slice(String origin){
        List<Slice> ret = new ArrayList<>();
        char[] charArray = origin.toCharArray();
        int searchIndex = 0;
        int seachBegin = 0;
        int remainLength = origin.length();
        while(searchIndex < origin.length()){
            int findLength = 0;
            List<Slice> slices = new ArrayList<>();
            if(remainLength < 2){
                //do nothing
            }else if(charArray[searchIndex]=='y'&&charArray[searchIndex+1] =='y'){
                if(remainLength>=4 && charArray[searchIndex+2]=='y'&&charArray[searchIndex+3] =='y'){
                    findLength = 4;
                    slices.add(new NumberSlice(NumberType.HighYear, adjustDate));
                    slices.add(new NumberSlice(NumberType.LowYear, adjustDate));
                }else{
                    findLength = 2;
                    slices.add(new NumberSlice(NumberType.LowYear, adjustDate));
                }
            }else if(charArray[searchIndex]=='M'&&charArray[searchIndex+1] =='M'){
                findLength = 2;
                slices.add(new NumberSlice(NumberType.Month, adjustDate));
            }else if(charArray[searchIndex]=='d'&&charArray[searchIndex+1] =='d'){
                findLength = 2;
                slices.add(new NumberSlice(NumberType.Day, adjustDate));
            }else if(charArray[searchIndex]=='H'&&charArray[searchIndex+1] =='H'){
                findLength = 2;
                slices.add(new NumberSlice(NumberType.Hour, adjustDate));
            }else if(charArray[searchIndex]=='m'&&charArray[searchIndex+1] =='m'){
                findLength = 2;
                slices.add(new NumberSlice(NumberType.Minute, adjustDate));
            }
            if(findLength > 0){
                if(seachBegin != searchIndex){
                    ret.add(new TextSlice(pattern.substring(seachBegin, searchIndex)));
                }
                ret.addAll(slices);
                remainLength -= findLength;
                searchIndex += findLength;
                seachBegin = searchIndex;
            }else{
                searchIndex++;
                remainLength--;
            }
        }
        if(seachBegin != searchIndex){
            ret.add(new TextSlice(pattern.substring(seachBegin, searchIndex)));
        }
        return ret;
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        this.pattern = IOUtil.readString(inputStream);
        int yearOffset = (int) IOUtil.readInt(inputStream);
        int monthOffset = (int) IOUtil.readInt(inputStream);
        int dayOffset = (int) IOUtil.readInt(inputStream);
        this.adjustDate = new AdjustDate(yearOffset, monthOffset, dayOffset);
        this.slices = slice(pattern);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        IOUtil.writeString(outputStream, this.pattern);
        IOUtil.writeInt(outputStream, this.adjustDate.getYearOffset());
        IOUtil.writeInt(outputStream, this.adjustDate.getMonthOffset());
        IOUtil.writeInt(outputStream, this.adjustDate.getDayOffset());
    }
}
