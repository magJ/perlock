package name.mitterdorfer.perlock;

import name.mitterdorfer.perlock.impl.util.Preconditions;

import java.nio.file.Path;

import java.nio.file.WatchEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Factory to create {@link PathWatcher}s that only deal with a single file and
 * ignore change to other files within the directory
 */
public class SinglePathWatcherFactory {

    /**
     * Creates a {@link PathWatcher} that watched for changes to the designated file, backed by a single thread executor.
     * @param path The path to a precise file or directory to watch for changes.
     * @param singlePathChangeListener The callback handler for when something happens to the file
     * @return A new <code>PathWatcher</code> instance
     */
    public static PathWatcher createSinglePathWatcher(Path path, SinglePathChangeListener singlePathChangeListener){
        return createSinglePathWatcher(path, singlePathChangeListener, Executors.newSingleThreadExecutor());
    }

    /**
     * Creates a {@link PathWatcher} that watched for changes to the designated file.
     * @param path The path to a precise file or directory to watch for changes.
     * @param singlePathChangeListener The callback handler for when something happens to the file
     * @param executorService The executor service to run the PatchWatcher thread on.
     * @return A new <code>PathWatcher</code> instance
     */
    public static PathWatcher createSinglePathWatcher(
            Path path,
            SinglePathChangeListener singlePathChangeListener,
            ExecutorService executorService){
        return createSinglePathWatcher(path, singlePathChangeListener, executorService, new PathWatcherFactory.NoOpLifecycleListener());
    }

    public static PathWatcher createSinglePathWatcher(
            Path path,
            SinglePathChangeListener singlePathChangeListener,
            LifecycleListener lifecycleListener){
        return createSinglePathWatcher(path, singlePathChangeListener, Executors.newSingleThreadExecutor(), lifecycleListener);
    }

    /**
     * Creates a {@link PathWatcher} that watched for changes to the designated file.
     * @param path The path to a precise file or directory to watch for changes.
     * @param singlePathChangeListener The callback handler for when something happens to the file
     * @param executorService The executor service to run the PatchWatcher thread on.
     * @param lifecycleListener A <code>LifeCycleListener</code> implementation that is called every time a lifecycle
     *                          event happens for a path watcher. Must not be null.
     * @return A new <code>PathWatcher</code> instance
     */
    public static PathWatcher createSinglePathWatcher(
            Path path,
            SinglePathChangeListener singlePathChangeListener,
            ExecutorService executorService,
            LifecycleListener lifecycleListener){
        Preconditions.isNotNull(path, "path");
        Preconditions.isNotNull(singlePathChangeListener, "singlePathChangeListener");
        Preconditions.isNotNull(executorService, "executorService");
        Preconditions.isNotNull(lifecycleListener, "lifecycleListener");
        PathWatcherFactory pathWatcherFactory = new PathWatcherFactory(executorService, lifecycleListener);
        return pathWatcherFactory.createNonRecursiveWatcher(path.getParent(), new SinglePathChangeListenerAdapter(path, singlePathChangeListener));
    }

    /**
     * Converts the three {@link PathChangeListener} methods back to a single call with event type.
     */
    private static class SinglePathChangeListenerAdapter implements PathChangeListener {

        private final Path path;
        private final SinglePathChangeListener singlePathChangeListener;

        SinglePathChangeListenerAdapter(Path path, SinglePathChangeListener singlePathChangeListener){
            this.path = path;
            this.singlePathChangeListener = singlePathChangeListener;
        }

        private void onPathChanged(Path path, WatchEvent.Kind<Path> eventKind){
            if(this.path.equals(path)){
                singlePathChangeListener.onPathChanged(eventKind);
            }
        }

        @Override
        public void onPathCreated(Path path) {
            onPathChanged(path, ENTRY_CREATE);
        }

        @Override
        public void onPathModified(Path path) {
            onPathChanged(path, ENTRY_MODIFY);
        }

        @Override
        public void onPathDeleted(Path path) {
            onPathChanged(path, ENTRY_DELETE);
        }
    }

}
