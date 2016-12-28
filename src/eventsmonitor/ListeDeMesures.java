package eventsmonitor;

import bkgpi2a.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static eventsmonitor.EventsMonitor.MAX_EVENTS;
import static eventsmonitor.EventsMonitor.MAX_STATUS;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
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
 * @version 0.05
 */
public class ListeDeMesures implements Serializable {

    /**
     * Pour formater les nombres
     */
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,##0");
    
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
    private String tableauStatus[][];

    /**
     * Tableau des événements
     */
    private String tableauEvenements[][];

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
        boolean boucle;
        Document doc;

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

        // Requête à construire : db.events.aggregate({$group:{"_id":"$status", count: {$sum:1}}},{$sort:{count:-1}})
        AggregateIterable<Document> outputStatus = collection.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", "$status").append("count", new Document("$sum", 1))),
                new Document("$sort", new Document("count", -1))
        ));

        tableauStatus = new String[MAX_STATUS][2];
        i = 0;
        boucle = true;
        cursor = outputStatus.iterator();
        while (cursor.hasNext() && boucle) {
            doc = cursor.next();
//            System.out.println(doc.get("_id").getClass() + ", " + doc.get("count").getClass());
            tableauStatus[i][0] = doc.get("_id").toString();
//            tableauStatus[i][1] = doc.get("count").toString();
            tableauStatus[i][1] = decimalFormat.format(doc.get("count"));
            i++;
            boucle = (i < MAX_STATUS);
        }
        for (j = i; j < MAX_STATUS; j++) {
            tableauStatus[j][0] = "";
            tableauStatus[j][1] = "";
        }
        if (this.debugMode) System.out.println(Arrays.deepToString(tableauStatus));

        // Requête à construire : db.events.aggregate({$group:{"_id":"$eventType", count: {$sum:1}}},{$sort:{count:-1}})
        AggregateIterable<Document> outputEvenements = collection.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", "$eventType").append("count", new Document("$sum", 1))),
                new Document("$sort", new Document("count", -1))
        ));

        tableauEvenements = new String[MAX_EVENTS][2];
        i = 0;
        boucle = true;
        cursor = outputEvenements.iterator();
        while (cursor.hasNext() && boucle) {
            doc = cursor.next();
//            System.out.println(doc.get("_id").getClass() + ", " + doc.get("count").getClass());
            tableauEvenements[i][0] = doc.get("_id").toString();
//            tableauEvenements[i][1] = doc.get("count").toString();
            tableauEvenements[i][1] = decimalFormat.format(doc.get("count"));
            i++;
            boucle = (i < MAX_EVENTS);
        }
        for (j = i; j < MAX_EVENTS; j++) {
            tableauEvenements[j][0] = "";
            tableauEvenements[j][1] = "";
        }
        if (this.debugMode) System.out.println(Arrays.deepToString(tableauEvenements));

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
     * @return un élement du tableau des status
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
     * @return le tableau des événements
     */
    public String[][] getTableauEvenements() {
        return tableauEvenements;
    }

    /**
     * @param tableauEvenements définit le tableau des événements
     */
    public void setTableauEvenements(String[][] tableauEvenements) {
        this.tableauEvenements = tableauEvenements;
    }

    /**
     * @param i indice de rangée
     * @param j indice de colonne
     * @return un élément du tableau des événéments
     */
    public String getTableauEvenements(int i, int j) {
        return tableauEvenements[i][j];
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
                + ", tableauStatus:" + Arrays.toString(getTableauStatus())
                + ", tableauEvenements:" + Arrays.toString(getTableauEvenements())
                + "}";
    }

}
