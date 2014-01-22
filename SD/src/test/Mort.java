/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import fr.unice.platdujour.application.Main;
import fr.unice.platdujour.chord.Identifier;
import fr.unice.platdujour.chord.Peer;
import fr.unice.platdujour.chord.Tracker;
import java.rmi.RemoteException;


public class Mort {
        
        public static void test(Peer landmarkPeer) throws RemoteException{
            Peer nextPeer = landmarkPeer;
            boolean verif = false;
            Identifier id = new Identifier(600);
            do {
                   nextPeer = nextPeer.getSuccessor();
                   if(nextPeer.getId().equals(id)){
                        System.out.println(nextPeer.getId());
                        nextPeer.die();
                        System.out.println("Mort");
                        verif = true;
                    }               
            } while ((verif != true));
         }
	
    
}
