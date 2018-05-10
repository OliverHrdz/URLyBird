package suncertify.client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import suncertify.application.BaseWindow;

/**
 * The main client application window.
 * 
 * @author Oliver Hernandez
 * 
 */
@SuppressWarnings("serial")
public class MainWindow extends BaseWindow {

    /*
     * the "command" labels that will be used by the action listeners of this
     * window to determine the source of the event.
     */
    public static final String SEARCH_BUTTON_CMD = "Search";
    public static final String SEARCH_ALL_BUTTON_CMD = "Search All";
    public static final String RESET_BUTTON_CMD = "Reset";
    public static final String BOOK_BUTTON_CMD = "Book";

    /*
     * search field components
     */
    private JLabel searchCriteriaLabel = new JLabel(
            "Search Criteria (case sensitive):");
    private JLabel hotelNameLabel = new JLabel("Hotel Name");
    private JTextField hotelNameField = new JTextField(15);
    private JLabel cityLabel = new JLabel("City");
    private JTextField cityField = new JTextField(15);

    /*
     * buttons
     */
    private JButton searchButton = new JButton(SEARCH_BUTTON_CMD);
    private JButton searchAllButton = new JButton(SEARCH_ALL_BUTTON_CMD);
    private JButton resetButton = new JButton(RESET_BUTTON_CMD);
    private JButton bookButton = new JButton(BOOK_BUTTON_CMD);

    /*
     * search results table
     */
    private JTable searchResultsTable;

