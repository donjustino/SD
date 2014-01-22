/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import fr.unice.platdujour.application.Main;
import fr.unice.platdujour.chord.Peer;
import static fr.unice.platdujour.chord.PeerImpl.tabReplicat;
import fr.unice.platdujour.chord.Tracker;
import java.rmi.RemoteException;
public class Recherche {

    public static void testRecherche(Peer landmarkPeer) throws RemoteException{
        Peer nextPeer = landmarkPeer;
        String recherche = "Clovis";
        do {
                        nextPeer = nextPeer.getSuccessor();
                        nextPeer.chercheValeurKeyReplicat(recherche);
               
               
          } while((!nextPeer.equals(landmarkPeer))); 
    }
}