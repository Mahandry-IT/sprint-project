package mg.itu.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.itu.annotation.AnnotationController;
import mg.itu.annotation.Get;
import mg.itu.annotation.Url;
import mg.itu.model.Employe;
import mg.itu.annotation.Parametre;
import mg.itu.annotation.Post;
import mg.itu.annotation.RequestBody;
import mg.itu.annotation.RestAPI;
import mg.itu.prom16.FileSave;
import mg.itu.prom16.ModelAndView;

@AnnotationController()
public class EmpController {
    
    Employe emp = new Employe();

    @Url(value = "/emp/list")
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/list.jsp");
        mv.addObject("list", emp.generateListEmp());
        return mv;
    }

    @Url(value = "/emp/table")
    @RestAPI
    public List<Employe> liste() {
        return emp.generateListEmp();
    }

    @Url(value = "/emp/tables")
    @RestAPI
    public ModelAndView table(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/list.jsp");
        mv.addObject("list", emp.generateListEmp());
        return mv;
    }

    @Get
    @Url(value="/emp/form")
    public ModelAndView form(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/form.jsp");
        mv.addObject("action", "treatment");
        return mv;
    }

    @Get
    @Url(value="/emp/formulaire")
    public ModelAndView formulaire(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/formulaire.jsp");
        mv.addObject("action", "traitement");
        return mv;
    }

    @Post
    @Url(value="/emp/formout")
    public ModelAndView formol(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/form.jsp");
        mv.addObject("action", "treatment");
        return mv;
    }

    // @Post
    // @Url(value="/emp/form")
    // public ModelAndView formout(){
    //     ModelAndView mv = new ModelAndView();
    //     mv.setUrl("../views/emp/formout.jsp");
    //     mv.addObject("action", "treat");
    //     return mv;
    // }

    // @Authenticated(roles = {1}) 
    @Post
    @Url(value="/emp/treatment")
    public ModelAndView treatment(@RequestBody Employe employe) throws Exception {
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/result.jsp");

        mv.addObject("name", employe.name);
        mv.addObject("job", employe.job);
        mv.addObject("user", employe.user);
        // mv.addObject("naissance", employe.naissance);
        mv.addObject("salaire", employe.salaire);
        return mv;
    }

    
    @Post
    @Url(value="/emp/traitement")
    public ModelAndView treatment(@Parametre(name = "photos") Part photoFile, HttpServletRequest request) throws Exception {
        Employe emp = new Employe();
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/resultat.jsp");
        if (photoFile != null && photoFile.getSize() > 0) {
              emp.photos = FileSave.saveFile(photoFile, request);
        }
        mv.addObject("photos", emp.photos);
        return mv;
    }
    

    @Url(value="/emp/treat")
    public ModelAndView treat(String name,String job){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/result.jsp");
        mv.addObject("name", name);
        mv.addObject("job", job);
        return mv;
    }

    // @Url(value="/emp/treatment")
    // public ModelAndView treatment(@RequestBody EmpController employe ){
    //     ModelAndView mv = new ModelAndView();
    //     mv.setUrl("../views/emp/result.jsp");
    //     mv.addObject("employe", employe);
    //     return mv;
    // }
}
