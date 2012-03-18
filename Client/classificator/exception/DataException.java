package classificator.exception;

/**
 * Si verifica quando nell'acquisire il trainingSet dal
 * database viene sollevata una SQLException
 */
public class DataException extends Exception{

    public DataException(String sqlMessage) {
        super(sqlMessage);
    }

}
