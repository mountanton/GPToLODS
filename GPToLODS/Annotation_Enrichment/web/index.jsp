<%-- 
    Document   : index
    Created on : 28-May-2014, 14:30:06
    Author     : micha_000
--%>
<%		boolean folders = false;
    String pag = "main.jsp";
    if (request.getAttribute("page") != null) {
        pag = request.getAttribute("page").toString();

    }

%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>GPT&#x2022;LODS</title>
</head>
<body>
    <jsp:include page="<%=pag%>"/>
</body>
</html>
