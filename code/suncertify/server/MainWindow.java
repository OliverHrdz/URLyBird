package suncertify.server;

import java.awt.BorderLayout;
import java.awt.Container;

import suncertify.application.BaseWindow;

/**
 * The main server application window.
 * 
 * @author Oliver Hernandez
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends BaseWindow {

    /**
     * Constructs the main server window.
     */
    public MainWindow() {
        setTitle("URLyBird" + this.executionMode);

        Container contentPane = getContentPane();
        contentPane.add(statusBar, BorderLayout.SOUTH);

        this.setResizable(false);
        this.setSize(300, 200);
        this.setLocationRelativeTo(null); // center onscreen
        this.disableWindow();
    }

    @Override
    protected void setComponents(boolean enabled) {
        // nothing to implement
    }

}
