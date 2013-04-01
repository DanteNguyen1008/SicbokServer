<%-- 
    Document   : UpdateBitcoinAddress
    Created on : Apr 1, 2013, 3:52:11 PM
    Author     : Kent
--%>

<%@page import="kent.Utils"%>
<%@page import="kent.component.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Bitcoin Address</title>
    </head>
    <body>
        <h1>Update Bitcoin Address</h1>
        <%
            User currentUser = null;
            currentUser = (User) session.getAttribute("user");
            if (session != null && currentUser != null) {
        %>
        Hello <b><% out.print(currentUser.getUsername());%></b>
        --- <a href="<%=Utils.SERVER_URL%>?type_of_request=sign_out">Sign out</a><br />
        Your current bitcoin address:<b> <% out.print(currentUser.getBitcoinId());%> </b><br />
        <form name="" action="<%=Utils.SERVER_URL%>" method="get">
            <input type="hidden" name="type_of_request" value="update_bitcoin_id" />
            <input type="text" name="bitcoin_id" value="<% out.print(currentUser.getBitcoinId());%>" />
            <input type="submit" value=" Update bitcoin address " />
        </form>
        <%
        } else {
        %>
        You are not sign in yet<br />
        Please sign in first:
        <form method="post" action="<%=Utils.SERVER_URL%>">
            <input type="hidden" name="backurl" value="<%=Utils.SERVER%>payment/UpdateBitcoinAddress.jsp" />
            <input type="hidden" name="type_of_request" value="sign_in" />
            <input type="text" name="username" /><br />
            <input type="password" name="password" />
            <input type="submit" value=" Sign in " />
        </form>
        <%
            }
        %>
    </body>
</html>
