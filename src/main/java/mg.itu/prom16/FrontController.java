package mg.itu.prom16;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;
import mg.itu.annotation.*;
import mg.itu.annotation.Error;
import mg.itu.error.ErrorHandler;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 50,       // 50 MB
    maxRequestSize = 1024 * 1024 * 100    // 100 MB
)
public class FrontController extends HttpServlet {

    private Map<String, Mapping> urlMappings = new HashMap<>();
    private boolean checked = false;
    private ArrayList<Class<?>> list;
    private Object objet;
    private HashMap<String, List<String>> error;
    private SprintConfigurationLoader sprintConfigurationLoader;

    @Override
    public void init() throws ServletException {
        Properties configProps = new Properties();
        InputStream in = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream("application.properties");
            if (in == null) {
                in = getServletContext().getResourceAsStream("/WEB-INF/application.properties");
            }
            if (in != null) {
                configProps.load(in);
            } else {
                throw new FileNotFoundException("Le fichier application.properties n'a pas \u00E9t\u00E9 trouv\u00E9 dans le classpath ni dans WEB-INF");
            }
            sprintConfigurationLoader = new SprintConfigurationLoader(configProps);
            
        } catch (Exception e) {
            throw new ServletException("Impossible de charger application.properties", e);
        } finally {
            if (in != null) {
                try { in.close(); } catch (IOException e) { /* Ignor\u00E9 */ }
            }
        }

