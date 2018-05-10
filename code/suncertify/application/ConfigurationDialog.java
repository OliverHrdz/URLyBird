package suncertify.application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Modal dialog window to display the application's configuration values.
 * 
 * @author Oliver Hernandez
 * 
 */
@SuppressWarnings("serial")
public class ConfigurationDialog extends JDialog {

    /*
     * the "command" labels that will be used by the action listeners of this
     * window to determine the source of the event.
     */
    public static final String DB_PATH_BUTTON_CMD = "Browse...";
    public static final String SAVE_BUTTON_CMD = "Save";
    public static final String CANCEL_BUTTON_CMD = "Cancel";

    private JLabel dbPathLabel = new JLabel("Database File:");
    private JLabel rmiHostLabel = new JLabel("Server Hostname:");

    private JTextField dbPathField = new JTextField(25);
    private JTextField rmiHostField = new JTextField(25);

    private JButton dbPathButton = new JButton(DB_PATH_BUTTON_CMD);
    private JButton saveButton = new JButton(SAVE_BUTTON_CMD);
    private JButton cancelButton = new JButton(CANCEL_BUTTON_CMD);

    /**
     * Constructs a modal dialog window to display and edit the application
     * configuration.
     * 
     * @param frame
     *            the owner window of the modal dialog view.
     * @param model
     *            a {@link suncertify.application.Configuration Configuration}
     *            object.
     */
    public ConfigurationDialog(Frame frame, Configuration model) {
        super(frame, "Configure URLyBird Reservation System", true);

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        /*
         * Create panel that will contain the configuration fields
         */

        JPanel configurationPanel = new JPanel(new GridBagLayout());

        /*
         * Build entry components for database file path
         */

        dbPathLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        dbPathLabel.setToolTipText("Enter the database file location");

        dbPathField.setText(model.getDBFilePath());
        dbPathField.setToolTipText("Enter the database file location");

        dbPathButton.setToolTipText("Select the database file");

        /*
         * Build entry components for RMI server host name
         */

        rmiHostLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        rmiHostLabel.setToolTipText("Enter the RMI server host name");

        rmiHostField.setText(model.getRMIHost());
        rmiHostField.setToolTipText("Enter the RMI server host name");

        /*
         * Add entry components for configuration values to configuration panel
         */

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 10, 0, 0);
        c.anchor = GridBagConstraints.LINE_END;
        configurationPanel.add(dbPathLabel, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 0, 5);
        c.anchor = GridBagConstraints.LINE_START;
        configurationPanel.add(dbPathField, c);

        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 0, 10);
        c.anchor = GridBagConstraints.LINE_START;
        configurationPanel.add(dbPathButton, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 10, 0, 0);
        c.anchor = GridBagConstraints.LINE_END;
        configurationPanel.add(rmiHostLabel, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 0, 5);
        c.anchor = GridBagConstraints.LINE_START;
        configurationPanel.add(rmiHostField, c);

        /*
         * Build panel to contain buttons
         */

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        /*
         * Add all components to dialog's content pane
         */

        Container contentPane = getContentPane();
        contentPane.add(configurationPanel, BorderLayout.NORTH);
        contentPane.add(Box.createRigidArea(new Dimension(10, 15)),
                BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(frame);

        /*
         * add browse button action listener
         */

        dbPathButton.addActionListener(new ActionListener() {
            JFileChooser fc = new JFileChooser();

            public void actionPerformed(ActionEvent e) {
                int option = fc.showOpenDialog(ConfigurationDialog.this);

                if (option == JFileChooser.APPROVE_OPTION) {
                    ConfigurationDialog.this.dbPathField.setText(fc
                            .getSelectedFile().getAbsolutePath());
                }
            }
        });

        /*
         * add cancel button action listener
         */

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConfigurationDialog.this.setVisible(false);
            }
        });
    }

    /**
     * Add a controller to handle the click event of the save button.
     * 
     * @param listener
     *            the controller.
     */
    public void addSaveButtonListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    /**
     * Get the database file path set in the dialog.
     * 
     * @return path to the database file.
     */
    public String getDBPath() {
        return dbPathField.getText();
    }

    /**
     * Get the RMI server hostname set in the dialog.
     * 
     * @return the RMI hostname.
     */
    public String getRMIHostname() {
        return rmiHostField.getText();
    }

}
