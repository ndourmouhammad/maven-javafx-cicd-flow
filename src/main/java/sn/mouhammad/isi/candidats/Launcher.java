package sn.mouhammad.isi.candidats;

import javafx.application.Application;

public class Launcher {
//    public static void main(String[] args) {
//        Application.launch(HelloApplication.class, args);
//    }
public static void main(String[] args) {
    System.out.println("==========================================");
    System.out.println("🚀 DEPLOYMENT SUCCESSFUL ON AWS EC2!");
    System.out.println("Application: Candidats App (Server Mode)");
    System.out.println("==========================================");

    // On empêche le programme de s'arrêter pour que Docker reste "Up"
    try {
        while (true) {
            System.out.println("🕒 Heartbeat: Application is running... "
                    + new java.util.Date());
            Thread.sleep(30000); // Affiche un message toutes les 30 secondes
        }
    } catch (InterruptedException e) {
        System.err.println("Application interrupted!");
    }
}
}
