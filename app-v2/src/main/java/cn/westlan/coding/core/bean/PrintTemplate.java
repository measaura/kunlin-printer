package cn.westlan.coding.core.bean;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import cn.westlan.coding.core.panel.CanvasFeature;
import cn.westlan.coding.core.panel.element.Element;
import lombok.Data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

@Data
public class PrintTemplate implements Serializable {
    private final Integer id;
    private final String name;
    private final Identifier identifier;
    private final PrintContent content;
    private final Bitmap demo;

    public PrintTemplate(Integer id, String name, Identifier identifier, PrintContent content) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
        this.content = content;
        this.demo = createDemo(identifier, content);
    }

    private static Bitmap createDemo(Identifier identifier, PrintContent content){
        CanvasFeature feature = identifier.getCanvasFeature();
        int width = feature.getWidth()* feature.getPpi();
        int height = feature.getHeight()* feature.getPpi();
        LinkedList<Element> elements = new LinkedList<>(content.getElements());
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Iterator<Element> iterator = elements.descendingIterator();
        while (iterator.hasNext()){
            Element element = iterator.next();
            element.beforeDraw(identifier.getCanvasFeature());
            element.onDraw(canvas);
        }
        return bitmap;
    }
}
