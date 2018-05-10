package suncertify.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import suncertify.application.BaseWindow;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * The controller in the Model-View-Controller architecture of the room
 * reservation system. Handles all GUI events from the Swing view and invokes a
 * {@link RoomReservationsModel} to handle the business logic.
 * 
 * @author Oliver Hernandez
 * 
 */
public class MainController implements ActionListener {

    private MainWindow mainWindow;
    private RoomReservationsModel model;
    private int[] columnWidths = { 125, 125, 100, 65, 70, 85, 80 };

    /**
     * Constructs a controller for the main client window of the application
     * with the specified model.
     * 
     * @param newModel
     *            a {@link RoomReservationsModel} object.
     */
    public MainController(RoomReservationsModel newModel) {
        this.model = newModel;
    }

    /**
     * Runs the controller, initializing the model and displaying the view.
     */
    public void start() {
        try {
            this.mainWindow = new MainWindow();
            this.mainWindow.startup();

            /* initialize model and connect to server */

            this.model.initialize();

            this.mainWindow.setTableModel(this.model);

            /* set preferred display widths of the table's columns */

            for (int col = 0; col < this.columnWidths.length; col++) {
                this.mainWindow.setColumnWidth(col, this.columnWidths[col]);
            }

            /* set cell renderer for max occupancy column to center values */

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            this.mainWindow.setColumnRenderer(2, renderer);

            /* set cell renderer for rate column to right justify values */

            renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.RIGHT);
            this.mainWindow.setColumnRenderer(4, renderer);

            /* set cell renderer for date column to center values */

            renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            this.mainWindow.setColumnRenderer(5, renderer);

            /* set this controller as the GUI's action listeners */

            this.mainWindow.addSearchButtonListener(this);
            this.mainWindow.addSearchAllButtonListener(this);
            this.mainWindow.addResetButtonListener(this);
            this.mainWindow.addBookButtonListener(this);

            this.mainWindow.enableWindow();
            this.mainWindow.setStatusMessage("Ready");
        } catch (Exception e) {
            BaseWindow
                    .handleException(
                            this.mainWindow,
                            "An error occurred connecting to the room "
                            + "reservation system.",
                            e);

            JOptionPane.showMessageDialog(this.mainWindow,
                    "Verify the application is configured correctly.", "Error",
                    JOptionPane.INFORMATION_MESSAGE);

            this.mainWindow.enableMenus();
            this.mainWindow.setStatusMessage("Not connected");
        }
    }

    /**
     * Handle events from the GUI that should invoke methods in the model.
     * 
     * @param event
     *            the event to handle.
     */
    public void actionPerformed(ActionEvent event) {
        try {
            String actionCmd = event.getActionCommand();

            if (actionCmd.equals(MainWindow.SEARCH_BUTTON_CMD)) {
                this.mainWindow.setStatusMessage("Searching...");
                this.mainWindow.disableWindow();

                /*
                 * execute search in a different thread from the Swing Event
                 * Dispatcher Thread.
                 */

                SearchByCriteriaActionThread thread =
                    new SearchByCriteriaActionThread();
                thread.start();
            } else if (actionCmd.equals(MainWindow.SEARCH_ALL_BUTTON_CMD)) {
                this.mainWindow.setStatusMessage("Loading all rooms...");
                this.mainWindow.resetSearch();
                this.mainWindow.disableWindow();

                /*
                 * execute search in a different thread from the Swing Event
                 * Dispatcher Thread.
                 */

                SearchAllActionThread thread = new SearchAllActionThread();
                thread.start();
            } else if (actionCmd.equals(MainWindow.RESET_BUTTON_CMD)) {
                this.mainWindow.resetSearch();
                this.model.reset();
            } else if (actionCmd.equals(MainWindow.BOOK_BUTTON_CMD)) {
                this.mainWindow.setStatusMessage("Booking room...");
                this.mainWindow.disableWindow();
                int selectedRoom = this.mainWindow.getSelectedRoomIndex();

                if (selectedRoom > -1) {
                    /*
                     * a row room is selected in the JTable, now check if it's
                     * booked already
                     */
                    if (!this.model.isRoomBooked(selectedRoom)) {

                        String customerId = (String) JOptionPane
                                .showInputDialog(this.mainWindow,
                                        "Enter the customer ID (8 digits):",
                                        "Book A Room",
                                        JOptionPane.PLAIN_MESSAGE, null, null,
                                        "");

                        if (customerId != null) {
                            if ((customerId.trim().length() > 0)) {
                                /*
                                 * execute booking in a different thread from
                                 * the Swing Event Dispatcher Thread.
                                 */
                                BookActionThread thread = new BookActionThread(
                                        selectedRoom, customerId);
                                thread.start();
                            } else {
                                // ok button clicked without entering a customer
                                this.mainWindow.enableWindow();
                                this.mainWindow.setStatusMessage("Ready");
                                JOptionPane
                                        .showMessageDialog(
                                                this.mainWindow,
                                                "Please enter a customer, the "
                                                + "selected room was not "
                                                + "booked.",
                                                "No Customer Entered",
                                                JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            // cancel button was clicked
                            this.mainWindow.enableWindow();
                            this.mainWindow.setStatusMessage("Ready");
                        }
                    } else {
                        // room booked already
                        this.mainWindow.enableWindow();
                        this.mainWindow.setStatusMessage("Ready");
                        JOptionPane
                                .showMessageDialog(
                                        mainWindow,
                                        "Cannot book the selected room - "
                                        + "already booked for another customer "
                                        + "ID.",
                                        "Room Already Booked",
                                        JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    // no row room is selected in JTable
                    this.mainWindow.enableWindow();
                    this.mainWindow.setStatusMessage("Ready");
                    JOptionPane.showMessageDialog(this.mainWindow,
                            "Please select a room to book.",
                            "No Room Selected", JOptionPane.WARNING_MESSAGE);
                }

            }
        } catch (Exception e) {
            // handle any possible uncaught runtime exceptions

            BaseWindow.handleException(this.mainWindow,
                    "An error occurred in the application.", e);
            this.mainWindow.enableWindow();
            this.mainWindow.setStatusMessage("Ready");
        }
    }

    /*
     * Base class to implement the template method pattern for handling GUI
     * events in a thread separate from the Swing Event Dispatcher Thread.
     */
    private abstract class ActionThread extends Thread {

        private MainWindow window;

        /*
         * The code to execute when handling an event.
         */
        abstract void doAction();

        /*
         * Instantiate an event handling thread.
         */
        ActionThread() {
            this.window = mainWindow;
        }

        /**
         * Handles the event, then enables the main window and sets its status
         * message when done.
         */
        @Override
        public void run() {
            doAction();

            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    window.enableWindow();
                    window.setStatusMessage("Ready");
                }
            });
        }
    }

    /*
     * Base class for all search events. Abstracts out common code for checking
     * whether any search results were found.
     */
    private abstract class SearchActionThread extends ActionThread {

        /*
         * Checks the model if any rooms were found after a search, and displays
         * a dialog with the specified message if none were found.
         */
        void checkRoomsFound(final String message) {
            if (model.getRowCount() < 1) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        JOptionPane.showMessageDialog(mainWindow, message,
                                "No Rooms Found",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        }
    }

    /*
     * Thread to handle the search button event.
     */
    private class SearchByCriteriaActionThread extends SearchActionThread {

        @Override
        void doAction() {
            String hotel = mainWindow.getSearchHotelName();
            String city = mainWindow.getSearchCity();

            try {
                model.findRooms(hotel, city);
                checkRoomsFound("No rooms matching the hotel and/or city were "
                        + "found.");
            } catch (RemoteException e) {
                BaseWindow
                        .handleException(
                                mainWindow,
                                "A network error occurred while searching the "
                                + "room reservation system.",
                                e);
            }
        }
    }

    /*
     * Thread to handle the search all button event.
     */
    private class SearchAllActionThread extends SearchActionThread {

        @Override
        void doAction() {
            try {
                model.findAllRooms();
                checkRoomsFound("No rooms were found in the system.");
            } catch (RemoteException e) {
                BaseWindow
                        .handleException(
                                mainWindow,
                                "A network error occurred while searching the "
                                + "room reservation system.",
                                e);
            }
        }
    }

    /*
     * Thread to handle the book button event.
     */
    private class BookActionThread extends ActionThread {

        private int selectedRoom;
        private String customerId;

        /*
         * Create a thread that will book the selected room for the given
         * customer ID.
         */
        public BookActionThread(int bookSelectedRoom, String bookCustomerId) {
            this.selectedRoom = bookSelectedRoom;

            /* customer id limited to 8 characters */
            if (bookCustomerId.length() > 8) {
                this.customerId = bookCustomerId.substring(0, 8);
            } else {
                this.customerId = bookCustomerId;
            }
        }

        @Override
        void doAction() {
            boolean booked;

            try {
                booked = model.bookRoom(selectedRoom, customerId);

                if (booked) {
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            JOptionPane.showMessageDialog(mainWindow,
                                    "The selected room was booked for customer "
                                    + "ID \"" + customerId + "\".",
                                    "Room Booked",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                } else {
                    javax.swing.SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            JOptionPane
                                    .showMessageDialog(
                                            mainWindow,
                                            "Cannot book the selected room - "
                                            + "already booked for another "
                                            + "customer ID.",
                                            "Room Already Booked",
                                            JOptionPane.WARNING_MESSAGE);
                        }
                    });
                }
            } catch (RemoteException e) {
                BaseWindow.handleException(mainWindow,
                        "A network error occurred while booking the room.", e);
            } catch (RecordNotFoundException e) {
                BaseWindow.handleException(mainWindow,
                        "Cannot book the room, it is no longer available.", e);
            } catch (SecurityException e) {
                BaseWindow
                        .handleException(
                                mainWindow,
                                "Cannot book the room - it may no longer be "
                                + "available, try again.",
                                e);
            }

        }
    }

}
