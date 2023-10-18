package cn.westlan.coding.test;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

import static java.util.Arrays.asList;

public class TestRx {

    public static void main(String[] args) {
        Observable<String> observable = Observable.using(
                ()->{System.out.println("using a!");return "a";},
                str->{System.out.println("using b!");return Observable.just(str);},
                str->{System.out.println("using c!");});
        observable.compose(takeUntil(
                Observable.just(1).map(integer -> {System.out.println("beforeEmission!");return integer;}),
                Observable.just(1).map(integer -> {System.out.println("afterEmission!");return integer;})));
        System.out.println("finish!");
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
