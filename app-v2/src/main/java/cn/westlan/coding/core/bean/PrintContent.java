package cn.westlan.coding.core.bean;

import cn.westlan.coding.core.io.IOUtil;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.io.Writable;
import cn.westlan.coding.core.panel.element.Element;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class PrintContent implements Readable, Writable {
    private final static byte[] identifier = new byte[]{0x50, 0x54, 0x43, 0x54, 0x00, 0x01};//PTCT
    private final List<Element> elements;

    public PrintContent(List<Element> elements) {
        this.elements = elements;
    }

    public PrintContent() {
        this.elements = new ArrayList<>();
    }

    @Override
    public void readFrom(ByteArrayInputStream inputStream) throws IOException {
        if(!IOUtil.equals(IOUtil.readBytes(inputStream, identifier.length), identifier)){
            return;
        }
        elements.clear();
        int size = IOUtil.readShort(inputStream);
        for(int i = 0; i < size; ++i){
            elements.add(IOUtil.readElement(inputStream));
        }
    }

    @Override
    public void writeTo(ByteArrayOutputStream outputStream) throws IOException {
        IOUtil.writeBytes(outputStream, identifier);
        IOUtil.writeShort(outputStream, elements.size());
        for(Element element : elements){
            IOUtil.writeElement(outputStream, element);
        }

    }
}
