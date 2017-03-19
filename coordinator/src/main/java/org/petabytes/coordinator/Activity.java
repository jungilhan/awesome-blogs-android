package org.petabytes.coordinator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.squareup.coordinators.Coordinator;
import com.squareup.coordinators.CoordinatorProvider;
import com.squareup.coordinators.Coordinators;

import java.util.Map;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    private ActivityGraph activityGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph = createActivityGraph();
        createActivityLayoutBinder().bind(this)
            .addView(LayoutInflater.from(this).inflate(activityGraph.getLayoutResId(), null, false));
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

    protected abstract ActivityLayoutBinder createActivityLayoutBinder();
}
