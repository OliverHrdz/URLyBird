package suncertify.client;

import java.rmi.RemoteException;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import suncertify.application.Room;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * Model implementation of the room reservation system's MVC design.
 * 
 * @author Oliver Hernandez
 * 
 * @see RoomReservationsModel
 * @see javax.swing.table.AbstractTableModel
 */
@SuppressWarnings("serial")
public class URLyBirdModel extends AbstractTableModel implements
        RoomReservationsModel {

    private String[] columnNames = { "Hotel Name", "City", "Max Occupancy",
            "Smoking?", "Rate", "Date Available", "Customer ID" };

    private List<Room> rooms;

    private RoomReservationsDelegate delegate;

    private String lastHotelSearched;

    private String lastCitySearched;

    /**
     * Constructs an <code>URLyBirdModel</code> object.
     */
    public URLyBirdModel() {
    }

    /**
     * Initializes the model's server connection.
     * 
     * @throws RoomReservationsException
     *             if an error occurs connecting to the room reservations
     *             server.
     * 
     * @see RoomReservationsModel#initialize()
     */
    public void initialize() throws RoomReservationsException {
        this.delegate = new RoomReservationsDelegate();
    }

    /**
     * Finds all the hotel rooms matching the search criteria and notifies
     * {@link javax.swing.event.TableModelListener observers} of this model that
     * the data has changed.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     * 
     * @see RoomReservationsModel#findRooms(String, String)
     */
    public void findRooms(String hotelName, String city)
    throws RemoteException {
        this.rooms = this.delegate.search(hotelName, city);
        fireTableDataChanged();

        // save search parameters for refreshing
        this.lastHotelSearched = hotelName;
        this.lastCitySearched = city;
    }

    /**
     * Finds all the hotel rooms and notifies
     * {@link javax.swing.event.TableModelListener observers} of this model that
     * the data has changed.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     * 
     * @see RoomReservationsModel#findAllRooms()
     */
    public void findAllRooms() throws RemoteException {
        this.rooms = this.delegate.getAllRooms();
        fireTableDataChanged();

        // save search parameters for refreshing
        this.lastHotelSearched = null;
        this.lastCitySearched = null;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        this.rooms = null;
        this.lastHotelSearched = null;
        this.lastCitySearched = null;
        fireTableDataChanged();
    }

    /**
     * {@inheritDoc}
     */
    public long getRoomIdAtRow(int rowId) {
        long roomId = -1;

        if (this.rooms != null) {
            roomId = this.rooms.get(rowId).getId();
        }

        return roomId;
    }

    /**
     * {@inheritDoc}
     */
    public boolean bookRoom(int selectedRoom, String customerId)
            throws RemoteException, RecordNotFoundException, SecurityException {
        boolean booked = false;
        long roomId = this.rooms.get(selectedRoom).getId();
        String currCustomer = this.rooms.get(selectedRoom).getOwner();

        if (currCustomer == null || currCustomer.isEmpty()) {
            try {
                booked = this.delegate.book(roomId, customerId);
            } catch (SecurityException e) {
                // refresh model to display possible other customer that booked
                // room
                findRooms(this.lastHotelSearched, this.lastCitySearched);
                throw e;
            }

            if (booked) {
                this.rooms.get(selectedRoom).setOwner(customerId);
                fireTableDataChanged();
            } else {
                // refresh model to display last customer that booked room
                findRooms(this.lastHotelSearched, this.lastCitySearched);
            }
        }

        return booked;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRoomBooked(int selectedRoom) {
        boolean booked = true;

        String currCustomer = this.rooms.get(selectedRoom).getOwner();

        if (currCustomer == null || currCustomer.isEmpty()) {
            booked = false;
        }

        return booked;
    }

    /**
     * Returns the number of rows of rooms currently in the data returned from
     * the last search.
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        int numRows = 0;

        if (this.rooms != null) {
            numRows = this.rooms.size();
        }

        return numRows;
    }

    /**
     * Returns the name of the room field at the specified column index.
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int column) {
        return this.columnNames[column];
    }

    /**
     * Returns the number of fields in a room row.
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    /**
     * Returns the value of the room field at the specified row and column
     * index.
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (this.rooms != null) {
            for (int i = 0; i < this.rooms.size(); i++) {
                if (i == rowIndex) {
                    switch (columnIndex) {
                    case 0:
                        return this.rooms.get(i).getHotelName();
                    case 1:
                        return this.rooms.get(i).getCity();
                    case 2:
                        return this.rooms.get(i).getMaxOccupancy();
                    case 3:
                        return this.rooms.get(i).getSmoking();
                    case 4:
                        return this.rooms.get(i).getRate();
                    case 5:
                        return this.rooms.get(i).getDate();
                    case 6:
                        return this.rooms.get(i).getOwner();
                    }

                    return null;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    /**
     * Return the class type of the data in the specified column.
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

}
