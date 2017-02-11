package org.petabytes.coordinator;

import android.view.View;

public interface PagerFactory<T> {

    View create(T item);
}
