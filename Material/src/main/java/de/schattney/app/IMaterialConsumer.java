package de.schattney.app;

import de.hochschuledarmstadt.component.IMessageConsumer;

public interface IMaterialConsumer extends IMessageConsumer {
    void refillMaterial();
}
