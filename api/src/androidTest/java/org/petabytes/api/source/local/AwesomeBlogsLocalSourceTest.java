package org.petabytes.api.source.local;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.realm.Realm;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class AwesomeBlogsLocalSourceTest {

    private AwesomeBlogsLocalSource localSource;

    @Before
    public void setUp() throws Exception {
        localSource = new AwesomeBlogsLocalSource(InstrumentationRegistry.getContext());
    }

    @Test
    public void getFeed() throws Exception {
        {
            Realm.getInstance(localSource.config)
                .executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });
            List<Feed> values = localSource.getFeed("dev")
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .getOnNextEvents();

            assertThat(values)
                .hasSize(1)
                .containsNull();
        }

        {
            Feed feed = new Feed();
            feed.setCategory("dev");
            Realm.getInstance(localSource.config)
                .executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Feed feed = new Feed();
                        feed.setCategory("dev");
                        realm.insert(feed);
                    }
                });

            List<Feed> values = localSource.getFeed("dev")
                .test()
                .awaitTerminalEvent()
                .assertCompleted()
                .getOnNextEvents();

            assertThat(values)
                .hasSize(1)
                .extracting("category")
                .contains("dev");
        }
    }
}