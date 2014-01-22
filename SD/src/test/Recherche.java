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
    /**
     * Méthode qui démontre l'accélérateur de recherche via les replicats 
     *
     */
    public static void testRecherche(Peer landmarkPeer) throws RemoteException{
        Peer nextPeer = landmarkPeer;
        boolean verif = false;
        System.out.println("Recherche pour le restaurant Clovis");
        String recherche = "Clovis";
        
        do {
                        nextPeer = nextPeer.getSuccessor();
                        if(nextPeer.chercheValeurKeyReplicat(recherche) == true){
                             verif = true;
                        }
                        
               
               
         } while((!nextPeer.equals(landmarkPeer)) || (verif != true)); 
    }
}