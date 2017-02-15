package org.petabytes.api;

import android.support.annotation.NonNull;

import org.petabytes.api.source.local.Feed;

import rx.Observable;

public interface DataSource {

    Observable<Feed> getFeed(@NonNull String category);
}
