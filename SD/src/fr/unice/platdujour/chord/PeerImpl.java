package fr.unice.platdujour.chord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the {@link Peer} API.
 */
public class PeerImpl extends UnicastRemoteObject implements Comparable<Peer>,
Peer {

	/** Default serialization ID */
	private static final long serialVersionUID = 1L;

	/** Identifier of the peer in the virtual ring */
	private final Identifier id;

	/** Local storage for entries that have an identifier that is managed by  
	 * the peer */
	private final Map<String, String> directory;

	/** Peer that is just before in the virtual ring */
	private Peer predecessor;

	/** Peer that is just after in the virtual ring */
	private Peer successor;
        
        private Peer successorofsuccessor;
        
        private boolean boolcheckdead;

        private Map<String, String> directoryreplicat;
        
        private Peer replicatsuccessor;
        
	public PeerImpl(Identifier id) throws RemoteException {
		this.id = id;
		this.predecessor = this;
		this.successor = this;
		this.directory = new HashMap<String, String>();

		ScheduledExecutorService threadPool =
				Executors.newScheduledThreadPool(1);
		threadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
                                        System.out.println("Je suis : " + PeerImpl.this.getId());
                                        
                                        if(PeerImpl.this.getboolcheckdead() == false )
                                        {
                                 
                                                //System.out.println("Peer sucesseur OK");
                                                
                                            
                                            
                                        }
                                         else{
                                            try {
                                                 PeerImpl.this.deserialization();
                                                 PeerImpl.this.successor = PeerImpl.this.replicatsuccessor.getSuccessor();
                                                 PeerImpl.this.successor.setPredecessor(PeerImpl.this.replicatsuccessor.getPredecessor());
                                                 PeerImpl.this.setboolcheckdead(false);
                                            } catch (IOException ex) {
                                                Logger.getLogger(PeerImpl.class.getName()).log(Level.SEVERE, null, ex);
                                            } catch (ClassNotFoundException ex) {
                                                Logger.getLogger(PeerImpl.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
					
					PeerImpl.this.stabilize();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, 0, 500, TimeUnit.MILLISECONDS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void create() throws RemoteException {
		this.predecessor = null;
		// The bootstrap of the Chord network requires a self loop
		this.successor = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void join(Peer landmarkPeer) throws RemoteException {
		this.predecessor = null;
		// To join the network, ask a peer that is already in the network to 
		// find which peer must be the successor of the joining peer, using 
		// the identifier of the joining peer
		this.successor = landmarkPeer.findSuccessor(this.id);
		// The stabilize method will then update all the other links correctly
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Peer findSuccessor(Identifier id) throws RemoteException {
		// There is only one peer in the network
		if (this.successor.equals(this)) {
			return this;
		}
		// The specified identifier is in between the current peer identifier 
		// and the successor identifier: the successor is then the peer we are 
		// looking for
		if (id.isBetweenOpenClosed(this.id, this.successor.getId())) {
			return this.successor;
		}
		// Nothing can be deduced from the specified identifier here: 
		// propagate the request in case the successor knows more about it
		else {
			return this.successor.findSuccessor(id);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identifier getId() throws RemoteException {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Peer getPredecessor() throws RemoteException {
		return this.predecessor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Peer getSuccessor() throws RemoteException {
		return this.successor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setPredecessor(Peer peer) throws RemoteException {
		this.predecessor = peer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setSuccessor(Peer peer) throws RemoteException {
		this.successor = peer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		try {
			// Two peers are equal if they have the same identifier
			return this.id.equals(((Peer) obj).getId());
		} catch (RemoteException e) {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		// The hashcode of a peer is the hashcode of its identifier
		return this.id.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Peer p) {
		try {
			return this.id.compareTo(p.getId());
		} catch (RemoteException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void stabilize() throws RemoteException {
		// x should be this itself, but it is not always the case, typically 
		// if the successor has recently taken a new peer as predecessor
		Peer x = this.successor.getPredecessor();

		// If x is this itself, then this condition is not valid. This 
		// condition is valid if the successor has another peer as predecessor,
		// then in this case we check if this other peer is indeed included in 
		// the current identifier and the identifier of the successor. If it 
		// is, then it mean that x must be the new successor.
		if (x != null && 
				x.getId().isBetweenOpenOpen(this.id, this.successor.getId())) {
			this.successor = x;
		}

		// The current peer needs to inform its successor that it is indeed its
		// successor 
		this.successor.notify(PeerImpl.this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void notify(Peer peer) throws RemoteException {
		// If a new peer notify itself as a predecessor of the current peer, 
		// check if it fits in the interval of the previous predecessor 
		// identifier and it own identifier. If yes, take it as predecessor.
		// Otherwise, nothing needs to be done.
		if (this.predecessor == null
				|| peer.getId().isBetweenOpenOpen(
						this.predecessor.getId(), this.id)) {
			this.predecessor = peer;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void put(String restaurant, String dailySpecial)
			throws RemoteException {
		this.directory.put(restaurant, dailySpecial);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String get(String restaurant) throws RemoteException {
		return this.directory.get(restaurant);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String describe() throws RemoteException {
		StringBuilder s = new StringBuilder("Peer [id=" + this.id + 
				", successor=" + this.successor.getId() + ", predecessor="
				+ this.predecessor.getId() + ", values=[");

		int cpt = 0;
		int size = this.directory.size();
		for (Entry<String,String> entry : this.directory.entrySet()) {
			s.append("(" + entry.getKey() + ";" + entry.getValue() + ")");
			if (++cpt != size) {
				s.append(", ");
			}
		}

		s.append("]]");
		return s.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void die() throws RemoteException {
		// Removes this from the RMI runtime. It will prevent all RMI calls 
		// from executing on this object. A further RMI call on this will 
		// cause a java.rmi.NoSuchObjectException.
		this.predecessor.setboolcheckdead(true);
                UnicastRemoteObject.unexportObject(this, true);
              

		System.out.println("Peer with id " + this.id + " has died.");
	}
        public void setSuccesorofSuccessor(Peer p) throws RemoteException {
            this.successorofsuccessor = p;
       
    }

    @Override
    public void setboolcheckdead(boolean b) throws RemoteException {
        this.boolcheckdead = b;
    }

    private boolean getboolcheckdead() {
        return this.boolcheckdead;
    }
    public void serialization(Peer temp) throws RemoteException, FileNotFoundException, IOException {
        FileOutputStream fichier = new FileOutputStream("peer"+this.getId()+".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fichier);
        oos.writeObject(temp);
        oos.flush();
        oos.close();
    }
     public void deserialization() throws RemoteException, FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fichier = new FileInputStream("peer"+this.getId()+".ser");
        ObjectInputStream ois = new ObjectInputStream(fichier);
        this.replicatsuccessor = (Peer) ois.readObject();
        //this.successor = this.replicatsuccessor.getSuccessor();
        //System.out.println("Ce peer" + this.describe());
        //System.out.println("Le replicat :" + this.replicatsuccessor.describe());
        //this.successor.setPredecessor(this.replicatsuccessor.getPredecessor());
        ois.close();
        fichier.close();
        
    }
     

}
