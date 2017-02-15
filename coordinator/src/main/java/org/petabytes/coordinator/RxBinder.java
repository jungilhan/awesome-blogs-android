package org.petabytes.coordinator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RxBinder {

    private final CompositeSubscription subscriptions;

    public RxBinder() {
        subscriptions = new CompositeSubscription();
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public <T> void subscribe(@NonNull Observable<T> observable, @NonNull Action1<? super T> onNext) {
        subscribe(observable, onNext, null, null);
    }

    public <T> void subscribe(@NonNull Observable<T> observable, @NonNull Action1<? super T> onNext, @Nullable Action1<Throwable> onError) {
        subscribe(observable, onNext, onError, null);
    }

    public <T> void subscribe(@NonNull final Observable<T> observable, @NonNull final Action1<? super T> onNext,
                              @Nullable final Action1<Throwable> onError, @Nullable final Action0 onComplete) {
        subscribe(observable, new Subscriber<T>() {
            @Override
            public void onNext(T value) {
                Timber.i("onNext: " + value.toString());
                onNext.call(value);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e.getMessage(), e);
                if (onError != null) {
                    onError.call(e);
                }
            }

            @Override
            public void onCompleted() {
                Timber.i("onCompleted");
                if (onComplete != null) {
                    onComplete.call();
                }
            }
        });
    }

    public <T> void subscribe(@NonNull Observable<T> observable, @NonNull Subscriber<T> subscriber) {
        subscriptions.add(observable.observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber));
    }

}