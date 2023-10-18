package cn.westlan.coding.core.connect;

import android.util.Log;
import cn.westlan.coding.core.bean.PrintStatus;
import cn.westlan.coding.core.io.Readable;
import cn.westlan.coding.core.connect.protocol.*;
import cn.westlan.coding.core.io.Writable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.concurrent.TimeUnit;

public interface PrintContext extends Printer {

    BleDevice getBleDevice();

    <T extends Readable> Observable<ReplyMsg<T>> observeReply(Reply<T> reply);

    Completable sendMessage(RequestMsg<?> message);

    void close();

    @Override
    default Observable<PrintStatus> observeStatusChanges() {
        return this.observeReply(Protocol.ReplyPrintStatus).map(ReplyMsg::getContent);
    }

    @Override
    default <T extends Writable, R extends Readable> Single<R> doRequest(RequestMsg<T> requestMsg, Reply<R> reply) {
        Log.i(getClass().getSimpleName(), "doRequest:"+requestMsg+" ,"+reply);
        return this.observeReply(reply).map(ReplyMsg::getContent).firstOrError().timeout(10, TimeUnit.SECONDS).doOnSubscribe(disposable -> {
            sendMessage(requestMsg).subscribe();
        });
//        return .andThen(
    }
}
