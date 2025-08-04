<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="mg.itu.model.Employe" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%
    Cookie[] cookies = request.getCookies();
    String errorJson = null;
    String link = (String) request.getAttribute("action");
    String objetJson = null;
    if (cookies != null) {
        for (Cookie c : cookies) {
            if ("errorData".equals(c.getName())) {
                errorJson = URLDecoder.decode(c.getValue(), "UTF-8");
            }
            if ("objetData".equals(c.getName())) {
                objetJson = URLDecoder.decode(c.getValue(), "UTF-8");
            }
        }
    }

    // Ensuite, d\u00E9s\u00E9rialiser et utiliser ces donn\u00E9es
    Gson gson = new Gson();
    HashMap<String, List<String>> error = gson.fromJson(errorJson, HashMap.class);
    Employe objet = gson.fromJson(objetJson, Employe.class);

%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>JSP page</title>
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <% if (error == null || error.isEmpty()) { %>
        <form action="<%= link %>" method="post">
            <label for="name">Name:</label>
            <input type="text" name="name" id="name">
            <label for="job">Job:</label>
            <input type="text" name="job" id="job">
            <label for="user">User:</label>
            <input type="number" name="user" id="user">
            <label for="Salaire">Salaire:</label>
            <input type="number" name="salaire" id="Salaire">
            <%-- <label for="Naissance">Naissance:</label>
            <input type="date" name="naissance" id="Naissance"> --%>
            <input type="submit" value="Valider">
        </form>
    <% } else { %>
        <form action="<%= link %>" method="post">
            <% if (!error.get("name").isEmpty()) { %>
                <% for (String errorMessage : error.get("name")) { %>
                    <p><%= errorMessage %></p>
                <% } %>
            <% } %>
            <label for="name">Name:</label>
            <input type="text" name="name" id="name" value="<%= objet.getName() != null ? objet.getName() : "" %>">
            <% if (!error.get("job").isEmpty()) { %>
                <% for (String errorMessage : error.get("job")) { %>
                    <p><%= errorMessage %></p>
                <% } %>
            <% } %>
            <label for="job">Job:</label>
            <input type="text" name="job" id="job" value="<%= objet.getJob() != null ? objet.getJob() : "" %>">
            <% if (!error.get("user").isEmpty()) { %>
                <% for (String errorMessage : error.get("user")) { %>
                    <p><%= errorMessage %></p>
                <% } %>
            <% } %>
            <label for="user">User:</label>
            <input type="number" name="user" id="user" value="<%= objet.getUser() != 0 ? objet.getUser() : 0 %>">
            <% if (!error.get("salaire").isEmpty()) { %>
                <% for (String errorMessage : error.get("salaire")) { %>
                    <p><%= errorMessage %></p>
                <% } %>
            <% } %>
            <label for="Salaire">Salaire:</label>
            <input type="number" name="salaire" id="Salaire" value="<%= objet.getSalaire() != 0.0 ? objet.getSalaire() : 0 %>">
            <%-- <% if (!error.get("naissance").isEmpty()) { %>
                <% for (String errorMessage : error.get("naissance")) { %>
                    <p><%= errorMessage %></p>
                <% } %>
            <% } %> --%>
            <%-- <label for="Naissance">Naissance:</label>
            <input type="date" name="naissance" id="Naissance" value="<%= objet.getNaissance() != null ? objet.getNaissance() : "" %>"> --%>
            <input type="submit" value="Valider">
        </form>
    <% } %>
</div>
<script src="assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