        // Initialisation du scanner de controllers
        try {
            scanner();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to load controllers", e);
        }
    }

    private void scanner() throws ServletException {
        if (!checked) {
            String pack = getServletConfig().getInitParameter("ControllerPackage");
            if (pack == null) {
                throw new ServletException("The parameter name ControllerPackage doesn't exist");
            } else {
                try {
                    list = getClassesInSpecificPackage(pack);
                    getListController(pack);
                    checked = true;
                } catch (Exception e) {
                    list = new ArrayList<>();
                    throw new ServletException(e.getMessage());
                }
            }
        }
    }

    private boolean isEmpty() {
        if (error != null) {
            for (List<String> errorList : error.values()) {
                if (errorList != null && !errorList.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Object convertValue(Class<?> targetType, String value, HttpServletRequest request, String paramName) throws Exception {
        if (targetType == Part.class) {
            return request.getPart(paramName);
        }
    
        // Si la valeur est vide, assigner une valeur par défaut
        if (value == null || value.trim().isEmpty()) {
            if (targetType == int.class || targetType == Integer.class) {
                return 0;
            } else if (targetType == long.class || targetType == Long.class) {
                return 0L;
            } else if (targetType == double.class || targetType == Double.class) {
                return 0.0;
            } else if (targetType == float.class || targetType == Float.class) {
                return 0.0f;
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return false;
            } else if (targetType == short.class || targetType == Short.class) {
                return (short) 0;
            } else if (targetType == byte.class || targetType == Byte.class) {
                return (byte) 0;
            } else if (targetType == char.class || targetType == Character.class) {
                return '\0';
            } else if (targetType == java.sql.Date.class || targetType == Timestamp.class || targetType == Time.class) {
                return null;
            }
        }
    
        // Conversion des types
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(value);
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(value);
        } else if (targetType == char.class || targetType == Character.class) {
            return value.charAt(0);
        } else if (targetType == java.sql.Date.class) {
            try {
                return java.sql.Date.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Format de date invalide, attendu: yyyy-MM-dd", e);
            }
        } else if (targetType == Timestamp.class) {
            if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime localDateTime = LocalDateTime.parse(value, isoFormatter);
                return Timestamp.valueOf(localDateTime);
            } else if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
                value = value + ":00";
                DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime localDateTime = LocalDateTime.parse(value, isoFormatter);
                return Timestamp.valueOf(localDateTime);
            }
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return new Timestamp(dateTimeFormat.parse(value).getTime());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Format de timestamp invalide, attendu: yyyy-MM-dd HH:mm:ss ou yyyy-MM-dd'T'HH:mm:ss", e);
            }
        } else if (targetType == java.sql.Time.class) {
            if (value.matches("\\d{2}:\\d{2}")) {
                value += ":00";
            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            try {
                return new Time(timeFormat.parse(value).getTime());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Format de temps invalide, attendu: HH:mm:ss", e);
            }
        } else {
            throw new IllegalArgumentException("Type de paramètre non supporté: " + targetType);
        }
    }
    
    private void getListController(String package_name) throws Exception {
        String path = "WEB-INF/classes/" + package_name.replace(".", "/");
        path = getServletContext().getRealPath(path);
        File file = new File(path);
        list.clear();
        
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File uniquefile : file.listFiles()) {
                    if (uniquefile.isFile() && uniquefile.getName().endsWith(".class")) {
                        String className = package_name + "." + uniquefile.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(AnnotationController.class)) {
                            list.add(clazz);
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(Url.class)) {
                                    String url = method.getAnnotation(Url.class).value();
                                    String httpMethod = "GET";
                                    if (method.isAnnotationPresent(Get.class) && !method.isAnnotationPresent(Post.class)) {
                                        httpMethod = "GET";
                                    } else if (!method.isAnnotationPresent(Get.class) && method.isAnnotationPresent(Post.class)) {
                                        httpMethod = "POST";
                                    }
                                    if (urlMappings.containsKey(url)) {
                                        Mapping mapping = urlMappings.get(url);
                                        try {
                                            mapping.addVerbAction(new VerbAction(clazz.getName(), method, httpMethod));
                                        } catch (Exception e) {
                                            throw new Exception("L'URL " + url + " a d\u00E9j\u00E0 une action avec le verbe " + httpMethod);
                                        }
                                    } else {
                                        Mapping map = new Mapping();
                                        map.addVerbAction(new VerbAction(clazz.getName(), method, httpMethod));
                                        urlMappings.put(url, map);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            throw new Exception("The package " + package_name + " doesn't exist");
        }
    }
    
    private ArrayList<Class<?>> getClassesInSpecificPackage(String packageName) throws IOException, ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File directory = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(AnnotationController.class)) {
                                classes.add(clazz);
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    private Object getValueInMethod(HttpServletRequest request, HttpServletResponse response, Mapping map) throws ErrorHandler {
        Object returnValue = null;
        try {
            String httpMethod = request.getMethod();
            VerbAction verbAction = map.getActionByVerb(httpMethod);
            Class<?> clazz = Class.forName(verbAction.getClassName());
            verbAction.getMethodName().setAccessible(true);
            Parameter[] methodParams = verbAction.getMethodName().getParameters();
            Object[] args = new Object[methodParams.length];
            Enumeration<String> params = request.getParameterNames();
            Map<String, String> paramMap = new HashMap<>();
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                paramMap.put(paramName, request.getParameter(paramName));
            }
            for (int i = 0; i < methodParams.length; i++) {
                if (methodParams[i].getType().equals(MySession.class)) {
                    MySession session = new MySession();
                    session.setSession(request.getSession());
                    args[i] = session;
                } else if (methodParams[i].isAnnotationPresent(RequestBody.class)) {
                    Class<?> paramType = methodParams[i].getType();
                    Object paramObject = paramType.getDeclaredConstructor().newInstance();
                    for (Field field : paramType.getDeclaredFields()) {
                        if (field.isAnnotationPresent(FormParametre.class)) {
                            String paramName = field.getAnnotation(FormParametre.class).value();
                            if (paramMap.containsKey(paramName)) {
                                field.setAccessible(true);
                                field.set(paramObject, convertValue(field.getType(), paramMap.get(paramName), request, paramName));
                            }
                        }
                    }
                    validateAttributes(paramObject);
                    validate(paramObject);
                    objet = paramObject;
                    if (error != null && !isEmpty()) {
                        return null; 
                    }
                    args[i] = paramObject;
                } else if (methodParams[i].isAnnotationPresent(RequestParam.class)) {
                    Class<?> paramType = methodParams[i].getType();
                    Object paramObject = paramType.getDeclaredConstructor().newInstance();
                    for (Field field : paramType.getDeclaredFields()) {
                        if (field.isAnnotationPresent(FormParametre.class)) {
                            String paramName = field.getAnnotation(FormParametre.class).value();
                            if (paramMap.containsKey(paramName)) {
                                field.setAccessible(true);
                                field.set(paramObject, convertValue(field.getType(), paramMap.get(paramName), request, paramName));
                            }
                        }
                    }
                    args[i] = paramObject;
                } else if (methodParams[i].isAnnotationPresent(Parametre.class)) {
                    String paramName = methodParams[i].getAnnotation(Parametre.class).name();
                    String paramValue = paramMap.get(paramName);
                    args[i] = convertValue(methodParams[i].getType(), paramValue, request, paramName);
                } else if (methodParams[i].getType().equals(HttpServletResponse.class)) {
                    args[i] = response; 
                } else if (methodParams[i].getType().equals(HttpServletRequest.class)) {
                    args[i] = request; 
                } else {
                    if (paramMap.containsKey(methodParams[i].getName())) {
                        args[i] = convertValue(methodParams[i].getType(), paramMap.get(methodParams[i].getName()), request, methodParams[i].getName());
                    } else {
                        args[i] = null; // Laisse le param\u00E8tre \u00E0 NULL s'il n'est pas trouv\u00E9
                    }
                    // throw new Exception("ETU002393: No associated parameter found.");
                }
            }
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType().equals(MySession.class)) {
                    field.setAccessible(true);
                    MySession session = new MySession();
                    session.setSession(request.getSession());
                    field.set(instance, session);
                    break;
                }
            }
            returnValue = verbAction.getMethodName().invoke(instance, args);
        } catch (Exception e) {
            throw new ErrorHandler(e);
        }
        return returnValue;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        error = null;
        objet = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("errorData".equals(cookie.getName()) || "objetData".equals(cookie.getName())) {
                    cookie.setMaxAge(0); 
                    cookie.setPath(request.getContextPath()); 
                    response.addCookie(cookie);
                }
            }
        }
        response.setContentType("text/html;charset=UTF-8");
        String url = request.getRequestURI().substring(request.getContextPath().length());
        try {
            PrintWriter out = response.getWriter();
            Mapping mapping = urlMappings.get(url);
            if (mapping != null) {
                String httpMethod = request.getMethod();
                VerbAction verbAction = mapping.getActionByVerb(httpMethod);
                if (verbAction == null) {
                    Exception e = new Exception("M\u00E9thode HTTP incorrecte: " + httpMethod 
                    + " utilis\u00E9 pour " + url + ". Aucun handler pour ce verbe HTTP.");
                    throw new ErrorHandler(404, e, "Page non trouv\u00E9e");
                }
                Method method = verbAction.getMethodName();
                if (!hasAccess(method.getName(), request, verbAction.getClassName())) {
                    Exception e = new Exception("Vous n'avez pas l'authorization \u00E0 acceder au lien " + url+ " \u00E0 la m\u00E9thode "+httpMethod);
                    throw new ErrorHandler(403, e, "Authorisation requis");
                }
                RestAPI annotation = method.getAnnotation(RestAPI.class);
                
                Object ob = getValueInMethod(request, response, mapping);
                if (error != null && !isEmpty()) {
                    String referer = request.getHeader("Referer");
                    String contextPath = request.getContextPath();
                    String relativePath = "/";
                    if (referer != null && referer.startsWith(contextPath)) {
                        relativePath = referer.substring(referer.indexOf(contextPath) + contextPath.length());
                    }
                    Gson gson = new Gson();
                    String errorJson = gson.toJson(error);
                    String objetJson = gson.toJson(objet);
                    Cookie errorCookie = new Cookie("errorData", URLEncoder.encode(errorJson, "UTF-8"));
                    Cookie objetCookie = new Cookie("objetData", URLEncoder.encode(objetJson, "UTF-8"));
                    errorCookie.setPath(request.getContextPath());
                    objetCookie.setPath(request.getContextPath());
                    objetCookie.setMaxAge(60*60);
                    errorCookie.setHttpOnly(true);
                    objetCookie.setHttpOnly(true);
                    errorCookie.setMaxAge(60*60);
                    response.addCookie(errorCookie);
                    response.addCookie(objetCookie);
                    if (method.isAnnotationPresent(Error.class)) {
                        response.sendRedirect(request.getContextPath() + method.getAnnotation(Error.class).value());    
                    }
                    response.sendRedirect(request.getContextPath() + relativePath);
                }
                if (ob != null) {
                    if (annotation != null) {
                        response.setContentType("application/json");
                        Gson gson = new Gson();
                        if (ob instanceof ModelAndView mw) {
                            JsonObject json = new JsonObject();
                            for (String key : mw.getData().keySet()) {
                                json.add(key, gson.toJsonTree(mw.getData().get(key)));
                            }
                            out.println(gson.toJson(json));
                        } else if(ob instanceof String) {
                            String reponse = String.valueOf(ob);
                            if (reponse.contains("redirect:/")) {
                                reponse = reponse.replace("redirect:/", "");
                                if (!reponse.startsWith("/")) {
                                    reponse = "/" + reponse;
                                }
                                response.sendRedirect(request.getContextPath() + reponse);
                            }
                        }else {
                            out.println(gson.toJson(ob));
                        }
                    } else {
                        if (ob instanceof String) {
                            String reponse = String.valueOf(ob);
                            if (reponse.contains("redirect:/")) {
                                reponse = reponse.replace("redirect:/", "");
                                if (!reponse.startsWith("/")) {
                                    reponse = "/" + reponse;
                                }
                                response.sendRedirect(request.getContextPath() + reponse);
                            } else{
                                out.println("La valeur retourn\u00E9e par la m\u00E9thode <b>" 
                                        + verbAction.getMethodName().getName() + "</b> est: <b>" + ob + "</b>.");
                            }
                        } else if (ob instanceof ModelAndView mw) {
                            if (mw.getData() != null) {
                                for (String cle : mw.getData().keySet()) {
                                    request.setAttribute(cle, mw.getData().get(cle));
                                }
                            }
                            RequestDispatcher dispatcher = request.getRequestDispatcher(mw.getUrl());
                            dispatcher.forward(request, response);
                        } else {
                            Exception e = new Exception("La valeur retourn\u00E9e par la m\u00E9thode " 
                            + verbAction.getMethodName().getName() + " n'est pas disponible dans notre framework.");
                            throw new ErrorHandler(e);
                        }
                    }
                } else {
                    Exception e = new Exception("Pas de valeur retourn\u00E9.");
                    throw new ErrorHandler(e);
                }
            } else {
                Exception e = new Exception("Pas de m\u00E9thode associ\u00E9 \u00E0 l'URL: " + url);
                    throw new ErrorHandler(e);
            }
        } catch (Exception ex) {
            ErrorHandler error = new ErrorHandler(ex);
            String referer = request.getHeader("Referer");
            request.setAttribute("exception", error);
            request.setAttribute("previous", referer);
            if (!sprintConfigurationLoader.getErrorPage().startsWith("/")) {
                sprintConfigurationLoader.setErrorPage("/" + sprintConfigurationLoader.getErrorPage());
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(sprintConfigurationLoader.getErrorPage());
            dispatcher.forward(request, response);

        }
    }


    private void validateWithoutError(Object obj) throws Exception {
        error = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            List<String> list = new ArrayList<>();
            if (field.isAnnotationPresent(Numeric.class)) {
                Numeric numeric = field.getAnnotation(Numeric.class);
                double value = (Double) field.get(obj);
                if (!Validator.validatePrecisionAndScale(value, numeric.precision(), numeric.scale())) {
                    list.add("Valeur de d\u00E9cimal invalide pour le champ " + field.getName() + " (pr\u00E9cision: " + numeric.precision() + ", scale: " + numeric.scale() + ")");
                }
                error.put(field.getName(), list);
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Entier.class)) {
                mg.itu.annotation.Entier numeric = field.getAnnotation(mg.itu.annotation.Entier.class);
                int value = (Integer) field.get(obj);
                if (!Validator.validateNumber(value, numeric.min(), numeric.max())) {
                    list.add("Valeur d'entier hors limites pour le champ " + field.getName());
                }
                error.put(field.getName(), list);
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Date.class)) {
                mg.itu.annotation.Date date = field.getAnnotation(mg.itu.annotation.Date.class);
                java.sql.Date datesql = (java.sql.Date) field.get(obj);
                if (!Validator.validateDate(datesql, date.format())) {
                    list.add("Format de date invalide pour le champ " + field.getName());
                }
                error.put(field.getName(), list);
            }
            if (field.isAnnotationPresent(mg.itu.annotation.DateHeure.class)) {
                mg.itu.annotation.DateHeure dateHeure = field.getAnnotation(mg.itu.annotation.DateHeure.class);
                java.sql.Timestamp datesql = (java.sql.Timestamp) field.get(obj);
                if (!Validator.validateDateHeure(datesql, dateHeure.format())) {
                    list.add("Format de date et heure invalide pour le champ " + field.getName());
                }
                error.put(field.getName(), list);
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Temps.class)) {
                mg.itu.annotation.Temps temps = field.getAnnotation(mg.itu.annotation.Temps.class);
                java.sql.Time datesql = (java.sql.Time) field.get(obj);
                if (!Validator.validateTemps(datesql, temps.format())) {
                    list.add("Format d'heure invalide pour le champ " + field.getName());
                }
                error.put(field.getName(), list);
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Bool.class)) {
                Boolean boolea = (Boolean) field.get(obj);
                if (!Validator.validateBoolean(boolea)) {
                    list.add("Format de boolean invalide pour le champ " + field.getName());
                }
                error.put(field.getName(), list);
            }
        }
    }

    private void validateWithError(Object obj) throws Exception {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Numeric.class)) {
                Numeric numeric = field.getAnnotation(Numeric.class);
                double value = (Double) field.get(obj);
                if (value < numeric.min() || value > numeric.max()) {
                    error.get(field.getName()).add("Valeur de " + field.getName() + " hors limites");
                }
                if (!Validator.validatePrecisionAndScale(value, numeric.precision(), numeric.scale())) {
                    error.get(field.getName()).add("Valeur de d\u00E9cimal invalide pour le champ " + field.getName() + " (pr\u00E9cision: " + numeric.precision() + ", scale: " + numeric.scale() + ")");
                }
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Entier.class)) {
                mg.itu.annotation.Entier numeric = field.getAnnotation(mg.itu.annotation.Entier.class);
                int value = (Integer) field.get(obj);
                if (!Validator.validateNumber(value, numeric.min(), numeric.max())) {
                    error.get(field.getName()).add("Valeur d'entier hors limites pour le champ " + field.getName());
                }
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Date.class)) {
                mg.itu.annotation.Date date = field.getAnnotation(mg.itu.annotation.Date.class);
                java.sql.Date datesql = (java.sql.Date) field.get(obj);
                if (!Validator.validateDate(datesql, date.format())) {
                    error.get(field.getName()).add("Format de date invalide pour le champ " + field.getName());
                }
            }
            if (field.isAnnotationPresent(mg.itu.annotation.DateHeure.class)) {
                mg.itu.annotation.DateHeure dateHeure = field.getAnnotation(mg.itu.annotation.DateHeure.class);
                java.sql.Timestamp datesql = (java.sql.Timestamp) field.get(obj);
                if (!Validator.validateDateHeure(datesql, dateHeure.format())) {
                    error.get(field.getName()).add("Format de date et heure invalide pour le champ " + field.getName());
                }
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Temps.class)) {
                mg.itu.annotation.Temps temps = field.getAnnotation(mg.itu.annotation.Temps.class);
                java.sql.Time datesql = (java.sql.Time) field.get(obj);
                if (!Validator.validateTemps(datesql, temps.format())) {
                    error.get(field.getName()).add("Format d'heure invalide pour le champ " + field.getName());
                }
            }
            if (field.isAnnotationPresent(mg.itu.annotation.Bool.class)) {
                Boolean boolea = (Boolean) field.get(obj);
                if (!Validator.validateBoolean(boolea)) {
                    error.get(field.getName()).add("Format de boolean invalide pour le champ " + field.getName());
                }
            }
        }
    }

    public void validate(Object obj) throws Exception {
        if (error == null || error.isEmpty()) {
            validateWithoutError(obj);
        } else {
            validateWithError(obj);
        }
    }

    private void validateAttributes(Object object) throws Exception {
        error = new HashMap<>();
        Class<?> clazz = object.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            List<String> list = new ArrayList<>();
            field.setAccessible(true);
            Object value = field.get(object);
            Class<?> fieldType = field.getType();
    
            // Validation @Required
            if (field.isAnnotationPresent(Required.class)) {
                boolean isEmpty = false;
    
                if (value == null) {
                    isEmpty = true; // Si c'est un objet et qu'il est null
                } else if (value instanceof String && ((String) value).trim().isEmpty()) {
                    isEmpty = true; // Si c'est une chaîne vide
                } else if (fieldType.isPrimitive()) { 
                    if ((fieldType == int.class && (int) value == 0) ||
                        (fieldType == double.class && (double) value == 0.0) ||
                        (fieldType == long.class && (long) value == 0L) ||
                        (fieldType == float.class && (float) value == 0.0f) ||
                        (fieldType == short.class && (short) value == 0) ||
                        (fieldType == byte.class && (byte) value == 0) ||
                        (fieldType == boolean.class && !(boolean) value)) { // false est la valeur par d\u00E9faut
                        isEmpty = true;
                    }
                }
    
                if (isEmpty) {
                    list.add("Le champ " + field.getName() + " est requis et ne doit pas \u00EAtre vide ou avoir sa valeur par d\u00E9faut.");
                }
                error.put(field.getName(), list);
            }
    
            // Validation @Numeric
            if (field.isAnnotationPresent(Numeric.class)) {
                if (value != null && !(value instanceof Number)) {
                    list.add("Le champ " + field.getName() + " doit \u00EAtre un nombre.");
                }
                error.put(field.getName(), list);
            }
    
            // Validation @Entier
            if (field.isAnnotationPresent(mg.itu.annotation.Entier.class)) {
                if (value != null && !(value instanceof Integer || fieldType == int.class)) {
                    list.add("Le champ " + field.getName() + " doit \u00EAtre de type int.");
                }
                error.put(field.getName(), list);
            }
    
            // Validation @Date
            if (field.isAnnotationPresent(mg.itu.annotation.Date.class)) {
                if (value != null && !(value instanceof LocalDate || value instanceof java.sql.Date)) {
                    list.add("Le champ " + field.getName() + " doit \u00EAtre de type date.");
                }
                error.put(field.getName(), list);
            }
    
            // Validation @DateHeure
            if (field.isAnnotationPresent(mg.itu.annotation.DateHeure.class)) {
                if (value != null && !(value instanceof Timestamp)) {
                    list.add("Le champ " + field.getName() + " doit \u00EAtre de type date et heure.");
                }
                error.put(field.getName(), list);
            }
    
            // Validation @Temps
            if (field.isAnnotationPresent(mg.itu.annotation.Temps.class)) {
                if (value != null && !(value instanceof Time)) {
                    list.add("Le champ " + field.getName() + " doit \u00EAtre de type heure.");
                }
                error.put(field.getName(), list);
            }
    
            // Validation @Bool
            if (field.isAnnotationPresent(mg.itu.annotation.Bool.class)) {
                if (value != null && !(value instanceof Boolean || fieldType == boolean.class)) {
                    list.add("Le champ " + field.getName() + " doit \u00EAtre de type boolean.");
                }
                error.put(field.getName(), list);
            }
        }
    }
    

    public boolean hasAccess(String methodName, HttpServletRequest request, String classname) throws ErrorHandler {
        try {
            Class<?> clazz = Class.forName(classname);
            Method method = null;
            for (Method m : clazz.getMethods()) {
                if (m.getName().equals(methodName)) {
                    method = m;
                    break;
                }
            }
            if (method == null) {
                throw new NoSuchMethodException("M\u00E9thode " + methodName + " non trouv\u00E9e dans la classe " + classname);
            }
            Authenticated auth = null;
            if (method.isAnnotationPresent(Authenticated.class)) {
                auth = method.getAnnotation(Authenticated.class);
            } else if (clazz.isAnnotationPresent(Authenticated.class)) {
                auth = clazz.getAnnotation(Authenticated.class);
            }
            
            if (auth != null) {
                int[] rolesAllowed = auth.roles();
                int currentUserRole = (int) request.getSession().getAttribute(sprintConfigurationLoader.getSessionRole()); 
                for (int role : rolesAllowed) {
                    if (role == currentUserRole) {
                        return true;
                    }
                }
                return false;
            }
            return true;
            
        } catch (Exception e) {
            throw new ErrorHandler(new Exception("Erreur lors de la v\u00E9rification des acc\u00E8s", e));
        }
    }
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json");
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            PrintWriter out = response.getWriter();
            out.println("Erreur: " + ex.getMessage());
            ex.printStackTrace(new java.io.PrintWriter(out));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json");
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            PrintWriter out = response.getWriter();
            out.println("Erreur: " + ex.getMessage());
            ex.printStackTrace(new java.io.PrintWriter(out));
        }
    }
}
