package fr.unice.platdujour.chord;

import static fr.unice.platdujour.chord.PeerImpl.nbReplicat;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.unice.platdujour.exceptions.AlreadyRegisteredException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the {@link Tracker} interface. This implementation 
 * use a list of peers to keep track of the peers that belong to the network.
 */
public class TrackerImpl extends UnicastRemoteObject implements Tracker {

	/** Default serialization ID */
    private static final long serialVersionUID = 1L;

    /** List of peers that belong to the network */
    private final List<Peer> peers;

    /** Used for random picking in the peer list*/
    private final Random randomGenerator;

    private List successeurMort = new LinkedList();
    public TrackerImpl(int port) throws RemoteException, MalformedURLException,
            AlreadyBoundException {
       
        this.peers = new ArrayList<Peer>();
        this.randomGenerator = new Random();
        // The tracker is a remotely accessible object: bind it to an RMI 
        // registry so that we can retrieve it at a well known address
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind("tracker", this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void register(Peer peer)
            throws AlreadyRegisteredException, RemoteException {
        if (this.peers.contains(peer)) {
            throw new AlreadyRegisteredException(peer.getId());
        }

        this.peers.add(peer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Peer getRandomPeer() throws RemoteException {
        if (this.peers.isEmpty()) {
            return null;
        }
        
        return this.peers.get(this.randomGenerator.nextInt(this.peers.size()));
    }
    @Override
    public synchronized boolean delPeer(Peer peer) throws RemoteException {
		return this.peers.remove(peer);
		
    }
    public void RestaureData(Map<String, String> directoryReplicat)  throws RemoteException{
           
              Peer landmarkPeer = this.getRandomPeer();
              Peer nextPeer = landmarkPeer;

		do {
                     System.out.println(directoryReplicat.toString());
                     if(nextPeer.getDirectory().equals("{}")){
                         nextPeer.setDirectory(directoryReplicat);
                         System.out.println(nextPeer.getDirectory());
                     }
                     
                        

		nextPeer = nextPeer.getSuccessor();
		} while (!nextPeer.equals(landmarkPeer));
            
            
    }

}
