package eventsmonitor;

import com.mongodb.client.MongoDatabase;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe décrivant un controleur
 *
 * @author Thierry Baribaud
 * @version 0.02
 */
public class Controleur implements Serializable, Runnable {

    /**
     * Suivi des changements sur le controleur
     */
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    /**
     * Connexion à la base de données
     */
    private MongoDatabase mongoDatabase;

    /**
     * Nom du controleur
     */
    private String name = "Controleur";

    /**
     * Nombre de mesures faites par le controleur
     */
    private long nombreDeMesures = 0;

    /**
     * Liste de mesures
     */
    private ListeDeMesures listeDeMesures = null;

    /**
     * Constructeur principal
     *
     * @param mongoDatabase connexion à la base de données
     */
    public Controleur(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    /**
     * @return le nom du controleur
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * @param name définit le nom du controleur
     */
    public synchronized void setName(String name) {
        String oldName = this.name;

        this.name = name;
        changeSupport.firePropertyChange("name", oldName, name);
    }

    /**
     * @return le nombre de mesures faites par le controleur
     */
    public synchronized long getNombreDeMesures() {
        return nombreDeMesures;
    }

    /**
     * @param nombreDeMesures le nombre de mesures faites par le controleur
     */
    public synchronized void setNombreDeMesures(long nombreDeMesures) {
//        long oldNombreDeMesures = this.nombreDeMesures;

        this.nombreDeMesures = nombreDeMesures;
//        changeSupport.firePropertyChange("nombreDeMesures", oldNombreDeMesures, nombreDeMesures);
    }

    /**
     * Méthode pour ajouter un écouteur pour tout changement de valeur
     *
     * @param listener écouteur à ajouter
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {

        changeSupport.addPropertyChangeListener(listener);

    }

    /**
     * Méthode pour retirer un écouteur pour tout changement de valeur
     *
     * @param listener écouteur à retirer
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {

        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Retourne l'objet sous forme textuelle
     *
     * @return retourne l'objet sous forme textuelle
     */
    @Override
    public String toString() {
        return "Controleur:{"
                + "name:" + getName()
                + ", nombreDeMesures:" + getNombreDeMesures()
                + ", listeDeMesures:" + getListeDeMesures()
                + "}";
    }

    @Override
    public void run() {
        long i = 0;

        while(true) {
            i++;
            setNombreDeMesures(i);
            setListeDeMesures(new ListeDeMesures(this.mongoDatabase));
            System.out.println("Mesure no " + getNombreDeMesures()+ ", " + getListeDeMesures());
            try {
                sleep((long) 5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Controleur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @return la liste des mesures
     */
    public ListeDeMesures getListeDeMesures() {
        return listeDeMesures;
    }

    /**
     * @param listeDeMesures définit la liste des mesures
     */
    public void setListeDeMesures(ListeDeMesures listeDeMesures) {
        ListeDeMesures oldListeDeMesures = this.listeDeMesures;

        this.listeDeMesures = listeDeMesures;
        changeSupport.firePropertyChange("listeDeMesures", oldListeDeMesures, listeDeMesures);
    }

}
