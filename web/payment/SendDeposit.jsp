<%-- 
    Document   : SendDeposit
    Created on : Mar 29, 2013, 10:21:38 AM
    Author     : Kent
--%>

<%@page import="kent.Utils"%>
<%@page import="kent.component.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Send Deposit</title>
    </head>
    <body>
        <h1>Send deposit!</h1>
        <%
            User currentUser = null;
            currentUser = (User) session.getAttribute("user");
            if (session != null && currentUser != null) {
        %>
        Hello <b><% out.print(currentUser.getUsername());%></b>
        --- <a href="<%=Utils.SERVER_URL%>?type_of_request=sign_out">Sign out</a><br />
        Your current balance is:<b> <% out.print(currentUser.getCurrentBalance());%> </b><br />
        <form name="" action="https://www.bitcoin247.com/en/HostedPayment" method="get">
            <%
                String merchantPaymentId = kent.Hash.getHashSHA256(kent.Utils.randomString(5) + System.currentTimeMillis());
                merchantPaymentId = merchantPaymentId.substring(5);
            %>
            <input type="hidden" name="merchantid" value="<%= Utils.MERCHANT_ID %>" />
            <input type="hidden" name="businessnameid" value="2" />
            <input type="hidden" name="merchantpaymentid" value="<% out.print(merchantPaymentId);%>" />
            <input type="hidden" name="note" value="<% out.print(currentUser.getUserId());%>" />                        
            <input type="hidden" name="returnurl" value="http://sicbogame.jelastic.servint.net/sicbogame/payment/deposit-success.html" />
            <input type="text" name="expectedamount" value="0.01" />
            <input type="submit" value=" Deposit bitcoin " />
        </form>
        <%


            } else {                
                %>
                You are not sign in yet<br />
                Please sign in first:
                <form method="post" action="<%=Utils.SERVER_URL%>">
                    <input type="hidden" name="backurl" value="<%=Utils.SERVER%>payment/SendDeposit.jsp" />
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