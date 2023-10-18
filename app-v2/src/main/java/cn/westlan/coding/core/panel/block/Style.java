package cn.westlan.coding.core.panel.block;

import android.graphics.Paint;
import android.util.Log;
import cn.westlan.coding.PrinterApplication;
import cn.westlan.coding.core.bean.Identifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public enum Style{
    _2_N_((byte) 0x01, 2,false),
    _2_5_N_((byte) 0x02, 2.5f,false),
    _3_N_((byte) 0x03, 3, false),
    _3_5_N_((byte) 0x04, 3.5f, false),
    _4_N_((byte) 0x05, 4, false),
    _2_B_((byte) 0x06, 2,true),
    _2_5_B_((byte) 0x07, 2.5f,true),
    _3_B_((byte) 0x08, 3,true),
    _3_5_B_((byte) 0x09, 3.5f,true),
    _4_B_((byte) 0x0a, 4,true);
    private final byte identifier;
    private final float fontSize;
    private final boolean bold;
    private final String fontType;
    static {
        fontTypes();
    }

    Style(byte identity, float fontSize, boolean bold) {
        this.identifier = identity;
        this.fontSize = fontSize;
        this.bold = bold;
        this.fontType = getFontType(fontSize);
    }

    public byte getIdentifier() {
        return identifier;
    }

    public float getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public String getFontType(){
        return fontType;
    }

    public static List<String> fontTypes(){
        List<String> fontSizes = new ArrayList<>();
        for(Identifier identifier :Identifier.values()){
            for(Style style : values()){
                if(!style.isBold()){
                    fontSizes.add(style.fontType);
                }
                Paint paint  = new Paint();
                paint.setTextSize(style.fontSize*identifier.getCanvasFeature().getPpi());
                paint.setTypeface(style.isBold()? PrinterApplication.boldFont:PrinterApplication.normalFont);
                paint.setFakeBoldText(style.isBold());
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                Log.i("eeeee", identifier.getName()+(style.isBold()?" bold":"normal")+" size:"+style.getFontSize()+"h:"+(fontMetrics.descent-fontMetrics.ascent));
                Log.i("eeeee", identifier.getName()+(style.isBold()?" bold":"normal")+" size:"+style.getFontSize()+"single  w:"+paint.measureText("9"));
//                Log.i("eeeee", identifier.getName()+" size:"+style.getFontSize()+"space w:"+paint.measureText(" "));
//                Log.i("eeeee", identifier.getName()+" size:"+style.getFontSize()+"ten w:"+paint.measureText("0123456789"));
            }
        }

        return fontSizes;
    }

    public static List<Float> fontSizes(){
        List<Float> fontSizes = new ArrayList<>();
        for(Style style : values()){
            if(!style.isBold()){
                fontSizes.add(style.fontSize);
            }
        }
        return fontSizes;
    }

    public static Style style(String fontType, boolean bold){
        for(Style style : values()){
            if(style.bold == bold && fontType.equals(style.fontType)){
                return style;
            }
        }
        return Style._2_N_;
    }

    public static Style style(float fontSize, boolean bold){
        for(Style style : values()){
            if(style.bold == bold && style.getFontSize() == fontSize){
                return style;
            }
        }
        return Style._2_N_;
    }

    public static Style style(byte identity){
        for(Style style : values()){
            if(style.identifier == identity){
                return style;
            }
        }
        return Style._2_N_;
    }

    private static String getFontType(float fontSize){
        DecimalFormat df = new DecimalFormat("#.#mm");
        return df.format(fontSize);
    }
}