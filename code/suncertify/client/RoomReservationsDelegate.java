package suncertify.client;

import java.rmi.RemoteException;
import java.util.List;

import suncertify.application.Room;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.server.RemoteRoomReservations;

/**
 * Delegate class for clients of the URLyBird Room Reservation System. Class is
 * the delegate implementation of the Business Delegate pattern. Abstracts from
 * a client the underlying implementation details of the business services they
 * need, such as the database system, network I/O, etc. These details are hidden
 * behind the simpler methods the client calls in this class.
 * 
 * @author Oliver Hernandez
 * 
 */
class RoomReservationsDelegate {

    private RemoteRoomReservations roomReservations;

    /**
     * Constructs the business delegate class instance for calling the business
     * methods needed by clients of the room reservation system. Access to the
     * underlying room reservation server is established during instantiation.
     * 
     * @throws RoomReservationsException
     *             when an error occurs accessing the room reservation server.
     */
    RoomReservationsDelegate() throws RoomReservationsException {
        try {
            this.roomReservations = LookupRoomReservations.getInstance()
                    .lookup();
        } catch (Exception e) {
            throw new RoomReservationsException(
                    "An error occurred connecting to the room reservations "
                    + "server.", e);
        }
    }

    /**
     * Book a room at the hotel.
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
     *             when the room has been deleted.
     * @throws SecurityException
     *             when the room was attempted to be booked by another user, or
     *             the request to book the room timed-out.
     */
    boolean book(long roomId, String customerId) throws RemoteException,
            RecordNotFoundException, SecurityException {
        return this.roomReservations.book(roomId, customerId);
    }

    /**
     * Search for hotel rooms based on the hotel name and/or city.
     * 
     * @param hotelName
     *            the name of the hotel to search for rooms by
     * @param city
     *            the name of the city to search for rooms by
     * @return a {@link java.util.List List} of
     *         {@link suncertify.application.Room Room} objects or
     *         <code>null</code> if none were found.
     * @throws RemoteException
     *             when a network error occurs.
     */
    List<Room> search(String hotelName, String city) throws RemoteException {
        String[] criteria = { hotelName, city, null, null, null, null, null };

        return this.roomReservations.search(criteria);
    }

    /**
     * Retrieve all the hotel rooms in the reservation system.
     * 
     * @return a {@link java.util.List List} of
     *         {@link suncertify.application.Room Room} objects
     * @throws RemoteException
     *             when a network error occurs.
     */
    List<Room> getAllRooms() throws RemoteException {
        return search(null, null);
    }

}
