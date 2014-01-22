/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import fr.unice.platdujour.chord.Identifier;
import fr.unice.platdujour.chord.Peer;
import java.rmi.RemoteException;

/**
 *
 * @author Justin
 */
public class TestCoherance {

    /**
     * Classe qui modifie les valeurs de tout les peers, et vérifie si la
     * récupération des nouvelles données marchent
     *
     */
    public static void test(Peer landmarkPeer) throws RemoteException {
        System.out.println("Test de cohérence :");
        Peer nextPeer = landmarkPeer;
        int i = 0;
        int j = 0;
        do {
            i = i + 1;
            j = j + 1;
            nextPeer = nextPeer.getSuccessor();
            nextPeer.put(Integer.toString(i), Integer.toString(j));

        } while (!nextPeer.equals(landmarkPeer));
        System.out.println("Mise à jour des liens :");
        nextPeer = landmarkPeer;

        do {

            nextPeer = nextPeer.getSuccessor();
            nextPeer.update();

        } while (!nextPeer.equals(landmarkPeer));

        System.out.println("Verification :");
        nextPeer = landmarkPeer;

        do {

            nextPeer = nextPeer.getSuccessor();
            System.out.println("\n");
            System.out.println("Pour l'ID : " + nextPeer.getId());
            nextPeer.printReplicat();
            System.out.println("\n");
        } while (!nextPeer.equals(landmarkPeer));

    }
}
