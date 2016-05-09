package name.mitterdorfer.perlock.examples;

import name.mitterdorfer.perlock.PathWatcher;
import name.mitterdorfer.perlock.SinglePathWatcherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This example application demonstrates how to create and use a path watcher. The demo accepts two command line
 * parameters:
 *
 * <ol>
 *     <li><code>--recursive</code> to watch a path recursively or <code>--non-recursive</code> to watch
 * non-recursively (i.e. only in the provided path but not below)</li>
 *      <li><code>path</code> which is the path to watch</li>
 * </ol>
 *
 * If you invoke the sample application as follows, it watches the home folder (on Unix): <code>java -jar perlock-examples.jar --non-recursive ~</code>
 */
class SinglePathWatcherDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePathWatcherDemo.class);

    public static void main(String[] args) throws Exception {
        String pathName = "";
        if (args.length > 0) {
            pathName = args[0];
        }
        Path path = Paths.get(pathName).toAbsolutePath();

        PathWatcher watcher = SinglePathWatcherFactory.createSinglePathWatcher(path,
                eventKind -> LOGGER.info("Event: {}", eventKind)).start();


        //noinspection ResultOfMethodCallIgnored
        System.in.read();
        LOGGER.debug("Stopping path watcher");

        //We're not interested in watching anymore. Stop the watcher and the corresponding thread pool
        watcher.stop();
    }
}