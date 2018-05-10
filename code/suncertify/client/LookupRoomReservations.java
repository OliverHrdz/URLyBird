package suncertify.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import suncertify.application.RoomReservationSystem;
import suncertify.application.URLyBirdConfiguration;
import suncertify.server.RemoteRoomReservations;
import suncertify.server.ServerException;
import suncertify.server.URLyBirdReservations;

/**
 * Service locator class for looking up the room reservation system. Class is a
 * component of the Business Delegate pattern implemented to abstract clients
 * from the room reservation server.
 * 
 * @author Oliver Hernandez
 * 
 */
class LookupRoomReservations {

    static final URLyBirdConfiguration configuration = URLyBirdConfiguration
            .getInstance();

    private static final LookupRoomReservations instance =
        new LookupRoomReservations();

    /**
     * Get the singleton instance of <code>LookupRoomReservations</code>.
     * 
     * @return the singleton <code>LookupRoomReservations</code> object.
     */
    public static LookupRoomReservations getInstance() {
        return instance;
    }

    private LookupRoomReservations() {
    }

    /**
     * Locates the room reservations server of the URLyBird Hotel Room
     * Reservation System.
     * 
     * @return the room reservations server.
     * 
     * @throws MalformedURLException
     *             when a network error occurs.
     * @throws RemoteException
     *             when a network error occurs.
     * @throws NotBoundException
     *             when the service cannot be located on the network.
     * @throws ServerException
     *             when an error occurs initializing the server.
     */
    public RemoteRoomReservations lookup() throws MalformedURLException,
            RemoteException, NotBoundException, ServerException {

        RemoteRoomReservations system = null;

        switch (RoomReservationSystem.getExecMode()) {
        case STANDALONE:
            system = new URLyBirdReservations();
            break;
        case CLIENT:
            system = (RemoteRoomReservations) Naming.lookup("rmi://"
                    + configuration.getRMIHost() + "/RoomReservations");
        }

        return system;
    }

}
