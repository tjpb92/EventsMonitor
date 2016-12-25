package eventsmonitor;

import bkgpi2a.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.Serializable;
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
 * @version 0.03
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
        Event event;
        DateTimeFormatter format = ISODateTimeFormat.dateTimeParser();
        
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
                + "}";
    }

}
