package mg.itu.annotation;
import jakarta.servlet.http.HttpSession;

public class MySession {
    private HttpSession session;

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public void update(String key, Object value){
        if( session.getAttribute(key) != null ){
            session.setAttribute(key, value);
        }
    }

    public void add(String key, Object value){
        if( session.getAttribute(key) == null ){
            session.setAttribute(key, value);
        }
    }

    public void remove(String key){
        session.removeAttribute(key);
    }
    
    public Object get(String key){
        return session.getAttribute(key);
    }
}
