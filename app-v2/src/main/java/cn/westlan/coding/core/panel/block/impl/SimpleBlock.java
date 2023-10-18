package cn.westlan.coding.core.panel.block.impl;


import cn.westlan.coding.core.panel.block.Slice;
import cn.westlan.coding.core.panel.block.slice.TextSlice;
import cn.westlan.coding.core.panel.block.Block;
import cn.westlan.coding.core.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlock implements Block {
    private String text;

    public SimpleBlock() {
        this("");
    }

    public SimpleBlock(String text) {
        this.text = text;
    }

    @Override
    public boolean backspace() {
        if(text.length()>1){
            text = text.substring(0, text.length()-1);
            return true;
        }
        return false;
    }

    @Override
    public String demo() {
        return text;
    }

    @Override
    public List<Slice> slices() {
        return new ArrayList<Slice>(){{add(new TextSlice(text));}};
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        this.text = IOUtil.readString(inputStream);
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        IOUtil.writeString(outputStream, this.text);
    }
}
