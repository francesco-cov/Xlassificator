package classificator.data;

/**
 * Estende la classe Attribute e rappresenta un attributo discreto
 */
public class DiscreteAttribute extends Attribute{

    private String values[];


    /**
     * Invoca il costruttore della superclasse e avvalora l'array values[] con valori discreti in input
     * @param name  Valori per nome simbolico dell'attributo
     * @param index Identificativo numerico dell'attributo
     * @param values Valori discreti
     */
    public DiscreteAttribute(String name, int index, String values[]){
        super(name, index);
        this.values = new String[values.length];
        // rimpiazza la copia dell'array tramite il classico ciclo for (più veloce)
        System.arraycopy(values, 0, this.values, 0, values.length); // deep copy
    }


    /**
     * Restituisce la cardinalità dell'array values[]
     * @return  Numero valori discreti dell'attributo
     */
    public int getNumberOfDistinctValues(){
        return values.length;
    }


    /**
     * Restituisce il valore dell'elemento i dell'array values[]
     * @param i indice di un solo valore discreto rispetto all'array
     * @return valore discreto con indice il parametro in input
     */
    public String getValue(int i){ // valore che l'attributo può assumere
        return values[i];
    }


    /**
     * @return Stringa contenente il nome dell'attributo
     */
    @Override
    public String toString(){
        return super.toString();
    }

}