    /**
     * Constructs the main client window and sets it to a disabled state where
     * no buttons or fields can be used.
     */
    public MainWindow() {
        setTitle("URLyBird" + this.executionMode);

        /*
         * Create top panel of main application window that will contain the
         * search fields and search buttons
         */
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        /*
         * Create panel for search criteria label
         */
        JPanel searchCriteriaLabelPanel = new JPanel();
        searchCriteriaLabelPanel.setLayout(new BoxLayout(
                searchCriteriaLabelPanel, BoxLayout.LINE_AXIS));
        searchCriteriaLabel.setHorizontalAlignment(SwingConstants.LEADING);
        searchCriteriaLabelPanel.add(searchCriteriaLabel);
        searchCriteriaLabelPanel
                .add(Box.createRigidArea(new Dimension(100, 0)));
        searchCriteriaLabelPanel.add(Box.createHorizontalGlue());

        /*
         * Create panel that will contain the search fields
         */
        JPanel searchCriteriaPanel = new JPanel(new GridLayout(0, 1, 0, 5));

        /*
         * Build search components for hotel name criteria
         */

        JPanel hotelNamePanel = new JPanel();
        hotelNamePanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 12, 0));

        hotelNameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
        hotelNameLabel.setToolTipText("Enter a hotel to search for");
        hotelNamePanel.add(hotelNameLabel);

        hotelNameField.setToolTipText("Enter a hotel to search for");
        hotelNamePanel.add(hotelNameField);

        /*
         * Build search components for city criteria
         */

        JPanel cityPanel = new JPanel();
        cityPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 12, 0));

        cityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        cityLabel.setToolTipText("Enter a city to search for");
        cityPanel.add(cityLabel);

        cityField.setToolTipText("Enter a city to search for");
        cityPanel.add(cityField);

        /*
         * Add search criteria components to search panel
         */
        searchCriteriaPanel.add(searchCriteriaLabelPanel);
        searchCriteriaPanel.add(hotelNamePanel);
        searchCriteriaPanel.add(cityPanel);

        /*
         * Create panel that will contain search buttons
         */
        JPanel searchButtonsPanel = new JPanel();
        searchButtonsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 5,
                0));
        searchButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

        /*
         * Build search buttons
         */
        searchButton
                .setToolTipText("Search rooms that match the hotel "
                        + "and/or city");
        searchAllButton.setToolTipText("Search for all rooms");
        resetButton.setToolTipText("Clear out the search values and results");

        /*
         * Add search buttons to button panel
         */

        searchButtonsPanel.add(searchButton);
        searchButtonsPanel.add(searchAllButton);
        searchButtonsPanel.add(resetButton);
        searchButtonsPanel.add(Box.createHorizontalGlue());

        /*
         * Add components to top panel of main application window
         */

        topPanel.add(searchCriteriaPanel);
        topPanel.add(searchButtonsPanel);

        /*
         * Build search results label and table
         */

        JLabel searchResultsLabel = new JLabel("Rooms Found:");

        searchResultsTable = new JTable();
        searchResultsTable
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsTable.setToolTipText("Click to select a room to book");
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);

        /*
         * Build book button and panel
         */

        JPanel bookButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        bookButton.setToolTipText("Book the selected room");
        bookButtonPanel.add(bookButton);

        /*
         * Create root panel to contain all components of main window
         */

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new GridBagLayout());

        /*
         * Add components in the Grid Bag
         */

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 10, 5, 0);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        rootPanel.add(topPanel, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 10, 0, 10);
        c.ipady = 15;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        rootPanel.add(searchResultsLabel, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 10, 0, 10);
        c.ipady = 80;
        c.weightx = 0.5;
        c.weighty = 0.9;
        c.fill = GridBagConstraints.BOTH;
        rootPanel.add(scrollPane, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 5, 0, 10);
        c.anchor = GridBagConstraints.LAST_LINE_START;
        rootPanel.add(bookButtonPanel, c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 0.1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_END;
        rootPanel.add(statusBar, c);

        /*
         * Add root panel to main application window and set size and location
         */

        this.getContentPane().add(rootPanel);

        this.setSize(650, 400);
        this.setLocationRelativeTo(null); // center onscreen
        this.disableWindow();
    }

    /**
     * Clears the search fields in the window.
     */
    public void resetSearch() {
        hotelNameField.setText("");
        cityField.setText("");
    }

    /**
     * Get the name of the hotel entered to search for a hotel room by.
     * 
     * @return the hotel name entered in the hotel name text field.
     */
    public String getSearchHotelName() {
        return hotelNameField.getText();

    }

    /**
     * Get the city entered to search for a hotel room by.
     * 
     * @return the city entered in the city text field.
     */
    public String getSearchCity() {
        return cityField.getText();
    }

    /**
     * Set the table model of the window's {@link javax.swing.JTable JTable} for
     * displaying search results.
     * 
     * @param model
     *            a {@link RoomReservationsModel} object.
     */
    public void setTableModel(RoomReservationsModel model) {
        this.searchResultsTable.setModel(model);
    }

    /**
     * Add a controller to handle events for the search button.
     * 
     * @param listener
     *            a controller that implements the
     *            {@link java.awt.event.ActionListener ActionListener}
     *            interface.
     */
    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    /**
     * Add a controller to handle events for the search all button.
     * 
     * @param listener
     *            a controller that implements the
     *            {@link java.awt.event.ActionListener ActionListener}
     *            interface.
     */
    public void addSearchAllButtonListener(ActionListener listener) {
        searchAllButton.addActionListener(listener);
    }

    /**
     * Add a controller to handle events for the reset button.
     * 
     * @param listener
     *            a controller that implements the
     *            {@link java.awt.event.ActionListener ActionListener}
     *            interface.
     */
    public void addResetButtonListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    /**
     * Add a controller to handle events for the book button.
     * 
     * @param listener
     *            a controller that implements the
     *            {@link java.awt.event.ActionListener ActionListener}
     *            interface.
     */
    public void addBookButtonListener(ActionListener listener) {
        bookButton.addActionListener(listener);
    }

    /**
     * Get the index of the room selected in the displayed
     * {@link javax.swing.JTable JTable}.
     * 
     * @return index of the selected room.
     */
    public int getSelectedRoomIndex() {
        return searchResultsTable.getSelectedRow();
    }

    /**
     * Set the width of the specified column in the displayed
     * {@link javax.swing.JTable JTable}.
     * 
     * @param columnIdx
     *            index of column to set width for.
     * @param width
     *            width to set column to.
     */
    public void setColumnWidth(int columnIdx, int width) {
        TableColumn column = null;

        column = searchResultsTable.getColumnModel().getColumn(columnIdx);
        column.setPreferredWidth(width);
    }

    /**
     * Set the {@link javax.swing.table.TableCellRenderer TableCellRenderer} of
     * the specified column in the displayed {@link javax.swing.JTable JTable}.
     * 
     * @param columnIdx
     *            index of column to set cell renderer for.
     * @param renderer
     *            renderer to set column cell to.
     */
    public void setColumnRenderer(int columnIdx, TableCellRenderer renderer) {
        TableColumn column = null;

        column = searchResultsTable.getColumnModel().getColumn(columnIdx);
        column.setCellRenderer(renderer);
    }

    @Override
    protected void setComponents(boolean enabled) {
        hotelNameField.setEnabled(enabled);
        cityField.setEnabled(enabled);

        searchButton.setEnabled(enabled);
        searchAllButton.setEnabled(enabled);
        resetButton.setEnabled(enabled);
        bookButton.setEnabled(enabled);

        searchResultsTable.setEnabled(enabled);
    }

}
