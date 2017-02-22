package org.petabytes.api;

import org.junit.Before;
import org.junit.Test;
import org.petabytes.api.source.local.AwesomeBlogsLocalSource;
import org.petabytes.api.source.local.Feed;
import org.petabytes.api.source.remote.AwesomeBlogsRemoteSource;

import rx.Observable;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ApiTest {

    private Api api;
    private AwesomeBlogsLocalSource localSource;
    private AwesomeBlogsRemoteSource remoteSource;

    @Before
    public void setUp() throws Exception {
        localSource = mock(AwesomeBlogsLocalSource.class);
        remoteSource = mock(AwesomeBlogsRemoteSource.class);
        api = new Api(localSource, remoteSource);
    }

    @Test
    public void getFeed_empty() throws Exception {
        doReturn(Observable.<Feed>empty()).when(localSource).getFeed(anyString());
        doReturn(Observable.just(new Feed())).when(remoteSource).getFeed(anyString());
        api.getFeed("dev")
            .test()
            .awaitTerminalEvent()
            .assertValueCount(1);

        verify(localSource, times(1)).getFeed(anyString());
        verify(remoteSource, times(1)).getFeed(anyString());
    }
    @Test
    public void getFeed_one_more() throws Exception {
        doReturn(Observable.just(new Feed())).when(localSource).getFeed(anyString());
        doReturn(Observable.just(new Feed())).when(remoteSource).getFeed(anyString());
        api.getFeed("dev")
            .test()
            .awaitTerminalEvent()
            .assertValueCount(1);

        verify(localSource, times(1)).getFeed(anyString());
        verify(remoteSource, times(1)).getFeed(anyString());
    }


}