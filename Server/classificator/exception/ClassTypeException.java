package classificator.exception;

/**
 * Si verifica quando nell'acquisizione del trainingSet
 * dal database si ha un tipo dell'attributo di classe
 * diverso da quello atteso
 */
public class ClassTypeException extends Exception{
    
    public ClassTypeException(){
        super("Continuous class attribute");
    }

}
