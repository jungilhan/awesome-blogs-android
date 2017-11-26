package org.petabytes.coordinator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.annimon.stream.Stream;
import com.squareup.coordinators.Coordinators;

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
            .forEach(entry -> Coordinators.bind(findViewById(entry.getKey()), view -> entry.getValue()));
    }

    protected abstract ActivityGraph createActivityGraph();

    protected abstract ActivityLayoutBinder createActivityLayoutBinder();
}
