package cn.westlan.coding.core.panel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;
import androidx.annotation.Nullable;
import cn.westlan.coding.R;
import cn.westlan.coding.core.bean.PrintContent;
import cn.westlan.coding.core.panel.attribute.*;
import cn.westlan.coding.core.panel.block.DynamicNumber;
import cn.westlan.coding.core.panel.element.Element;
import cn.westlan.coding.core.panel.select.SelectListener;
import cn.westlan.coding.core.panel.select.SelectedSet;

import java.util.*;

//todo
//存储（发送）
//其他元素
//属性
public class EditorPanel extends View implements Attributes, Panel, SelectListener<Element> {
    private final LinkedList<Element> elements = new LinkedList<>();
    private final SelectedSet<Element> selectedSet = new SelectedSet<>(this);
    private TouchAction touchAction = TouchAction.Click;
    private Point lastPoint = new Point();
    private Point downPoint = new Point();
    private int maxWidth = 0;
    private int maxHeight = 0;
    private int cavasWidth = 0;
    private int cavasHeight = 0;
    private CanvasFeature canvasFeature;
    private String labelName = getContext().getString(R.string.label_name);
    private Matrix viewMatrix;
    private Matrix invertMatrix;

    public EditorPanel(Context context) {
        this(context, null);
    }

