/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import fr.unice.platdujour.application.Main;
import fr.unice.platdujour.chord.Peer;
import fr.unice.platdujour.chord.Tracker;
import java.rmi.RemoteException;
public class Recherche {

    public static void testRecherche(Peer landmarkPeer) throws RemoteException{
        Peer nextPeer = landmarkPeer;
        String recherche = "Clovis";
        do {
                nextPeer = nextPeer.getSuccessor();
                 if(nextPeer.get(recherche) == null){ 
                        if(nextPeer.getReplicat(recherche) != null){
                             System.out.println("Plat du jour récupérer à l'aide des replicats: " + nextPeer.getReplicat(recherche));
                        }
                        else
                            System.out.println("Non trouvé dans les réplicats");
                 }
                 else{
                     System.out.println("Trouvé dans le peer: ");
                 }
               
          } while((!nextPeer.equals(landmarkPeer))); 
    }
}