package classificator.exception;

/**
 * Si verifica quando non esiste alcun nodo di split
 */
public class NoSplitException extends Exception{

    public NoSplitException(){
        super("Empty explanatory attributes's set");
    }

}