    public EditorPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public EditorPanel(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditorPanel(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setCanvasFeatrue(CanvasFeature canvasFeature){
        this.canvasFeature = canvasFeature;
        this.setCanvasSize(canvasFeature.getWidth()*canvasFeature.getPpi(), canvasFeature.getHeight()*canvasFeature.getPpi());
    }

    private void setCanvasSize(int cavasWidth, int cavasHeight){
        if(this.maxWidth ==0){
//            int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//            int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//            this.measure(width,height);
            this.maxWidth = this.getWidth();
            this.maxHeight = this.getHeight();
        }
        this.cavasWidth = cavasWidth;
        this.cavasHeight = cavasHeight;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(cavasWidth*maxHeight > maxWidth*cavasHeight){
            layoutParams.height = maxWidth*cavasHeight/cavasWidth;
        }else{
            layoutParams.width = maxHeight*cavasWidth/cavasHeight;
        }
        float scaleFactor = maxWidth*1f/cavasWidth;
        this.viewMatrix = new Matrix();
        this.viewMatrix.setScale(scaleFactor,scaleFactor);
        this.invertMatrix = new Matrix(this.viewMatrix);
        this.invertMatrix.invert(this.invertMatrix);
        setLayoutParams(layoutParams);
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(invertMatrix != null){
            event.transform(invertMatrix);
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            this.downPoint = new Point((int) event.getX(), (int) event.getY());
            this.lastPoint = downPoint;
            this.touchAction = TouchAction.Click;
            //可以通过直接判断 点选 且选中的元素简化流程
            for(Element element : elements){
                Area downArea = element.pointIn(lastPoint);
                if(!Area.Outside.equals(downArea)){
                    if(downArea.equals(Area.DragX)){
                        this.touchAction = TouchAction.DragX;
                    }else if(downArea.equals(Area.DragY)){
                        this.touchAction = TouchAction.DragY;
                    }
                    break;
                }
            }
        } else if (actionMasked == MotionEvent.ACTION_UP) {
            if(TouchAction.Click.equals(this.touchAction)){
                Point point = new Point((int) event.getX(), (int) event.getY());
                for(Element element : elements){
                    Area downArea = element.pointIn(point);
                    if(!Area.Outside.equals(downArea)){
                        this.selectedSet.add(element);
                        break;
                    }
                }
            }
            this.touchAction = TouchAction.Click;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            Point point = new Point((int) event.getX(), (int) event.getY());
            if(!point.equals(lastPoint)){
                if(TouchAction.DragX.equals(this.touchAction)){
                    selectedSet.getFirst().dragX(point.x - lastPoint.x);
                }else if(TouchAction.DragY.equals(this.touchAction)){
                    selectedSet.getFirst().dragY(point.y - lastPoint.y);
                }else{
                    this.touchAction = TouchAction.Move;
                    for(Element element : selectedSet){
                        element.move(point.x - lastPoint.x, point.y-lastPoint.y);
                    }
                }
                this.lastPoint = point;
                invalidate();
            }
//        }else if (actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
//        } else if (actionMasked == MotionEvent.ACTION_POINTER_UP) {
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(viewMatrix != null){
            canvas.concat(viewMatrix);
        }
        Iterator<Element> iterator = elements.descendingIterator();
        while (iterator.hasNext()){
            Element element = iterator.next();
            element.beforeDraw(canvasFeature);
            element.onDraw(canvas);
            element.onDrawForeground(canvas);
        }
    }

    // 单\复选、拖拽（draw+event）
    //选中的控件需要包裹虚线
    //单选中可拖拽的控件，拖拽按钮有独立事件


    //draw  用foreground 搞定
    //event 是否命中 由控件反馈
    //原始view和Event 有尺寸转换

    public void addElement(Element element){
        elements.addFirst(element);
        selectedSet.add(element);
    }

//    public void addElement(Class<? extends Element> cls){
//        Element element =
//    }
    public void clear(){
        elements.clear();
        selectedSet.clear();
    }

    public PrintContent getContent(){
        return new PrintContent(elements);
    }

    public void setContent(PrintContent content){
        elements.clear();
        selectedSet.clear();
        elements.addAll(content.getElements());
    }

    public void setMultiSelect(boolean multi) {
        selectedSet.setMulti(multi);
    }

    public boolean isMultiSelect() {
        return selectedSet.isMulti();
    }

    @Override
    public List<Attribute<?>> attributes(){
        List<Attribute<?>> attributes = new ArrayList<>();
        attributes.add(new DelegateAttribute<>(R.string.label_name, new Property<String>() {
            @Override
            public String get() {
                return labelName;
            }

            @Override
            public boolean set(String str) {
                labelName = str;
                return true;
            }
        }));
        attributes.add(new ReadonlyAttribute<>(R.string.label_width, canvasFeature.getWidth()+"mm"));
        attributes.add(new ReadonlyAttribute<>(R.string.label_height, canvasFeature.getHeight()+"mm"));
        return attributes;
    }

    @Override
    public void onSelectedChange(List<Element> selected) {
        invalidate();
    }

    public void addSelectListener(SelectListener<Element> listener){
        this.selectedSet.addListener(listener);
    }

    public void lockSelected(boolean lock){
        if(selectedSet.size() <= 0)return;
        for(Element element : selectedSet){
            element.setLocked(lock);
        }
        invalidate();
    }

    public boolean isLockedSelected(){
        if(selectedSet.size() <= 0)return false;
        return selectedSet.getFirst().isLocked();
    }

    public void copySelected(){
        if(selectedSet.isMulti()){
            Toast.makeText(this.getContext(),  R.string.edit_tip_copy, Toast.LENGTH_LONG).show();
            return;
        }
        if(selectedSet.size() <= 0)return;
        Element element = selectedSet.getFirst();
        if(element != null){
            Element ne = element.clone();
            ne.move(0, 1);
            elements.addFirst(ne);
            invalidate();
        }
    }

    public void rotateSelected(){
        if(selectedSet.isMulti()){
            Toast.makeText(this.getContext(),  R.string.edit_tip_copy,Toast.LENGTH_LONG).show();
            return;
        }
        if(selectedSet.size() <= 0)return;
        if(selectedSet.getFirst().rotate180()){
            invalidate();
        }
    }

    public void deleteSelected(){
        if(selectedSet.size() <= 0)return;
        for(Element element : selectedSet){
            elements.remove(element);
        }
        selectedSet.clear();
        invalidate();
    }


    public boolean alignSelectedLeft() {
        if(selectedSet.size() <= 1)return false;
        int startX = Integer.MAX_VALUE;
        for (Element element : selectedSet) {
            if (startX > element.x()) {
                startX = element.x();
            }
        }
        for (Element element : selectedSet) {
            element.moveTo(startX, element.y());
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedMidHorizontal() {
        if(selectedSet.size() <= 1)return false;
        int alignCenterX = selectedSet.getFirst().centerX();
        for (Element element : selectedSet) {
            element.moveTo(alignCenterX-element.width()/2, element.y());
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedRight() {
        if(selectedSet.size() <= 1)return false;
        int endX = Integer.MIN_VALUE;
        for (Element element : selectedSet) {
            if (endX < element.getBound().right) {
                endX = element.getBound().right;
            }
        }
        for (Element element : selectedSet) {
            element.moveTo(endX-element.width(), element.y());
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedTop() {
        if(selectedSet.size() <= 1)return false;
        int startY = Integer.MAX_VALUE;
        for (Element element : selectedSet) {
            if (startY > element.y()) {
                startY = element.y();
            }
        }
        for (Element element : selectedSet) {
            element.moveTo(element.x(), startY);
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedMidVertical() {
        if(selectedSet.size() <= 1)return false;
        int alignCenterY = selectedSet.getFirst().centerY();  
        for (Element element : selectedSet) {
            element.moveTo(element.x(), alignCenterY-element.height()/2);
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedBottom() {
        if(selectedSet.size() <= 1)return false;
        int endY = Integer.MIN_VALUE;
        for (Element element : selectedSet) {
            if (endY < element.getBound().bottom) {
                endY = element.getBound().bottom;
            }
        }
        for (Element element : selectedSet) {
            element.moveTo(element.x(), endY-element.height());
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedHorizontal() {
        if(selectedSet.size() <= 2)return false;
        LinkedList<Element> elements = new LinkedList<>();
        for(Element element : selectedSet){
            elements.add(element);
        }
        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element element, Element t1) {
                return ((int) (element.centerX() - t1.centerX()));
            }
        });
        int centerX = elements.getFirst().getBound().centerX();
        int abs = Math.abs(centerX - elements.getLast().getBound().centerX()) / (elements.size() - 1);
        for (int i = 1; i < elements.size() - 1; i++) {
            Element element =  elements.get(i);
            element.moveTo(((abs * i) + centerX) - (element.width() / 2), element.y());
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedVertical() {
        if(selectedSet.size() <= 2)return false;
        LinkedList<Element> elements = new LinkedList<>();
        for(Element element : selectedSet){
            elements.add(element);
        }
        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element baseGraphical, Element t1) {
                return ((int) (baseGraphical.getBound().centerY() - t1.getBound().centerY()));
            }
        });
        int centerY = elements.getFirst().getBound().centerY();
        int abs = Math.abs(centerY - elements.getLast().getBound().centerY()) / (elements.size() - 1);
        for (int i = 1; i < elements.size() - 1; i++) {
            Element element =  elements.get(i);
            element.moveTo(element.x(), ((abs * i) + centerY) - (element.height() / 2));
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedIsometric() {
        if(selectedSet.size() <= 2)return false;
        LinkedList<Element> elements = new LinkedList<>();
        for(Element element : selectedSet){
            elements.add(element);
        }
        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element element, Element t1) {
                return ((int) (element.y() - t1.y()));
            }
        });
        int startY = Integer.MAX_VALUE;
        int endY = Integer.MIN_VALUE;
        int heightSum = 0;
        for(Element element : elements){
            heightSum += element.height();
            if (element.y() < startY) {
                startY = element.y();
            }
            if (element.y() > endY) {
                endY = element.y();
            }
        }
        int interval = Integer.max(1, (endY-startY-heightSum)/(selectedSet.size()-1));
        for (int i = 1; i < elements.size(); i++) {
            Element element =  elements.get(i);
            element.moveTo(element.x(), (elements.get(i - 1)).getBound().bottom + interval);
        }
        invalidate();
        return true;
    }

    public boolean alignSelectedToMid() {
        if(selectedSet.size() <= 0)return false;
        for (Element element : selectedSet) {
            element.moveTo(cavasWidth/2-element.width()/2, element.y());
        }
        invalidate();
        return true;
    }

    @Override
    public void send(Slave slave) {
        Bitmap bitmap = Bitmap.createBitmap(cavasWidth, cavasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        List<DynamicNumber> dynamicNumbers = new ArrayList<>();
        Iterator<Element> iterator = elements.descendingIterator();
        while (iterator.hasNext()){
            Element element = iterator.next();
            element.beforeDraw(canvasFeature);
            element.onDraw(canvas, dynamicNumbers);
        }
        slave.onShow(bitmap, dynamicNumbers);
    }

    //    private Element newElement(Class<? extends Element> cls){
//        if(cls.equals(TextElement.class)){
//            return
//        }
//    }
}
