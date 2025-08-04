package mg.itu.prom16;

import java.util.HashMap;

public class ModelAndView {
    String url;
    HashMap<String,Object> data;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String,Object> getData() {
        return data;
    }
    public void setData(HashMap<String,Object> data) {
        this.data = data;
    }
    public void addObject(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }
    
    
}
