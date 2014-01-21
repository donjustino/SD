package fr.unice.platdujour.chord;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class used to create a Chord Identifier from a regular String.
 */
public class Key extends Identifier {

	/** Default serialization ID */
    private static final long serialVersionUID = 1L;

    
    public Key(String value) {
        this(value.getBytes());
    }

    private Key(byte[] bytes) {
        super(toIntegerRestrictedToInterval(bytes, NB_BITS) * 100);
    }

    /**
     * Produces an hash value from some bytes and makes it fit into the given 
     * range.
     * @param bytes Bytes to hash
     * @param intervalSize Interval in which the hash should fit
     * @return
     */
    private static int toIntegerRestrictedToInterval(byte[] bytes,
                                                     int intervalSize) {
        return (new BigInteger(computeSHA1(bytes)).abs().intValue()
                % intervalSize + intervalSize)
                % intervalSize;
    }

    /** 
     * Creates a SHA-1 hash value of some bytes.
     * @param bytes
     * @return
     */
    private static byte[] computeSHA1(byte[] bytes) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md.digest(bytes);
    }

}
