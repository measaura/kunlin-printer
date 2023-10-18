package cn.westlan.coding.core.panel;

import cn.westlan.coding.core.panel.block.AdjustDate;
import cn.westlan.coding.core.panel.block.NumberType;
import cn.westlan.coding.core.panel.block.Slice;
import cn.westlan.coding.core.panel.block.slice.NumberSlice;
import cn.westlan.coding.core.panel.block.slice.TextSlice;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class DateConfig {
    private final String pattern;
    private final AdjustDate adjustDate;
    private final List<Slice> slices;
    private final String text;

    public DateConfig(String pattern, AdjustDate adjustDate) {
        this.pattern = pattern;
        this.adjustDate = adjustDate;
        this.slices = slice(pattern, adjustDate);
        this.text = slices.stream().map(Slice::demo).collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateConfig)) return false;
        DateConfig that = (DateConfig) o;
        return Objects.equals(pattern, that.pattern) && Objects.equals(adjustDate, that.adjustDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, adjustDate);
    }

    private static List<Slice> slice(String pattern, AdjustDate adjustDate){
        List<Slice> ret = new ArrayList<>();
        char[] charArray = pattern.toCharArray();
        int searchIndex = 0;
        int seachBegin = 0;
        int remainLength = pattern.length();
        while(searchIndex < pattern.length()){
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
}
