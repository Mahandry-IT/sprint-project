<%@page import="mg.itu.error.ErrorHandler"%>
<%
    ErrorHandler ex = (ErrorHandler) request.getAttribute("exception");
    java.io.StringWriter sw = new java.io.StringWriter();
    java.io.PrintWriter pw = new java.io.PrintWriter(sw);
    if (ex != null) {
        ex.getException().printStackTrace(pw);
    }
    String stackTrace = sw.toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error</title>
</head>
<body>
    <ul>
        <li>Message: <b><%= ex != null ? ex.getException().getMessage() : "Erreur inconnue" %></b></li>
    </ul>
    <p><pre><%= stackTrace %></pre></p> 
</body>
</html>
