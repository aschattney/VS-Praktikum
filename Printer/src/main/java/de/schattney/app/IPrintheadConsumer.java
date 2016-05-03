package de.schattney.app;

import de.hochschuledarmstadt.component.IMessageConsumer;

public interface IPrintheadConsumer extends IMessageConsumer{
    void setPrintHeadStatus(String status);
}
