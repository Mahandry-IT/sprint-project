<%@page import="mg.itu.controller.EtablissementController"%>
<%@page import="java.util.ArrayList"%>
<%
    EtablissementController emp = (EtablissementController) request.getAttribute("etablissement");
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
        <li>Id: <b><%= emp.getId()%></b></li>
        <li>Nom: <b><%= emp.getNom()%></b></li>
    </ul>
</body>
</html>