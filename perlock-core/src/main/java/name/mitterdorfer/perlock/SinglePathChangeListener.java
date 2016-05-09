package name.mitterdorfer.perlock;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Callback interface for notifying the user when the chosen file changes.
 * Suited for use as a functional interface
 *
 * <p>Implementation note: A <code>PathChangeListener</code> will be called from the internal <code>PathWatcher</code>
 * thread. This implies that callback methods should return reasonably fast and offload heavy lifting to a dedicated
 * thread. Otherwise, events may be lost.</p>
 */
public interface SinglePathChangeListener {

    void onPathChanged(WatchEvent.Kind<Path> eventKind);

}
