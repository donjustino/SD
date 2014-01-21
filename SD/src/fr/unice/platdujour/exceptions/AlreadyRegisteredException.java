package fr.unice.platdujour.exceptions;

import fr.unice.platdujour.chord.Identifier;

/**
 * This exception represents an abnormal case in which a peer that has already 
 * been registered to the tracker tries to register again.
 */
public class AlreadyRegisteredException extends Exception {

	/** Default serialization ID */
    private static final long serialVersionUID = 1L;

    public AlreadyRegisteredException(Identifier id) {
        super("Peer with ID " + id + " is already registered");
    }

}
