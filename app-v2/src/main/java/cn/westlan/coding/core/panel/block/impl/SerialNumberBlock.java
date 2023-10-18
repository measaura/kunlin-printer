package cn.westlan.coding.core.panel.block.impl;

import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.panel.block.*;
import cn.westlan.coding.core.panel.block.slice.NumberSlice;
import cn.westlan.coding.core.panel.block.slice.TextSlice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SerialNumberBlock implements Block {
    private Accum accum;
    private List<Slice> slices;

    public SerialNumberBlock() {
        this(new Accum());
    }

    public SerialNumberBlock(Accum accum) {
        this.accum = accum;
        this.slices = Collections.singletonList(new NumberSlice(NumberType.Serial, accum));
    }

    @Override
    public boolean backspace() {
        return false;
    }

    @Override
    public String demo() {
        int fillLen = accum.getEnd().toString().length()- accum.getStart().toString().length();
        return "0000000000000000".substring(0, Integer.max(0, fillLen))+ accum.getStart().toString();
    }

    public Integer getStart() {
        return accum.getStart();
    }

    public Integer getEnd() {
        return accum.getEnd();
    }

    public Integer getStep() {
        return accum.getStep();
    }

    @Override
    public List<Slice> slices() {
        return slices;
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        int start = (int) IOUtil.readInt(inputStream);
        int end = (int) IOUtil.readInt(inputStream);
        int step = (int) IOUtil.readInt(inputStream);
        this.accum = new Accum(start, end, step);
        this.slices = Collections.singletonList(new NumberSlice(NumberType.Serial, accum));
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        IOUtil.writeInt(outputStream, this.accum.getStart());
        IOUtil.writeInt(outputStream, this.accum.getEnd());
        IOUtil.writeInt(outputStream, this.accum.getStep());
    }
}
