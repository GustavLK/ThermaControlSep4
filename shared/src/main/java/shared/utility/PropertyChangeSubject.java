package shared.utility;

import java.beans.PropertyChangeListener;

public interface PropertyChangeSubject {
    public void addListener(PropertyChangeListener listener);
    public void removeListener(PropertyChangeListener listener);
}
