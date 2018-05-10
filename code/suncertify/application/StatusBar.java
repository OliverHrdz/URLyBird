package suncertify.application;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * A GUI window Status Bar.
 * 
 * @author Oliver Hernandez
 * 
 */
@SuppressWarnings("serial")
public class StatusBar extends JLabel {

    /** Create a new Status Bar with the default status message. */
    public StatusBar() {
        this.setPreferredSize(new Dimension(100, 20));
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.setMessage("Set Me");
    }

    /**
     * Create a new Status Bar with the specified message.
     * 
     * @param message
     *            the text the status bar should display.
     */
    public StatusBar(String message) {
        this();
        this.setMessage(message);
    }

    /**
     * Set the message to display in this Status Bar.
     * 
     * @param message
     *            the text the status bar should display.
     */
    public void setMessage(String message) {
        this.setText(message);
    }

}