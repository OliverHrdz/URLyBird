package suncertify.application;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import suncertify.util.StringUtil;
import suncertify.util.ThrowableUtil;

/**
 * Base class for the main windows of the room reservation system.
 * 
 * @author Oliver Hernandez
 * 
 */
@SuppressWarnings("serial")
public abstract class BaseWindow extends JFrame {

    /**
     * The default cursor to display, such as a standard mouse pointer.
     */
    public static final Cursor NORMAL_CURSOR =
        new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * The cursor to display when a long running operation is in progress, such
     * as an hourglass.
     */
    public static final Cursor DELAY_CURSOR = new Cursor(Cursor.WAIT_CURSOR);

    /*
     * menu components
     */
    protected JMenuBar menuBar = new JMenuBar();
    protected JMenu fileMenu = new JMenu("File");
    protected JMenu systemMenu = new JMenu("System");
    protected JMenuItem aboutMenuItem = new JMenuItem("About...");
    protected JMenuItem configureMenuItem = new JMenuItem("Configure...");
    protected JMenuItem exitMenuItem = new JMenuItem("Exit");

    protected StatusBar statusBar = new StatusBar("Initializing...");

    protected String executionMode;

    /**
     * Standardized method to handle exceptions and display them in the GUI.
     * 
     * @param frame
     *            should be the visible window at the time the exception
     *            occurred.
     * @param errorMessage
     *            the error message to display.
     * @param e
     *            the exception that occurred.
     */
    public static void handleException(Component frame, String errorMessage,
            Exception e) {
        Object[] options = { "Show Details", "OK" }; // button labels

        int choice = JOptionPane.showOptionDialog(frame, errorMessage, "Error",
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
                options, options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            String trace = ThrowableUtil.getStackTrace(e);

            JOptionPane.showMessageDialog(frame, e.getMessage()
                    + StringUtil.NEW_LINE + e.getCause() + StringUtil.NEW_LINE
                    + trace, e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Constructs the base window for all windows in the application, such as
     * the common components and menus.
     */
    public BaseWindow() {
        /* get execution mode to display in title bar */

        switch (RoomReservationSystem.getExecMode()) {
        case CLIENT:
            this.executionMode = " Client";
            break;
        case STANDALONE:
            this.executionMode = " (Standalone Mode)";
            break;
        case SERVER:
            this.executionMode = " Server";
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * build menus
         */

        fileMenu.setMnemonic(KeyEvent.VK_F);
        systemMenu.setMnemonic(KeyEvent.VK_S);

        configureMenuItem.setMnemonic(KeyEvent.VK_C);
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);

        fileMenu.add(configureMenuItem);
        fileMenu.add(exitMenuItem);

        systemMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(systemMenu);

        this.setJMenuBar(menuBar);

        /*
         * add menu action listeners
         */

        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(BaseWindow.this,
                        "URLyBird Hotel Room Reservation System, Version 1.0.",
                        "About URLyBird", JOptionPane.PLAIN_MESSAGE);
            }
        });

        configureMenuItem.addActionListener(new ActionListener() {
            private ConfigurationController configCtlr =
                new ConfigurationController(URLyBirdConfiguration.getInstance(),
                        BaseWindow.this);

            public void actionPerformed(ActionEvent e) {
                configCtlr.start();
            }
        });

    }

    /**
     * Display the GUI window and queues it to the Swing Event Dispatch Thread.
     * 
     */
    public void startup() {
        final JFrame window = this;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                window.setVisible(true);
            }
        });
    }

    /**
     * Enable the components of this window and set the cursor to a normal icon.
     */
    public void enableWindow() {
        this.setWindow(true, NORMAL_CURSOR);
    }

    /**
     * Disable the components of this window and set the cursor to a waiting
     * icon.
     */
    public void disableWindow() {
        this.setWindow(false, DELAY_CURSOR);
    }

    /**
     * Enable only the menus and set the window cursor to normal. If the rest of
     * the components are already disabled, they will remain so.
     */
    public void enableMenus() {
        fileMenu.setEnabled(true);
        systemMenu.setEnabled(true);
        this.setCursor(NORMAL_CURSOR);
    }

    /**
     * Set the text in the bottom status bar of this window.
     * 
     * @param message
     *            the new status message.
     */
    public void setStatusMessage(String message) {
        this.statusBar.setMessage(message);
    }

    /**
     * Sets the window and all of its components according to the enabled flag.
     * 
     * @param enabled
     *            if <code>true</code>, the components will be enabled,
     *            otherwise they will be disabled.
     */
    protected abstract void setComponents(boolean enabled);

    private void setWindow(boolean enabled, Cursor cursor) {
        fileMenu.setEnabled(enabled);
        systemMenu.setEnabled(enabled);

        setComponents(enabled);

        this.setCursor(cursor);
    }

}
