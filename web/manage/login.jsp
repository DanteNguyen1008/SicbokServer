<%-- 
    Document   : login
    Created on : Mar 28, 2013, 1:07:14 PM
    Author     : Admin
--%>

<%@page import="kent.Utils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Management login Page</title>
    </head>
    <body>
        
        <form action="<%=Utils.SERVER_URL_ADMIN%>" method="POST">
 
            User name : <input type="text" id="txtUsername" name="txtUsername" />
            Password : <input type="password" id="txtPassword" name="txtPassword" />
            <input type="submit" name="btnSubmit" id="btnSubmit" value="Login" />
            <input type="hidden" value="admin_sign_in" name="request_name" id="request_name" />
        </form>
        <%            
            String status = request.getParameter("status");
            if (status != null) {
                if (status.equals("false")) {
                    out.println("Login fail");
                }
            }
        %>
    </body>
</html>
