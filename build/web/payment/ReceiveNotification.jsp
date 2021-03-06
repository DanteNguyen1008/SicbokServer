<%-- 
    Document   : ReceiveNotification
    Created on : Mar 29, 2013, 11:13:36 AM
    Author     : Kent
--%>

<%@page import="kent.database.DatabaseHandler"%>
<%@page import="kent.component.User"%>
<%@page import="org.json.JSONObject"%>
<%@page import="kent.utils.JsonReader"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Receive deposit notification</title>
    </head>
    <body>
        <h1>Receive deposit notification</h1>
        <%

            String Bitcoin247PaymentId = request.getParameter("Bitcoin247PaymentId");
            String MerchantPaymentId = request.getParameter("MerchantPaymentId");
            String ExpectedAmount = request.getParameter("ExpectedAmount");
            String TotalAmountReceived = request.getParameter("TotalAmountReceived");
            String PaymentStatus = request.getParameter("PaymentStatus");
            String BitcoinReceiveAddress = request.getParameter("BitcoinReceiveAddress");
            String Note = request.getParameter("Note");
            String TsCreate = request.getParameter("TsCreate");

            //Bitcoin247PaymentId = "8027";
            String getDetailUrl = "https://www.bitcoin247.com/MerchantApi/v1/json/GetPaymentDetailsByBitcoin247Id";
            getDetailUrl += "?merchantid=11741&password=T@mH@i169";
            getDetailUrl += "&bitcoin247paymentid=" + Bitcoin247PaymentId;


            try {

                JSONObject jsonResult = JsonReader.readJsonFromUrl(getDetailUrl);
                //out.print(jsonResult.toString());
                int re_ErrorCode = jsonResult.getInt("ErrorCode");

                //String re_Bitcoin247PaymentId = (String) jsonResult.get("Bitcoin247PaymentId");
                //String re_MerchantPaymentId = (String) jsonResult.get("MerchantPaymentId");
                double re_TotalAmountReceived = jsonResult.getDouble("TotalAmountReceived");
                int userId = jsonResult.getInt("Note");

                // Calculate Bicoin to USD (Zenny)
                // Read price exchange from Mtgox 
                JSONObject mtGoxExchange = JsonReader.readJsonFromUrl("http://data.mtgox.com/api/1/BTCUSD/ticker");
                JSONObject mtGoxReturn = mtGoxExchange.getJSONObject("return");
                JSONObject mtGoxReturnSell = mtGoxReturn.getJSONObject("sell");
                double mtGoxReturnSellValue = mtGoxReturnSell.getDouble("value");

                double totalDepositUSD = mtGoxReturnSellValue * re_TotalAmountReceived;

                // Get user
                User u = new User();
                u.setUserId(userId);
                double currentBalance = u.getCurrentBalance();
                double newBalance = 0;
                if (u.isIsTrial()) {
                    newBalance = totalDepositUSD;
                } else {
                    newBalance = currentBalance + totalDepositUSD;
                }
                // Update user balance
                u.updateBalance(newBalance);

                // Insert payment history
                int insertedId = DatabaseHandler.getInstance().executeSQLAndGetId(
                        "kb_user_payment",
                        "user_payment_id",
                        "USER_PAYMENT_INSERT",
                        new String[]{"userId", "typeOfPayment", "dateOfPayment", "balanceBefore", "balanceAfter"},
                        new Object[]{userId, 1, System.currentTimeMillis() / 1000, currentBalance, newBalance});

            } catch (Exception e) {
                e.printStackTrace();
            }

        %>
    </body>
</html>
