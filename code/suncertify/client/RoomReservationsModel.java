package suncertify.client;

import java.rmi.RemoteException;

import javax.swing.table.TableModel;

import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * A model for the list of hotel rooms returned by a search. Model is a
 * two-dimensional representation of the data, consisting of rows and columns.
 * <p>
 * Interface is part of a Model-View-Controller architecture for the hotel room
 * reservation system. By extending the {@link javax.swing.table.TableModel
 * TableModel} interface, the design reuses the Observer design pattern inherent
 * in this interface and the "observers" that implement the
 * {@link javax.swing.event.TableModelListener TableModelListener} interface.
 * Implementations of this interface are then the "observables" in the Observer
 * pattern.
 * <p>
 * When the model updates its data, it will notify the view as an observer of
 * the model to refresh itself with the latest data via implementations of the
 * {@link javax.swing.table.TableModel TableModel} and
 * {@link javax.swing.event.TableModelListener TableModelListener} interfaces.
 * 
 * @see javax.swing.table.TableModel
 * @see javax.swing.event.TableModelListener
 * 
 * @author Oliver Hernandez
 * 
 */
public interface RoomReservationsModel extends TableModel {

    /**
     * Initializes the model, such as retrieving any data or connecting to any
     * resources. If any exceptions can be thrown during initialization of the
     * model, this method must be implemented instead of performing such
     * initializations in a constructor. This allows handling of any such
     * exceptions during initialization in the view.
     * 
     * @throws RoomReservationsException
     *             if an error occurred during initialization.
     */
    public void initialize() throws RoomReservationsException;

    /**
     * Find all the hotel rooms that match the search criteria passed in. The
     * search criteria is case-sensitive. A match occurs when the room field
     * exactly matches or begins with the search criteria. For example, a hotel
     * named "Sunny Inn" will match "Sun" and "Sunny", in addition to
     * "Sunny Inn".
     * 
     * @param hotelName
     *            the hotel name to search rooms by.
     * @param city
     *            the city to search rooms by.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     */
    public void findRooms(String hotelName, String city) throws RemoteException;

    /**
     * Find all the hotel rooms in the room reservation system.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     */
    public void findAllRooms() throws RemoteException;

    /**
     * Clear out all the search results in the model.
     */
    public void reset();

    /**
     * Returns the room ID at the specified row in the model.
     * 
     * @param rowId
     *            a zero-based row index.
     * @return the room ID at the specified row, or -1 if not found.
     */
    public long getRoomIdAtRow(int rowId);

    /**
     * Book the hotel room at the specified row in the model.
     * 
     * @param rowId
     *            a zero-based row index.
     * @param customerId
     *            the customer ID to book the room for.
     * 
     * @return <code>true</code> when the room was successfully booked for the
     *         customer, <code>false</code> otherwise.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     * @throws RecordNotFoundException
     *             when the room has been deleted.
     * @throws SecurityException
     *             when the room was attempted to be booked by another user, or
     *             the request to book the room timed-out.
     */
    public boolean bookRoom(int rowId, String customerId)
            throws RemoteException, RecordNotFoundException, SecurityException;

    /**
     * Returns whether or not the model displayed to the view has the selected
     * room booked. The view would show a value in the Customer ID column if the
     * room was booked.
     * 
     * @param selectedRoom
     *            the row index of the room selected by the view.
     * 
     * @return <code>true</code> if the selected room is booked,
     *         <code>false</code> otherwise.
     */
    public boolean isRoomBooked(int selectedRoom);

}
