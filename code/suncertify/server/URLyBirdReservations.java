package suncertify.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import suncertify.application.Room;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * The implementation of the {@link RemoteRoomReservations} for the URLyBird
 * Hotel Room Reservation System.
 * 
 * @see RemoteRoomReservations
 * 
 * @author Oliver Hernandez
 * 
 */
public class URLyBirdReservations implements RemoteRoomReservations {

    private Data database = Data.getInstance();

    /**
     * Create the server, initializing the connection to the database.
     * 
     * @throws ServerException
     *             when an error occurred initializing the server.
     */
    public URLyBirdReservations() throws ServerException {
        try {
            this.database.open();
        } catch (IOException e) {
            throw new ServerException(
                    "An error occurred initializing the server.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean book(long roomId, String customerId)
            throws RecordNotFoundException, SecurityException {
        long cookie;
        String[] record;
        boolean booked = false;

        record = this.database.readRecord(roomId);

        if (record[6] == null || record[6].trim().isEmpty()) {
            record[6] = customerId;
            cookie = this.database.lockRecord(roomId);
            this.database.updateRecord(roomId, record, cookie);
            this.database.unlock(roomId, cookie);
            booked = true;
        }

        return booked;
    }

    /**
     * {@inheritDoc}
     */
    public List<Room> search(String[] criteria) {
        Room room;
        String[] record;
        List<Room> rooms = null;
        long[] roomIds = this.database.findByCriteria(criteria);

        if (roomIds.length > 0) {
            rooms = new ArrayList<Room>();

            for (long currRoomId : roomIds) {
                try {
                    record = this.database.readRecord(currRoomId);

                    room = new Room();
                    room.setId(currRoomId);
                    room.setHotelName(record[0]);
                    room.setCity(record[1]);
                    room.setMaxOccupancy(record[2]);
                    room.setSmoking(record[3]);
                    room.setRate(record[4]);
                    room.setDate(record[5]);
                    room.setOwner(record[6]);

                    rooms.add(room);
                } catch (RecordNotFoundException e) {
                    /*
                     * row may have been deleted by another user, so we'll just
                     * skip it
                     */
                }
            }
        }

        return rooms;
    }

}
