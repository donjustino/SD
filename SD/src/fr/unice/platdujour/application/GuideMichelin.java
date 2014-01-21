package fr.unice.platdujour.application;

import java.rmi.RemoteException;

/**
 * This interface is the API of the GuideMichelin. We can put new entries in 
 * the guide via the put method and we can retrieve daily specials from 
 * restaurants via the get method.
 */
public interface GuideMichelin {

	/**
	 * Stores a new entry (or an updated entry) in the GuideMichelin.
	 * @param restaurant Name of the restaurant to list in the GuideMichelin
	 * @param dailySpecial Name of the daily special that offers the dish 
	 * @throws RemoteException
	 */
    void put(String restaurant, String dailySpecial) throws RemoteException;

    /**
     * Searches for the daily special of a given restaurant.
     * @param restaurant The name of the restaurant
     * @return The name of the daily special offered by the restaurant, or null 
     * if there is no such restaurant
     * @throws RemoteException
     */
    String get(String restaurant) throws RemoteException;

}
