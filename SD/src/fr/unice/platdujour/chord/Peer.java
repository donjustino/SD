package fr.unice.platdujour.chord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * This interface declares the main operations that a peer sustain. A peer is
 * defined as a Remote object as its methods are called remotely (usually from
 * other peers).
 */
public interface Peer extends Remote {

    /**
     * Initializes the peer-to-peer network, and makes the peer join it. This
     * method must be called only once by the first peer which joins the
     * network.
     *
     * @throws RemoteException
     */
    void create() throws RemoteException;

    /**
     * Allows the current peer to join the peer-to-peer network using the
     * specified Peer.
     *
     * @param landmarkPeerb The peer from which the network is accessed.
     * @throws RemoteException
     */
    void join(Peer landmarkPeer) throws RemoteException;

    /**
     * This method finds the peer in the network which is responsible for
     * storing content whose identifier is the specified identifier.
     *
     * @param id The identifier for which the peer manager is searched
     * @return The peer that manages the specified identifier
     * @throws RemoteException
     */
    Peer findSuccessor(Identifier id) throws RemoteException;

    /**
     * @return The identifier of the peer in the virtual ring
     * @throws RemoteException
     */
    Identifier getId() throws RemoteException;

    /**
     * @return The predecessor peer of the peer in the virtual ring
     * @throws RemoteException
     */
    Peer getPredecessor() throws RemoteException;

    /**
     * @return The successor peer of the peer in the virtual ring
     * @throws RemoteException
     */
    Peer getSuccessor() throws RemoteException;

    /**
     * Changes the predecessor peer of the peer in the virtual ring
     *
     * @param peer The new predecessor
     * @throws RemoteException
     */
    void setPredecessor(Peer peer) throws RemoteException;

    /**
     * Changes the successor peer of the peer in the virtual ring
     *
     * @param peer The new successor
     * @throws RemoteException
     */
    void setSuccessor(Peer peer) throws RemoteException;

    /**
     * Updates the successor link and notifies the predecessor. This method
     * should be called periodically.
     *
     * @throws RemoteException
     */
    void stabilize() throws RemoteException;

    /**
     * Updates the predecessor link. This method should be called from the
     * stabilize method.
     *
     * @param peer
     * @throws RemoteException
     */
    void notify(Peer peer) throws RemoteException;

    /**
     * Stores an entry in the local storage structure.
     *
     * @param restaurant Name of the restaurant to store locally.
     * @param dailySpecial Name of the daily special to store locally.
     * @throws RemoteException
     */
    void put(String restaurant, String dailySpecial) throws RemoteException;

    /**
     * Looks for the specified restaurant in the local storage structure and
     * returns the associated daily special.
     *
     * @param restaurant The name of the restaurant that is searched in the
     * local storage.
     * @return The name of the daily special associated to the specified
     * restaurant or null if no entry is found for the specified restaurant.
     * @throws RemoteException
     */
    String get(String restaurant) throws RemoteException;

    /**
     * Replaces the toString method to have a remote description of the peer.
     *
     * @return a String describing the peer state
     * @throws RemoteException
     */
    String describe() throws RemoteException;

    /**
     * Simulates a peer's savage death. This method can be used to remotely to
     * kill a peer.
     *
     * @throws RemoteException
     */
    void die() throws RemoteException;

    /**
     * Défini le sucesseur du sucesseur
     *
     */
    public void setSuccessorofSuccessor(Peer peer) throws RemoteException;

    /**
     * Retourne le tracker
     *
     */
    public Tracker getTracker() throws RemoteException;

    /**
     * Défini le tracker
     *
     */
    public void setTracker(Tracker tracker) throws RemoteException;

    /**
     * Retourne la cléf du peer
     *
     */
    public String returnKey() throws RemoteException;

    /**
     * Retourne la valeur du peer
     *
     */
    public String returnValue() throws RemoteException;

    /**
     * Méthode qui sauvegarde les replicats des successeurs
     *
     */
    public void saveReplicat() throws RemoteException;

    /**
     * Affiche sur la console les replicats
     *
     */

    public void printReplicat() throws RemoteException;

    /**
     * Méthode qui met à jour les replicats peer
     */
    public void update() throws RemoteException;

    /**
     * Méthode qui retourne la valeur d'une clé des replicats
     *
     */
    public boolean chercheValeurKeyReplicat(String recherche) throws RemoteException;

    /**
     * Méthode qui retourne le directory d'un Peeer
     *
     */
    public Map<String, String> getDirectory() throws RemoteException;

    /**
     * Méthode quidéfinit un Directory passé en
     *
     */
    public void setDirectory(Map<String, String> directoryReplicat) throws RemoteException;
}
