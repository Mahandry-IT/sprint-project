<%-- 
    Document   : index
    Created on : 25 janv. 2024, 11:59:21
    Author     : mahan
--%>
<%@page import="mg.itu.model.Employe"%>
<%@page import="mg.itu.annotation.MySession"%>
<%@page import="java.util.ArrayList"%>
<%
    MySession ses = (MySession) request.getAttribute("session");
    ArrayList<Employe> emps =(ArrayList<Employe>) request.getAttribute("list");
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
<%if (ses != null) { %>
    <form action="logout" method="post">
        <input type="submit" value="Deconnecter">
    </form>
<% } %>
<div class="container mt-5">
    <table class="table" border="1">
        <thead>
        <tr>
            <th>Name</th>
            <th>Job</th>
        </tr>
        </thead>
        <tbody>
            <% for (Employe emp : emps) { %>
                <tr>
                    <td><%=emp.getName() %></td>
                    <td><%=emp.getJob() %></td>
                </tr>
            <% } %>
        
        </tbody>
    </table>
</div>
<!-- Bootstrap JS (optional, but needed for some features) -->
<script src="assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
