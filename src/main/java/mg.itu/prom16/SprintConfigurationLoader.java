package mg.itu.prom16;

import java.util.Properties;

public class SprintConfigurationLoader {

    private String errorPage;
    private String sessionRole = "idrole";
    private int cookieMaxAge = 60;
    private String fileDirectory;

    public SprintConfigurationLoader(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getErrorPage() {
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getSessionRole() {
        return sessionRole;
    }

    public void setSessionRole(String sessionRole) {
        this.sessionRole = sessionRole;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public void initAttributes(Properties props) throws Exception {
        this.errorPage = props.getProperty("sprint.error.page", "views/error.jsp");
        this.sessionRole = props.getProperty("sprint.session.role", "idrole");
        this.fileDirectory = props.getProperty("sprint.file.directory", System.getProperty("user.dir") + "/save");

        try {
            this.cookieMaxAge = Integer.parseInt(props.getProperty("cookie.maxAge", "60"));
        } catch (NumberFormatException e) {
            this.cookieMaxAge = 60;
            throw new Exception("Erreur lors du chargement de application.properties", e);
        }
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }
}