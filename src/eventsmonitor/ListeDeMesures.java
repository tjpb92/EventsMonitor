package eventsmonitor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.Serializable;
import org.bson.Document;
import org.joda.time.DateTime;

/**
 * Classe décrivant une liste de mesures faites à un instant t.
 *
 * @author Thierry Baribaud
 * @version 0.02
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
     * Constructeur principal
     * @param mongoDatabase connexion à la base de données
     */
    public ListeDeMesures(MongoDatabase mongoDatabase) {
        MongoCollection<Document> collection;
        
        collection = mongoDatabase.getCollection("events");
        setNombreDEvenements(collection.count());
        setDateDeLaMesure(new DateTime());
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
     * Retourne l'objet sous forme textuelle
     *
     * @return retourne l'objet sous forme textuelle
     */
    @Override
    public String toString() {
        return "ListeDeMesures:{"
                + "nombreDEvenements:" + getNombreDEvenements()
                + ", dateDeLaMesure:" + getDateDeLaMesure()
                + "}";
    }

}
