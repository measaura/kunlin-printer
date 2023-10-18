package cn.westlan.coding.control.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import org.jetbrains.annotations.NotNull;


public final class LongClickImageView extends AppCompatImageView {
    private MyHandler handler = new MyHandler();
    private long intervalTime = 100;
    private LongClickRunnable longClickRunnable = new LongClickRunnable();
    private OnClickListener listener;

    public LongClickImageView(@NonNull @NotNull Context context) {
        super(context);
        setOnLongClickListener(view -> {
            new Thread(longClickRunnable).start();
            return true;
        });
    }

    public LongClickImageView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnLongClickListener(view -> {
            new Thread(longClickRunnable).start();
            return true;
        });
    }

    public LongClickImageView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnLongClickListener(view -> {
            new Thread(longClickRunnable).start();
            return true;
        });
    }

    private final class LongClickRunnable implements Runnable {
        private int num;

        public void run() {

            while (LongClickImageView.this.isPressed()) {
                if (this.num++ % 5 == 0 && listener != null) {
                    LongClickImageView.this.handler.post(()->{
                        listener.onClick(LongClickImageView.this);
                    });
                }
                SystemClock.sleep(LongClickImageView.this.intervalTime / ((long) 5));
            }
        }
    }

    private static final class MyHandler extends Handler {
        public MyHandler() {
            super(Looper.getMainLooper());
        }
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
        setOnClickListener(listener);
    }
}
