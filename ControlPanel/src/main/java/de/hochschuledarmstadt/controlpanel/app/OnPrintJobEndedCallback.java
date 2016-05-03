package de.hochschuledarmstadt.controlpanel.app;

import de.hochschuledarmstadt.model.PrintJob;

/**
 * Created by Andy on 30.04.2016.
 */
public interface OnPrintJobEndedCallback {
    void onPrintJobEnded(PrintJob printJob);
}
