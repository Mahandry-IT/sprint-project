<%@page import="mg.itu.controller.EmpController"%>
<%@page import="java.util.ArrayList"%>
<%
    String name = (String) request.getAttribute("name");
    String job = (String) request.getAttribute("job");
    java.sql.Date naissance = (java.sql.Date) request.getAttribute("naissance");
    double salaire = (double) request.getAttribute("salaire");
    int user = (int) request.getAttribute("user");
    String path = (String) request.getAttribute("photos");
    
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <ul>
        <li>Name: <b><%= name%></b></li>
        <li>Job: <b><%= job%></b></li>
        <li>Naissance: <b><%= naissance%></b></li>
        <li>Salaire: <b><%= salaire%></b></li>
        <li>User: <b><%= user%></b></li>
        <li>Name: <b><%= path%></b></li>
    </ul>
</body>
</html>