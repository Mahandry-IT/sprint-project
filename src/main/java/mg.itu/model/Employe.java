package mg.itu.model;

import java.util.ArrayList;

import mg.itu.annotation.Date;
import mg.itu.annotation.Entier;
import mg.itu.annotation.FormParametre;
import mg.itu.annotation.MySession;
import mg.itu.annotation.Numeric;
import mg.itu.annotation.Required;

public class Employe {
    @FormParametre("name")
    @Required
    public String name;

    @FormParametre("job")
    @Required
    public String job;

    @Required
    @FormParametre("user")
    @Entier(min = 1, max = 3)    
    public int user;

    @Required
    @FormParametre("salaire")
    @Numeric
    public double salaire;

    // @Required
    // @FormParametre("naissance")
    // @Date(format = "yyyy-MM-dd")
    // public java.sql.Date naissance;

    // @Required
    @FormParametre("photos")
    public String photos;

    public byte[] data;

    private MySession session;

    public MySession UrlSession() {
        return session;
    }

    public void setSession(MySession session) {
        this.session = session;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public Employe() {
    }

    public Employe(int user,String name, String job) {
        setUser(user);
        setJob(job);
        setName(name);
    }

    public String UrlName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String UrlJob() {
        return job;
    }
    public void setJob(String job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    // public java.sql.Date getNaissance() {
    //     return naissance;
    // }

    // public void setNaissance(java.sql.Date naissance) {
    //     this.naissance = naissance;
    // }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public MySession getSession() {
        return session;
    }

    public ArrayList<Employe> generateListEmp(){
        ArrayList<Employe> listemp = new ArrayList<>();
        listemp.add(new Employe(1,"Mahandry", "Ing\u00E9nieur"));
        listemp.add(new Employe(1,"Toky", "Web Designer"));
        listemp.add(new Employe(2,"Toky", "Web Designer"));
        listemp.add(new Employe(2,"Sitraka", "Directeur Administratif"));
        listemp.add(new Employe(3,"Ony", "Comptable"));
        listemp.add(new Employe(3,"Sambatra", "Coursier"));
        return listemp;
    }
    
    public ArrayList<Employe> UrlListById(int id){
        ArrayList<Employe> liste = generateListEmp();
        ArrayList<Employe> filteredListFor = new ArrayList<>();
        for (Employe emp : liste) {
            if (emp.getUser() == id) {
                filteredListFor.add(emp);
            }
        }
        return filteredListFor;
    }

}