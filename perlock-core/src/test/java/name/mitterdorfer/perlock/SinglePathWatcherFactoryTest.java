package name.mitterdorfer.perlock;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

import static java.nio.file.StandardWatchEventKinds.*;

public class SinglePathWatcherFactoryTest {

    private static final Long testTimeout = 6L;

    private Path filepath;

    @Before
    public void setUp() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        filepath = fs.getPath("/rootPath/filepath.txt");
        Files.createDirectory(filepath.getParent());
    }

    @Test
    public void testNonRecursiveWatcher() throws Exception {
        final BlockingQueue<WatchEvent.Kind<Path>> queue = new ArrayBlockingQueue<WatchEvent.Kind<Path>>(3);
        SinglePathChangeListener singlePathChangeListener =  new SinglePathChangeListener() {
            @Override
            public void onPathChanged(WatchEvent.Kind<Path> eventKind) {
                queue.add(eventKind);
            }
        };

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        LifecycleListener lifecycleListener = new LifecycleListener() {
            @Override
            public void onStart(PathWatcher pathWatcher) {
                countDownLatch.countDown();
            }

            @Override
            public void onException(PathWatcher pathWatcher, Exception ex) {

            }

            @Override
            public void onStop(PathWatcher pathWatcher) {

            }
        };

        PathWatcher watcher = SinglePathWatcherFactory.createSinglePathWatcher(filepath, singlePathChangeListener, lifecycleListener);

        watcher.start();
        countDownLatch.await(testTimeout, TimeUnit.SECONDS);

        Files.createFile(filepath);
        assertEquals(ENTRY_CREATE, queue.poll(testTimeout, TimeUnit.SECONDS));

        Files.write(filepath, new byte[1]);
        assertEquals(ENTRY_MODIFY, queue.poll(testTimeout, TimeUnit.SECONDS));

        Files.delete(filepath);
        assertEquals(ENTRY_DELETE, queue.poll(testTimeout, TimeUnit.SECONDS));

    }


}
