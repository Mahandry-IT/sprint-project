package mg.itu.error;

public class ErrorHandler extends Exception {
    private int code = 500; 
    private Exception exception;
    private String titre;

    public ErrorHandler(int code, Exception exception, String titre) {
        super(exception);
        this.code = code;
        this.exception = exception;
        this.titre = titre;
    }

    public ErrorHandler(Exception exception) {
        super(exception);
        this.exception = exception;
        this.titre = "Erreur interne du serveur";
    }

    public int getCode() {
        return code;
    }

    public Exception getException() {
        return exception;
    }

    public String getTitre() {
        return titre;
    }
}
