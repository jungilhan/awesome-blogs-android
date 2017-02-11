package org.petabytes.coordinator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.squareup.coordinators.*;
import com.squareup.coordinators.Coordinator;

import java.util.Map;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    private ActivityGraph activityGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = createActivityGraph();
        setContentView(activityGraph.getLayoutResId());
        ButterKnife.bind(this);

        Stream.of(activityGraph.getCoordinatorMap().entrySet())
            .forEach(new Consumer<Map.Entry<Integer, Coordinator>>() {
                @Override
                public void accept(final Map.Entry<Integer, Coordinator> entry) {
                    Coordinators.bind(findViewById(entry.getKey()), new CoordinatorProvider() {
                        @Nullable
                        @Override
                        public Coordinator provideCoordinator(View view) {
                            return entry.getValue();
                        }
                    });
                }
            });
    }

    protected abstract ActivityGraph createActivityGraph();

    protected ActivityGraph getActivityGraph() {
        return activityGraph;
    }
}
