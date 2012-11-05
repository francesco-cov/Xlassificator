package classificator.data;

import java.io.Serializable;

/**
 * Classe astratta che modella un generico attributo discreto o continuo
 */
public abstract class Attribute implements Serializable{

    protected String name;
    protected int index;


    /**
     * E' il costruttore di classe. Inizializza i valori dei membri
     * @param name  nome simbolico dell'attributo
     * @param index identificativo numerico dell'attributo
     */
    Attribute(String name, int index){
        this.name = name;
        this.index = index;
    }

    
    /**
     * Restituisce il valore nel membro name
     * @return  Nome simbolico dell'attributo
     */
    public String getName(){
        return name;
    }


    /**
     * Restituisce il valore nel membro index
     * @return  Identificativo numerico dell'attributo
     */
    public int getIndex(){
        return index;
    }


    /**
     * @return Stringa contenente il nome dell'attributo
     */
    @Override
    public String toString(){
        return getName();
    }

}
