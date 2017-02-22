package org.petabytes.api.source.local;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.realm.Realm;

import static org.assertj.core.api.Assertions.assertThat;

@org.junit.runner.RunWith(android.support.test.runner.AndroidJUnit4.class)
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