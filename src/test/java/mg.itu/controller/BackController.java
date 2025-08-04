package mg.itu.controller; 
import java.time.LocalDate;
import java.util.Date;

import mg.itu.annotation.*; 

@AnnotationController()
public class BackController {

    public BackController(){}

    @Url(value = "/")
    public String welcome(){
        return "redirect:/emp/form";
    }
    @Url(value = "/hello")
    public String hello(){
        return "Hello Guys.";
    }
    @Url(value = "/date")
    public Date day(){
        return new Date(LocalDate.now().toEpochDay());
    }

    
}