package mg.itu.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;

import mg.itu.annotation.AnnotationController;
import mg.itu.annotation.Authenticated;
import mg.itu.annotation.Url;
import mg.itu.model.Employe;
import mg.itu.annotation.MySession;
import mg.itu.annotation.Parametre;
import mg.itu.annotation.Post;
import mg.itu.prom16.ModelAndView;

@AnnotationController()
public class UserController {
    int user;
    String email;
    String password;

    private MySession session;

    public UserController(int user, String email, String password) {
        this.user = user;
        this.email = email;
        this.password = password;
    }

    public UserController() {}

    public MySession UrlSession() {
        return session;
    }

    public void setSession(MySession session) {
        this.session = session;
    }
    
    @Post
    @Url(value="/user/login")
    public ModelAndView login(@Parametre(name = "email")String email,@Parametre(name = "pwd")String pwd){
        ModelAndView mv = new ModelAndView();
        ArrayList<UserController> listuser = generateUser();
        for (UserController user : listuser) {
            if (user.UrlEmail().compareTo(email) == 0 && user.UrlPassword().compareTo(pwd) == 0) {
                session.add("user", user.UrlUser());
                mv.setUrl("../views/emp/form.jsp");
                mv.addObject("session", session);
                return mv;
            }
        }
        mv.setUrl("../views/user/form.jsp");
        mv.addObject("error", "Identifiant incorrect");
        return mv;
    }

    @Url(value="/user/form")
    public ModelAndView form(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/user/form.jsp");
        mv.addObject("action", "login");
        return mv;
    }

    @Url(value="/user/logout")
    public ModelAndView logout(){
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/user/form.jsp");
        mv.addObject("action", "login");
        return mv;
    }

    private ArrayList<UserController> generateUser(){
        ArrayList<UserController> listuser = new ArrayList<>();
        listuser.add(new UserController(1,"mahandry@gmail.com", "1234"));
        listuser.add(new UserController(2,"toky@gmail.com", "pwd1234"));
        listuser.add(new UserController(3,"sitraka@gmail.com", "123456"));
        return listuser;
    }

    public int UrlUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String UrlEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String UrlPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Authenticated(roles = {1}) 
    @Url(value="/user/empList")
    @Post
    public ModelAndView empList() {
        ModelAndView mv = new ModelAndView();
        mv.setUrl("../views/emp/list.jsp");
        mv.addObject("list", new Employe().generateListEmp());
        return mv;
    }
}
