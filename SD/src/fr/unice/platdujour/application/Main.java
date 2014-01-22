package fr.unice.platdujour.application;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import fr.unice.platdujour.chord.Identifier;
import fr.unice.platdujour.chord.Peer;
import fr.unice.platdujour.chord.PeerImpl;
import fr.unice.platdujour.chord.Tracker;
import fr.unice.platdujour.chord.TrackerImpl;
import fr.unice.platdujour.exceptions.AlreadyRegisteredException;
import test.Mort;
import test.Recherche;

/**
 * This class defines a main in which
 * 1) a tracker is created
 * 2) a Chord network is initialized
 * 3) all the peers in the network are listed
 * 4) a {@link GuideMichelin} is created. It will use the Chord network
 * 5) some data are added to the {@link GuideMichelin}
 * 6) the peers are listed again with the data they store
 * 7) some data are requested from the {@link GuideMichelin}
 */
public class Main {
        static GuideMichelin guideMichelin;
        /** Number of peers that will be injected in the network */
        private static final int NB_PEERS = 10; 
        
        /** Port number of RMI registry */
        private static final int RMI_REGISTRY_PORT = 1099;

        /**
         * @param args Not used
         * @throws Exception
         */
        static Tracker tracker;

        public static Tracker getTracker() {
                return tracker;
        }
        public static void main(String[] args) throws Exception {

                // A tracker is created
                new TrackerImpl(RMI_REGISTRY_PORT);

                Tracker tracker =
                                (Tracker) Naming.lookup("rmi://localhost:" + RMI_REGISTRY_PORT
                                                + "/tracker");
                
                // A Chord network is initialized
                createNetwork(tracker);

                // All the peers in the network are listed
                Thread.sleep((long) (Math.log(NB_PEERS)*1000));
                System.out.println("\nTurn around after first stabilization");
                turnAround(tracker.getRandomPeer());

                Thread.sleep((long) (Math.log(NB_PEERS)*1000));
                System.out.println("\nTurn around after second stabilization");
                turnAround(tracker.getRandomPeer());

                // A GuideMichelin is created. It will use the Chord network
                guideMichelin = new GuideMichelinImpl(tracker);

                // Some data are added to the {@link GuideMichelin}
                DataGenerator dataGenerator = new DataGenerator(10);
                Map<String, String> newData;

                for (int i = 0 ; i < 10 ; i++) {
                        newData = dataGenerator.getNewData();      
                        for (Entry<String, String> entry : newData.entrySet()) {
                                guideMichelin.put(entry.getKey(), entry.getValue());
                        }
                }

                // The peers are listed again with the data they store
                Thread.sleep(2000);
                System.out.println("\nTurn around after adding data");
                turnAround(tracker.getRandomPeer());
                
                // Some data are requested from the GuideMichelin
                String[] restaurants = {"Le Bistrot Gourmand", "Auberge de la Madone", "toto"};

                for (String restaurant : restaurants) {
                        System.out.println("\nRestaurant '" + restaurant + "' - Daily special: '"
                                        + guideMichelin.get(restaurant) + "'");
                }
                Peer nextPeer = tracker.getRandomPeer();
                //sauvegarde replicat 
                do {
                        nextPeer = nextPeer.getSuccessor();
                        nextPeer.saveReplicat();
         
                } while (!nextPeer.equals(tracker.getRandomPeer()));
                setReplicat(tracker.getRandomPeer());
                update(tracker.getRandomPeer());
                update(tracker.getRandomPeer());
                cherche(tracker.getRandomPeer());
                mort(tracker.getRandomPeer());
        }

        /**
         * Creates a network composed of NB_PEERS peers.
         * @param tracker The tracker that is going to keep track of the peers
         * @throws RemoteException
         * @throws AlreadyRegisteredException If a peer tries to register more 
         * than once
         */
        private static void createNetwork(Tracker tracker) 
                        throws RemoteException, AlreadyRegisteredException {
                for (int i = 0 ; i < NB_PEERS ; i++) {
                        Peer p = new PeerImpl(new Identifier(i * 100));

                        if (i == 0) {
                                System.out.println("Ring created by " + p.getId());
                                p.create();
                                p.setTracker(tracker);
                        } 
                        else {
                                // The new peer is inserted in the network using a random peer 
                                // that already belongs to the network. This random peer is 
                                // retrieved thanks to the tracker.
                                Peer randomPeer = tracker.getRandomPeer();
                                System.out.println("Added " + p.getId() + " from "
                                                + randomPeer.getId() + " that points to "
                                                + randomPeer.getSuccessor().getId());
                                
                                p.setTracker(tracker);
                                p.join(randomPeer);
                        }

                        tracker.register(p);
                }
        }

        /**
         * This method run through the entire Chord network and print each 
         * encountered peer.
         * @param landmarkPeer The peer from which the turn starts
         * @throws RemoteException
         */
        private static void turnAround(Peer landmarkPeer) throws RemoteException {
                System.out.println(
                                "\nStarted turn around from " + landmarkPeer.getId());
                Peer nextPeer = landmarkPeer;

                do {
                        nextPeer = nextPeer.getSuccessor();
                        System.out.println("Visited " + nextPeer.describe());

                } while (!nextPeer.equals(landmarkPeer));
        }
        private static void mort(Peer landmarkPeer) throws RemoteException, AlreadyRegisteredException{
                Peer nextPeer = landmarkPeer;
                Mort.test(nextPeer);
            
        }
        private static void cherche(Peer landmarkPeer) throws RemoteException {
                Peer nextPeer = landmarkPeer;
                Recherche.testRecherche(nextPeer);

        }
          private static void setReplicat(Peer landmarkPeer) throws RemoteException {
             Peer nextPeer = landmarkPeer;
             do {
                        nextPeer = nextPeer.getSuccessor();
                        nextPeer.saveReplicat();
                        
               } while ((!nextPeer.equals(landmarkPeer)));

        }
    public static GuideMichelin getGuideMichelin() {
                return guideMichelin;
        }

    private static void update(Peer landmarkPeer) throws RemoteException {
                     Peer nextPeer = landmarkPeer;
             do {
                        nextPeer = nextPeer.getSuccessor();
                        nextPeer.update();
                        
               } while ((!nextPeer.equals(landmarkPeer)));
        
    }
}