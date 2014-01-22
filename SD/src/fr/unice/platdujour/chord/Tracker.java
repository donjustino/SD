package fr.unice.platdujour.chord;

import java.rmi.Remote;
import java.rmi.RemoteException;

import fr.unice.platdujour.exceptions.AlreadyRegisteredException;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface defines the API of a tracker. It is supposed to keep track of
 * peers that joined and leaved the network.
 */
public interface Tracker extends Remote {

    /**
     * Register a new peer in the structure maintained by the tracker.
     *
     * @param peer The peer that has just joined the network
     * @throws AlreadyRegisteredException If the specified peer is already
     * registered
     * @throws RemoteException
     */
    void register(Peer peer)
            throws AlreadyRegisteredException, RemoteException;

    /**
     * @return A peer that belongs to the network, randomly
     * @throws RemoteException
     */
    Peer getRandomPeer() throws RemoteException;

    /**
     * Méthode qui supprime un peer du tracker
     *
     */

    public boolean delPeer(Peer peer) throws RemoteException;

    /**
     * Méthode qui permet de redéfinr un peer avec un directory vide, le
     * directory d'un Peer mort
     *
     */
    public void restoreData(Map<String, String> directoryReplicat) throws RemoteException;

}
