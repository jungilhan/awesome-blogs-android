package org.petabytes.coordinator;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import com.squareup.coordinators.Coordinator;

import java.util.HashMap;
import java.util.Map;

public class ActivityGraph {

    private final int layoutResId;
    private final Map<Integer, Coordinator> coordinatorMap;

    private ActivityGraph(int layoutResId, @NonNull Map<Integer, Coordinator> coordinatorMap) {
        this.layoutResId = layoutResId;
        this.coordinatorMap = coordinatorMap;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public Map<Integer, Coordinator> getCoordinatorMap() {
        return coordinatorMap;
    }

    @SuppressWarnings("unchecked")
    <T> T get(@IdRes Integer id) {
        return (T) coordinatorMap.get(id);
    }

    public static class Builder {

        private int layoutResId;
        private Map<Integer, Coordinator> coordinatorMap = new HashMap<>();

        public ActivityGraph.Builder layoutResId(@LayoutRes int layoutResId) {
            this.layoutResId = layoutResId;
            return this;
        }

        public ActivityGraph.Builder coordinator(@IdRes int id, @NonNull Coordinator coordinator) {
            coordinatorMap.put(id, coordinator);
            return this;
        }

        public ActivityGraph build() {
            return new ActivityGraph(layoutResId, coordinatorMap);
        }
    }
}
