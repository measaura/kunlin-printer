package cn.westlan.coding;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testRxjava(){
        Observable<String> observable = Observable.using(
                ()->{System.out.println("using a!");return "a";},
                str->{System.out.println("using b!");return Observable.just(str);},
                str->{System.out.println("using c!");});
        observable.compose(takeUntil(
                Observable.just(1).map(integer -> {System.out.println("beforeEmission!");return integer;}),
                Observable.just(1).map(integer -> {System.out.println("afterEmission!");return integer;})));
    }

    private static <T> ObservableTransformer<T, T> takeUntil(Observable<?> beforeEmission, Observable<?> afterEmission) {
        return observable -> observable.publish(publishedObservable -> {
                    final Observable<?> afterEmissionTakeUntil = publishedObservable
                            .take(1)
                            .ignoreElements()
                            .andThen(afterEmission);
                    return Observable.amb(
                            asList(
                                    publishedObservable,
                                    publishedObservable.takeUntil(beforeEmission)
                            ))
                            .takeUntil(afterEmissionTakeUntil);
                }
        );
    }
}