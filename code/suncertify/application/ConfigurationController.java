package suncertify.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The controller in the Model-View-Controller architecture for the
 * configuration of the application. Handles all GUI events from the Swing view
 * (a modal dialog) and invokes a {@link suncertify.application.Configuration
 * Configuration} object to handle saving changes to the configuration.
 * 
 * @author Oliver Hernandez
 * 
 */
public class ConfigurationController implements ActionListener {

    private Configuration configurationModel; // the model

    private ConfigurationDialog dialog; // the view (a modal dialog)

    /**
     * Create a controller for the specified model, and associate the modal
     * dialog view to the specified owner window.
     * 
     * @param model
     *            a {@link suncertify.application.Configuration Configuration}
     *            object.
     * @param frame
     *            the owner window of the modal dialog view.
     */
    public ConfigurationController(Configuration model, JFrame frame) {
        this.configurationModel = model;
        this.dialog = new ConfigurationDialog(frame, this.configurationModel);
        this.dialog.addSaveButtonListener(this);
    }

    /**
     * Display the view of the MVC implementation.
     */
    public void start() {
        this.dialog.setVisible(true);
    }

    /**
     * Handle the "Save" action of the view by invoking the model to persist
     * changes.
     * 
     * @param event
     *            the click event of the view's "Save" button.
     */
    public void actionPerformed(ActionEvent event) {
        this.configurationModel.setDBFilePath(this.dialog.getDBPath());
        this.configurationModel.setRMIHost(this.dialog.getRMIHostname());

        try {
            this.configurationModel.persist();

            JOptionPane.showMessageDialog(this.dialog,
                    "Restart the application for the changes to take effect.",
                    "Changes Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (ConfigurationException e) {
            BaseWindow.handleException(this.dialog,
                    "An error occurred saving the changes made to the "
                            + "application configuration.", e);
        }

        this.dialog.setVisible(false);
    }

}
