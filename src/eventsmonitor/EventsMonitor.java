package eventsmonitor;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import utils.ApplicationProperties;
import utils.DBServer;
import utils.DBServerException;

/**
 * Programme Java permettant de surveiller l'activité d'une base de données
 * MongoDb
 *
 * @author Thierry Baribaud
 * @version 0.05
 */
public class EventsMonitor {
    
    /**
     * Nombre maximum de status à afficher
     */
    public final static int MAX_STATUS = 5;

    /**
     * Nombre maximum d'événements à afficher
     */
    public final static int MAX_EVENTS = 30;

    /**
     * dbServerType : prod pour le serveur de production, pre-prod pour le
     * serveur de pré-production. Valeur par défaut : pre-prod.
     */
    private String dbServerType = "pre-prod";

    /**
     * debugMode : fonctionnement du programme en mode debug (true/false).
     * Valeur par défaut : false.
     */
    private static boolean debugMode = false;

    /**
     * testMode : fonctionnement du programme en mode test (true/false). Valeur
     * par défaut : false.
     */
    private static boolean testMode = false;

    /**
     * Constructeur de la classe EventsMonitor
     *
     * @param args paramètres de la ligne de commande
     * @throws eventsmonitor.GetArgsException en cas d'erreur avec les
     * paramètres en ligne de commande
     * @throws java.io.IOException en cas d'erreur d'entrée/sortie.
     * @throws utils.DBServerException en cas d'erreur avec le serveur de base
     * de données.
     */
    public EventsMonitor(String[] args) throws GetArgsException, IOException, DBServerException {
        ApplicationProperties applicationProperties;
        DBServer dbServer;
        GetArgs getArgs;
        MongoClient mongoClient;
        MongoDatabase mongoDatabase;
        Thread faireDesMesures;
        TableauDeSuivi tableauDeSuivi;
        Controleur controleur;

        System.out.println("Création d'une instance de EventsMonitor ...");

        System.out.println("Analyse des arguments de la ligne de commande ...");
        getArgs = new GetArgs(args);
        setDbServerType(getArgs.getDbServerType());
        setDebugMode(getArgs.getDebugMode());
        setTestMode(getArgs.getTestMode());

        System.out.println("Lecture des paramètres d'exécution ...");
        applicationProperties = new ApplicationProperties("EventsMonitor.prop");

        System.out.println("Lecture des paramètres du serveur de base de données ...");
        dbServer = new DBServer(getDbServerType(), applicationProperties);
        if (debugMode) {
            System.out.println(dbServer);
        }
        System.out.println("Ouverture de la connexion au serveur de base de données : " + dbServer.getName());
        mongoClient = new MongoClient(dbServer.getIpAddress(), (int) dbServer.getPortNumber());

        System.out.println("Connexion à la base de données : " + dbServer.getDbName());
        mongoDatabase = mongoClient.getDatabase(dbServer.getDbName());

        tableauDeSuivi = new TableauDeSuivi(debugMode);
        controleur = new Controleur(mongoDatabase, debugMode);
        controleur.addPropertyChangeListener(tableauDeSuivi);
        faireDesMesures = new Thread(controleur);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                tableauDeSuivi.setVisible(true);
            }
        });
        faireDesMesures.start();
    }

    /**
     * @param dbServerType définit le serveur de base de données
     */
    private void setDbServerType(String dbServerType) {
        this.dbServerType = dbServerType;
    }

    /**
     * @return dbServerType le serveur de base de données
     */
    private String getDbServerType() {
        return (dbServerType);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventsMonitor eventsMonitor;

        System.out.println("Lancement de EventsMonitor ...");

        try {
            eventsMonitor = new EventsMonitor(args);
            if (debugMode) {
                System.out.println(eventsMonitor);
            }
        } catch (Exception exception) {
            System.out.println("Problème lors de l'instanciation de EventsMonitor");
            exception.printStackTrace();
        }

        System.out.println("Fin de EventsMonitor");
    }

    /**
     * @param debugMode : fonctionnement du programme en mode debug
     * (true/false).
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * @param testMode : fonctionnement du programme en mode test (true/false).
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * @return debugMode : retourne le mode de fonctionnement debug.
     */
    public boolean getDebugMode() {
        return (debugMode);
    }

    /**
     * @return testMode : retourne le mode de fonctionnement test.
     */
    public boolean getTestMode() {
        return (testMode);
    }

    /**
     * Retourne le contenu de EventsMonitor
     *
     * @return retourne le contenu de Pi2aClient
     */
    @Override
    public String toString() {
        return "EventsMonitor:{"
                + "dbServer=" + getDbServerType()
                + "}";
    }

}
