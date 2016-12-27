package eventsmonitor;

import bkgpi2a.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Classe décrivant une liste de mesures faites à un instant t.
 *
 * @author Thierry Baribaud
 * @version 0.04
 */
public class ListeDeMesures implements Serializable {

    /**
     * Nombre d'événements en base à l'instant t
     */
    private long nombreDEvenements;

    /**
     * Date de la mesure
     */
    DateTime dateDeLaMesure;

    /**
     * Date de réception du dernier événement
     */
    private DateTime dateDernierEvenement;

    /**
     * Tableau des status
     */
    private String tableauStatus [][];
    
    /**
     * debugMode : fonctionnement du programme en mode debug (true/false).
     * Valeur par défaut : false.
     */
    private static boolean debugMode = false;

    /**
     * Constructeur principal
     *
     * @param mongoDatabase connexion à la base de données
     */
    public ListeDeMesures(MongoDatabase mongoDatabase, boolean debugMode) {
        MongoCollection<Document> collection;
        ObjectMapper objectMapper;
        MongoCursor<Document> cursor;
        BasicDBObject orderBy;
        BasicDBObject groupBy;
        Event event;
        DateTimeFormatter format = ISODateTimeFormat.dateTimeParser();
        int i;
        int j;
        
        collection = mongoDatabase.getCollection("events");
        setNombreDEvenements(collection.count());
        setDateDeLaMesure(new DateTime());
        
        this.debugMode = debugMode;
        objectMapper = new ObjectMapper();
        
        orderBy = new BasicDBObject("sentDate", -1);
        cursor = collection.find().sort(orderBy).iterator();
        if (cursor.hasNext()) {
            try {
                event = objectMapper.readValue(cursor.next().toJson(), Event.class);
                setDateDernierEvenement(format.parseDateTime(event.getSentDate()));
            } catch (IOException ex) {
                Logger.getLogger(ListeDeMesures.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        tableauStatus = new String[5][2];
//        tableauStatus = new long[][] {{-3, -2,-1, 0, 1}, {100, 200, 300, 400, 500}};
//        tableauStatus = new long[][] {{-3, 100},{-2, 200},{-1, 300},{0, 400},{1, 500}};

        // Requête à construire : db.events.aggregate({$group:{"_id":"$status", count: {$sum:1}}},{$sort:{count:-1}})
//        groupBy = new BasicDBObject("$group", new BasicDBObject("_id","$status").append("count",new BasicDBObject("$sum",1)));
//        orderBy = new BasicDBObject("$sort", new BasicDBObject("count", -1));
//        cursor = collection.aggregate(groupBy).iterator();
        // C'est pas gagné ...
        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", "$status").append("count", new Document("$sum", 1))),
                new Document("$sort", new Document("count", -1))
        ));

        i = 0;
        for (Document doc : output) {
//            System.out.println(doc.get("_id").getClass() + ", " + doc.get("count").getClass());
            tableauStatus[i][0] = doc.get("_id").toString();
            tableauStatus[i][1] = doc.get("count").toString();
            i++;
        }
        for (j=i; j<5; j++) {
            tableauStatus[j][0] = "";
            tableauStatus[j][1] = "";
        }

//        for (i=0;i<5;i++)
//            for (j=0;j<2;j++) 
//                System.out.println("i="+i+", j="+j+", tab="+tableauStatus[i][j]);
//        
    }

    /**
     * @return le nombre d'événements en base à l'instant t
     */
    public synchronized long getNombreDEvenements() {
        return nombreDEvenements;
    }

    /**
     * @param nombreDEvenements définit le nombre d'événements en base à
     * l'instant t
     */
    public synchronized void setNombreDEvenements(long nombreDEvenements) {
        this.nombreDEvenements = nombreDEvenements;
    }

    /**
     * @return le nombre d'événements en base à l'instant t
     */
    public synchronized DateTime getDateDeLaMesure() {
        return dateDeLaMesure;
    }

    /**
     * @param dateDeLaMesure définit le nombre d'événements en base à l'instant
     * t
     */
    public synchronized void setDateDeLaMesure(DateTime dateDeLaMesure) {
        this.dateDeLaMesure = dateDeLaMesure;
    }

    /**
     * @return la date de réception du dernier événement
     */
    public DateTime getDateDernierEvenement() {
        return dateDernierEvenement;
    }

    /**
     * @param dateDernierEvenement définit la date de réception du dernier
     * événement
     */
    public void setDateDernierEvenement(DateTime dateDernierEvenement) {
        this.dateDernierEvenement = dateDernierEvenement;
    }

    /**
     * @return le tableau des status
     */
    public String[][] getTableauStatus() {
        return tableauStatus;
    }

    /**
     * @param i indice de rangée
     * @param j indice de colonne
     * @return le tableau des status
     */
    public String getTableauStatus(int i, int j) {
        return tableauStatus[i][j];
    }

    /**
     * @param tableauStatus définit le tableau des status
     */
    public void setTableauStatus(String[][] tableauStatus) {
        this.tableauStatus = tableauStatus;
    }

    /**
     * Retourne l'objet sous forme textuelle
     *
     * @return retourne l'objet sous forme textuelle
     */
    @Override
    public String toString() {
        return "ListeDeMesures:{"
                + "nombreDEvenements:" + getNombreDEvenements()
                + ", dateDeLaMesure:" + getDateDeLaMesure()
                + ", dateDernierEvenement:" + getDateDernierEvenement()
                + ", tableauStatus:" + getTableauStatus()
                + "}";
    }

}
