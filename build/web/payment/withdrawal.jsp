<%-- 
    Document   : withdrawal
    Created on : Apr 1, 2013, 2:57:40 PM
    Author     : Kent
--%>

<%@page import="kent.database.DatabaseHandler"%>
<%@page import="kent.utils.JsonReader"%>
<%@page import="org.json.JSONObject"%>
<%@page import="kent.Utils"%>
<%@page import="kent.component.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Withdrawal</title>
    </head>
    <body>
        <h1>Withdrawal!</h1>
        <%
            User currentUser = null;
            currentUser = (User) session.getAttribute("user");
            if (session != null && currentUser != null) {
        %>
        Hello <b><% out.print(currentUser.getUsername());%></b>
        --- <a href="<%=Utils.SERVER_URL%>?type_of_request=sign_out">Sign out</a><br />
        Your current balance:<b> <% out.print(currentUser.getCurrentBalance());%> </b><br />
        Your bitcoin address:<b> <% out.print(currentUser.getBitcoinId());%> </b>
        --- <a href="<%=Utils.SERVER%>payment/UpdateBitcoinAddress.jsp">Change bitcoin address</a><br />
        Enter balance you want to withdraw below:<br />
        <form name="" action="" method="post">
            <input type="hidden" name="type_of_request" value="update_bitcoin_id" />
            <input type="text" name="amount_withdraw" value="0" />
            <input type="submit" value=" Withdraw bitcoin " />
        </form>

        <%
            String strAmountWithdraw = request.getParameter("amount_withdraw");

            double amountWithdraw = 0;
            try {
                amountWithdraw = (strAmountWithdraw != null)
                        ? Double.parseDouble(request.getParameter("amount_withdraw")) : 0;
            } catch (NumberFormatException nFE) {
                System.out.println("Amount withdrawal is not an Double");
            }

            if (amountWithdraw > 0.0 || amountWithdraw <= currentUser.getCurrentBalance()) {                
                // Amount valid. Send preparation withdrawal.
                String getDetailUrl = "https://www.bitcoin247.com/MerchantApi/v1/json/PrepareSendBitcoinsTransaction";
                getDetailUrl += "?merchantid=" + Utils.MERCHANT_ID;
                getDetailUrl += "&password=" + Utils.MERCHANT_PASS;
                getDetailUrl += "&amount=" + amountWithdraw;
                getDetailUrl += "&bitcoinaddress=" + currentUser.getBitcoinId();

                try {
                    

                    JSONObject jsonResult = JsonReader.readJsonFromUrl(getDetailUrl);
                    //out.print(jsonResult.toString());
                    int re_ErrorCode = jsonResult.getInt("ErrorCode");
                    int re_SendBitcoinsTransactionId = jsonResult.getInt("SendBitcoinsTransactionId");

                    // Calculate USD(Zenny) to Bitcoin
                    // Read price exchange from Mtgox 
                    JSONObject mtGoxExchange = JsonReader.readJsonFromUrl("http://data.mtgox.com/api/1/BTCUSD/ticker");
                    JSONObject mtGoxReturn = mtGoxExchange.getJSONObject("return");
                    JSONObject mtGoxReturnSell = mtGoxReturn.getJSONObject("sell");
                    double mtGoxReturnSellValue = mtGoxReturnSell.getDouble("value");

                    double totalWithdrawBitcoin = amountWithdraw / mtGoxReturnSellValue;
                    
                    // Execute Withdraw
                    String ExecuteWithdraw = "https://www.bitcoin247.com/MerchantApi/v1/json/ExecuteSendBitcoinsTransaction";
                    ExecuteWithdraw += "?merchantid=" + Utils.MERCHANT_ID;
                    ExecuteWithdraw += "&password=" + Utils.MERCHANT_PASS;
                    ExecuteWithdraw += "&sendbitcoinstransactionid=" + re_SendBitcoinsTransactionId;
                    
                    JSONObject executeWithdrawResult = JsonReader.readJsonFromUrl(ExecuteWithdraw);
                    int re_ExecuteErrorCode = executeWithdrawResult.getInt("ErrorCode");
                    
                    if (re_ExecuteErrorCode == 0) {
                        // Get user
                        User u = new User();
                        u.setUserId(currentUser.getUserId());
                        double currentBalance = u.getCurrentBalance();
                        double newBalance = currentBalance - amountWithdraw;
                        // Update user balance
                        u.updateBalance(newBalance);

                        // Insert payment history
                        int insertedId = DatabaseHandler.getInstance().executeSQLAndGetId(
                                "kb_user_payment",
                                "user_payment_id",
                                "USER_PAYMENT_INSERT",
                                new String[]{"userId", "typeOfPayment", "dateOfPayment", "balanceBefore", "balanceAfter"},
                                new Object[]{currentUser.getUserId(), 2, System.currentTimeMillis() / 1000, currentBalance, newBalance});
                        out.print("Withdrawall successfully.");
                    } else {
                        out.print("There are errors. Please try again.");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                out.print("Amount invalid!");
            }

        } else {
        %>
        You are not sign in yet<br />
        Please sign in first:
        <form method="post" action="<%=Utils.SERVER_URL%>">
            <input type="hidden" name="backurl" value="<%=Utils.SERVER%>payment/withdrawal.jsp" />
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
