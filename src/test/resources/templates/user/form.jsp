<%-- 
    Document   : index
    Created on : 25 janv. 2024, 11:59:21
    Author     : mahan
--%>
<%@page import="mg.itu.controller.EmpController"%>
<%@page import="java.util.ArrayList"%>
<%
    String link = (String)request.getAttribute("action");
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>JSP page</title>
    <!-- Bootstrap CSS -->
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <form action="<%= link %>" method="post">
        <label for="email">Email:</label>
        <input type="email" name="email" id="email">
        <label for="pwd">Password:</label>
        <input type="password" name="pwd" id="pwd">
        <input type="submit" value="Valider">
    </form>
</div>
<!-- Bootstrap JS (optional, but needed for some features) -->
<script src="assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
