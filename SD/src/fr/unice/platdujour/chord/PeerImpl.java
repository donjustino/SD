package fr.unice.platdujour.chord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	private Map<String, String> directory;

	/** Peer that is just before in the virtual ring */
	private Peer predecessor;

	/** Peer that is just after in the virtual ring */
	private Peer successor;
        
        /**
	 * Pair successeur de la pair successeur de ce peer
	 */
	private Peer successorofsuccessor;
        
        
        /**
	 * Création d'un objet tracker
	 */
        Tracker tracker;
        
        ScheduledExecutorService threadPool;

        private Map<String, String> directoryReplicat;
 
        
        Peer replicatsuccessor;
   
        public static int nbReplicat = 2;
        
        private String[][] tabReplicat = new String[nbReplicat][2]; 
	public PeerImpl(Identifier id) throws RemoteException {
		this.id = id;
		this.predecessor = this;
		this.successor = this;
		this.successorofsuccessor = this;
		this.directory = new HashMap<String, String>();
		this.directoryReplicat = new HashMap<String, String>();

		threadPool =
				Executors.newScheduledThreadPool(1);
		
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					// The stabilize method is called periodically to update 
					// the successor and predecessor links of the peer
						PeerImpl.this.stabilize();
						
				} catch (RemoteException e) {
					try{
					System.out.println("Succeseur mort, suppression... " + PeerImpl.this.tracker.delPeer(PeerImpl.this.successor));
						PeerImpl.this.successor = PeerImpl.this.successorofsuccessor;
						PeerImpl.this.successorofsuccessor = PeerImpl.this.successor.getSuccessor();
						PeerImpl.this.successor.setPredecessor(PeerImpl.this);
						PeerImpl.this.predecessor.setSuccessorofSuccessor(PeerImpl.this.successor);
						System.out.println(PeerImpl.this.describe());
                                                PeerImpl.this.tracker.RestaureData(PeerImpl.this.directoryReplicat);
						
					}catch (RemoteException et) {
						et.printStackTrace();
					} catch (IOException ex) {
                                        Logger.getLogger(PeerImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    }
				}
			}
		};
		threadPool.scheduleAtFixedRate(r, 0, 500, TimeUnit.MILLISECONDS);
	}
	@Override
	public synchronized void create() throws RemoteException {
		this.predecessor = null;
		// The bootstrap of the Chord network requires a self loop
		this.successor = this;
	}
         public Map<String,String> getDatas() throws RemoteException{
		return this.directory;
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
		//System.out.println("On peer "+ this.getId());
		Peer x = null;
		try{
			x = this.successor.getPredecessor();
		}catch(NoSuchObjectException e){
			System.err.println("Objet succeseur mort");
		}

		// If x is this itself, then this condition is not valid. This 
		// condition is valid if the successor has another peer as predecessor,
		// then in this case we check if this other peer is indeed included in 
		// the current identifier and the identifier of the successor. If it 
		// is, then it mean that x must be the new successor.
		if (x != null && 
				x.getId().isBetweenOpenOpen(this.id, this.successor.getId())) {
			this.successor = x;
		}
		this.successorofsuccessor = this.successor.getSuccessor();
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
		threadPool.shutdown();
                UnicastRemoteObject.unexportObject(this, true);

		System.out.println("Peer with id " + this.id + " has died.");
	}
        
        public void setSuccessorofSuccessor(Peer peer) throws RemoteException {
		this.successorofsuccessor=peer;
	}
        public Tracker getTracker() throws RemoteException {
		return tracker;
	}
                
	public void setTracker(Tracker tracker) throws RemoteException {
		this.tracker = tracker;
	}
        
	public String returnKey() throws RemoteException {
		StringBuilder s = new StringBuilder();

		int cpt = 0;
		int size = this.directory.size();
		for (Entry<String,String> entry : this.directory.entrySet()) {
			s.append(entry.getKey());
		}
                return s.toString();
	}
        public String returnValue() throws RemoteException {
		StringBuilder s = new StringBuilder();

		int cpt = 0;
		int size = this.directory.size();
		for (Entry<String,String> entry : this.directory.entrySet()) {
			s.append(entry.getValue());
		}
                return s.toString();
	}
        
        public void saveReplicat() throws RemoteException{
                Peer temp = this.successor;
		
		for(int i = 0 ; i < nbReplicat ; i++){
                        tabReplicat[i][0] =  temp.returnKey();
                        tabReplicat[i][1] =  temp.returnValue();
			temp = temp.getSuccessor();
		}
	
            /* for(int i = 0; i < tabReplicat.length; i++){
                    System.out.println("Key : " + tabReplicat[i][0] + " Value : " + tabReplicat[i][1]);
            } */

        }
       
        public void printReplicat() throws RemoteException{
            System.out.println(this.describe());
            for(int i = 0; i < tabReplicat.length; i++){
              System.out.println("Key : " + tabReplicat[i][0] + " Value : " + tabReplicat[i][1]);
           }
           System.out.println("");
          }

    @Override
        public void update() throws RemoteException{
            /*for(int i = 0 ; i < nbReplicat ; i++){
                
            }*/
            
            int i = 0;
            Peer nextPeer = this;
            do{
            
                
                 if(!tabReplicat[i][0].equals(nextPeer.getSuccessor().returnKey())){
                     //System.out.println("Test :" + tabReplicat[i][0] + this.successor.returnKey());
                     //System.out.println("Le sucesseur à change sa cléf" + this.successor.returnKey() + ", mise à jour... ");
                     tabReplicat[i][0] = nextPeer.getSuccessor().returnKey();
                     //System.out.println("Mise à jour OK, nouvelle key : " + tabReplicat[i][0]);   
                     System.out.println("Mise à jour des réplicats faites...");
                 }
                 if(!tabReplicat[i][1].equals(nextPeer.getSuccessor().returnValue())){
                      //System.out.println("Le sucesseur à change sa valeur" + this.returnValue() + ", mise à jour... ");
                      tabReplicat[i][1] =  nextPeer.getSuccessor().returnValue();
                       //System.out.println("Mise à jour OK, nouvelle valeur : " + tabReplicat[i][1]);   
                      System.out.println("Mise à jour des réplicats faites...");
                 }
                
                
                i = i + 1; 
                   
                nextPeer = nextPeer.getSuccessor();
            }while(i != nbReplicat);
            
            
            
            this.directoryReplicat = this.getSuccessor().getDirectory();
         }

    @Override
    public boolean chercheValeurKeyReplicat(String recherche) throws RemoteException  {
           for(int i = 0; i < tabReplicat.length; i++){
                    if( tabReplicat[i][0].equals(recherche)){
                         System.out.println("Trouvé,  Value : " + tabReplicat[i][1]);
                        return true;
                    }
            }
 
            return false;
        
    }
      public Map<String, String> getDirectory() throws RemoteException {
		return this.directory;
	}


    @Override
    public void setDirectory(Map<String, String> directoryReplicat) {
        this.directory = directoryReplicat;
    }

  }