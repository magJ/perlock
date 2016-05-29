package name.mitterdorfer.perlock;

import java.nio.file.Path;

/**
 * <p>Callback interface to get notified about changes on the file system. </p>
 *
 * <p>Implementation note: A <code>PathChangeListener</code> will be called from the internal <code>PathWatcher</code>
 * thread. This implies that callback methods should return reasonably fast and offload heavy lifting to a dedicated
 * thread. Otherwise, events may be lost.</p>
 */
public interface PathChangeListener {

    /**
     * This method has to be called when a new path is created.
     *
     * @param path The path that has been created. Must not be null.
     */
    void onPathChanged(EventKind eventKind, Path path);

}
