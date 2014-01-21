package fr.unice.platdujour.application;

import java.rmi.RemoteException;

import fr.unice.platdujour.chord.Key;
import fr.unice.platdujour.chord.Peer;
import fr.unice.platdujour.chord.Tracker;

/**
 * This implementation of the {@link GuideMichelin} stores its entries in a 
 * distributed manner. It uses the Chord peer-to-peer network to distribute 
 * storage and to retrieve content.  
 */
public class GuideMichelinImpl implements GuideMichelin {
	
	/** Tracker used to locate the peers that store the entries */
    private final Tracker tracker;
     

    public GuideMichelinImpl(Tracker tracker) {
        this.tracker = tracker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String restaurant, String dailySpecial) 
    		throws RemoteException {
    	// Storing an entry in our peer-to-peer network simply consist in 
    	// asking the peer that is responsible for the restaurant key to store 
    	// the couple (restaurant, daily special)
        this.findIndexer(restaurant).put(restaurant, dailySpecial);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String restaurant) throws RemoteException {
    	// Getting a content in our peer-to-peer network simply consist in 
    	// asking the peer that is responsible for the restaurant key to return 
    	// its associated value
        return this.findIndexer(restaurant).get(restaurant);
    }

    /**
     * Locates the peer that must store a content whose key is the restaurant 
     * name.
     * @param restaurant
     * @return The peer that is responsible for this key
     * @throws RemoteException
     */
    private final Peer findIndexer(String restaurant) throws RemoteException {
        return this.tracker.getRandomPeer().findSuccessor(new Key(restaurant));
    }

}
