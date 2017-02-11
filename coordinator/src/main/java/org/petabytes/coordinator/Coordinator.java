package org.petabytes.coordinator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

public class Coordinator extends com.squareup.coordinators.Coordinator {

    private RxBinder rxBinder;

    public Coordinator() {
        this.rxBinder = new RxBinder();
    }

    @Override
    public void attach(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void detach(View view) {
        rxBinder.unsubscribe();
    }

    protected <T> void bind(@NonNull Observable<T> observable, @NonNull Action1<? super T> onNext) {
        rxBinder.subscribe(observable, onNext, null);
    }

    protected <T> void bind(@NonNull Observable<T> observable, @NonNull Action1<? super T> onNext, @Nullable Action1<Throwable> onError) {
        rxBinder.subscribe(observable, onNext, onError, null);
    }

    protected <T> void bind(@NonNull Observable<T> observable, @NonNull Action1<? super T> onNext,
                            @Nullable Action1<Throwable> onError, @Nullable Action0 onComplete) {
        rxBinder.subscribe(observable, onNext, onError, onComplete);
    }

    private <T> void bind(@NonNull Observable<T> observable, @NonNull Subscriber<T> subscriber) {
        rxBinder.subscribe(observable, subscriber);
    }
}
