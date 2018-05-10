package suncertify.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import suncertify.application.Room;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * Interface defining the business methods of the room reservation system.
 * Extends {@link java.rmi.Remote} for connecting to the system remotely over
 * <code>RMI</code>.
 * 
 * @author Oliver Hernandez
 * 
 */
public interface RemoteRoomReservations extends Remote {

    /**
     * Search for hotel rooms matching the specified criteria.
     * 
     * @param criteria
     *            a <code>String</code> array containing values that should be
     *            searched for.
     * 
     * @return a collection of {@link suncertify.application.Room Room} objects
     *         or <code>null</code> if none were found.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     * 
     * @see suncertify.db.DBAccess#findByCriteria(java.lang.String[])
     */
    public List<Room> search(String[] criteria) throws RemoteException;

    /**
     * Book the specified hotel room.
     * 
     * @param roomId
     *            the ID of the room to book.
     * @param customerId
     *            the customer ID to book the room for.
     * 
     * @return <code>true</code> if the room was booked successfully,
     *         <code>false</code> if the room is already booked.
     * 
     * @throws RemoteException
     *             when a network error occurs.
     * @throws RecordNotFoundException
     *             when the specified room is not found.
     * @throws SecurityException
     *             when the room was attempted to be booked by another user, or
     *             the request to book the room timed-out.
     */
    public boolean book(long roomId, String customerId) throws RemoteException,
            RecordNotFoundException, SecurityException;

}
