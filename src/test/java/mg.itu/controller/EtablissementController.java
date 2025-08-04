package mg.itu.controller;

import mg.itu.annotation.AnnotationController;
import mg.itu.annotation.FormParametre;
import mg.itu.annotation.Get;
import mg.itu.annotation.Parametre;
import mg.itu.annotation.RequestBody;
import mg.itu.prom16.ModelAndView;

@AnnotationController()
public class EtablissementController {

    @FormParametre("id")
    String id;

    String nom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public EtablissementController(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }
}
