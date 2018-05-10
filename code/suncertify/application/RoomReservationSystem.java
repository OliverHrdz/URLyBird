package suncertify.application;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import suncertify.client.MainController;
import suncertify.client.URLyBirdModel;
import suncertify.server.MainWindow;
import suncertify.server.RemoteRoomReservations;
import suncertify.server.URLyBirdReservations;

/**
 * The Hotel Room Reservation System for URLyBird, Inc., discount hotel room
 * broker company. The system accepts 1 argument that specifies the execution
 * mode, either "server" or "alone". The mode "server" indicates only the server
 * program must run, while "alone" indicates standalone mode where the server
 * and GUI run together. In standalone mode, the server cannot be connected to
 * remotely. If no argument is specified, only the network client GUI runs and
 * connects to a server remotely.
 * 
 * @author Oliver Hernandez
 * 
 */
public class RoomReservationSystem {

    private static ExecutionMode execMode = ExecutionMode.STANDALONE;

    /* static reference to RMI exported object needed to avoid premature GC */
    private static RemoteRoomReservations remoteReservations;

    /**
     * Main entry point to the URLyBird Hotel Room Reservation System.
     * 
     * @param args
     *            main program arguments.
     */
    public static void main(String[] args) {

        /* determine the execution mode */

        if (args.length > 0) {
            execMode = ExecutionMode.getMode(args[0]);
            if (execMode == null) {
                handleInvalidArgument();
            }
        } else {
            execMode = ExecutionMode.CLIENT;
        }

        switch (execMode) {
        case STANDALONE:
        case CLIENT:
            /*
             * start the MVC controller; the model passed in to it will
             * automatically determine whether to connect to the server remotely
             * or locally in standalone mode.
             */
            MainController controller = new MainController(new URLyBirdModel());
            controller.start();

            break;
        case SERVER:
            /* start just the server */
            startServer();

            break;
        default:
            handleInvalidArgument();
        }

    }

    /**
     * Get the execution mode the application is running. Returns an instance of
     * {@link ExecutionMode} enumeration.
     * 
     * @return whether the application is running in client, server, or
     *         standalone mode.
     */
    public static ExecutionMode getExecMode() {
        return execMode;
    }

    private static void startServer() {
        MainWindow window = new MainWindow();
        window.startup(); // display server window

        /*
         * Start the <code>RMI</code> server and export the remote room
         * reservation system to it.
         */

        try {
            remoteReservations = new URLyBirdReservations();

            RemoteRoomReservations stub =
                (RemoteRoomReservations) UnicastRemoteObject
                    .exportObject(remoteReservations, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("RoomReservations", stub);

            window.enableWindow();
            window.setStatusMessage("Running");
        } catch (Exception e) {
            BaseWindow.handleException(window,
                    "An error occurred starting the room reservation server.",
                    e);

            JOptionPane.showMessageDialog(window,
                    "Verify the application is configured correctly.", "Error",
                    JOptionPane.INFORMATION_MESSAGE);

            window.enableMenus();
            window.setStatusMessage("Server not running");
        }

    }

    private static void handleInvalidArgument() {
        System.out.println("Failed to start the URLyBird Hotel Room "
                + "Reservation System.");
        System.out.println("   An invalid mode was specified, only \"server\" "
                + "or \"alone\" are valid.");
        System.out.println("");
        System.out.println("   If no mode is specified, only the URLyBird "
                + "Client will run.");
        System.out.println("   java -jar <path>runme.jar [server | alone]");
        System.exit(-1);
    }

}