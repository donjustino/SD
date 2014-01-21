package fr.unice.platdujour.chord;

import java.io.Serializable;

/**
 * Class that represents an identifier in the Chord ring.
 */
public class Identifier implements Comparable<Identifier>, Serializable {

	/** Default serialization ID */
    private static final long serialVersionUID = 1L;

    /** Number of bits of the identifier */
    protected static final int NB_BITS = 31;

    /** Maximum value sustained by an Identifier */
    protected static final int MAX_VALUE = (2 << (NB_BITS - 1)) - 1;

    /** The value of the identifier */
    private final int value;

    
    public Identifier(int value) {
        if (value < 0 || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid identifier value: "
                    + value);
        }

        this.value = value;
    }

    /**
     * Method to know if Identifier is included in an interval of identifiers 
     * (last excluded).
     * @param a Identifier start
     * @param b Identifier end
     * @return
     */
    public boolean isBetweenOpenClosed(Identifier a, Identifier b) {
        if (a.compareTo(b) < 0) {
            return this.compareTo(a) > 0 && this.compareTo(b) <= 0;
        } else {
            return this.compareTo(a) > 0 || this.compareTo(b) <= 0;
        }
    }

    /** 
     * Method to know if Identifier is included in an interval of identifiers 
     * (last included).
     * @param a Identifier start
     * @param b Identifier end
     * @return
     */
    public boolean isBetweenOpenOpen(Identifier a, Identifier b) {
        if (a.compareTo(b) < 0) {
            return this.compareTo(a) > 0 && this.compareTo(b) < 0;
        } else {
            return this.compareTo(a) > 0 || this.compareTo(b) < 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Identifier id) {
        return this.value - id.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Identifier
                && this.value == ((Identifier) obj).value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf(this.value).hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Long.toString(this.value);
    }

}
