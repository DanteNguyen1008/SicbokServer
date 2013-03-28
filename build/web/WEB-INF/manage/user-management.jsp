<%-- 
    Document   : user-management
    Created on : Mar 28, 2013, 2:13:08 PM
    Author     : Admin
--%>
<%@page import="java.sql.ResultSet"%>
<%@page import="kent.component.Management"%>
<%@page import="kent.Utils"%>
<%
    Management admin = null;
    if (session.getAttribute(Utils.ADMIN_SESSION_NAME) == null) {
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", Utils.SERVER + "manage/login.jsp");
    } else {
        admin = (Management) session.getAttribute(Utils.ADMIN_SESSION_NAME);
    }
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User management page</title>
    </head>
    <body>
        <%
            if (admin != null) {
        %>
        <table>
            <tr>
                <td>User name</td>
                <td>Email</td>
                <td>Full name</td>
                <td>Bitcoin id</td>
                <td>Active</td>
                <td>Enable</td>
            </tr>
            <%
                ResultSet rs = admin.loadUserList();
                while (rs.next()) {
            %>
            <tr>
                <td><%=rs.getString("username")%></td>
                <td><%=rs.getString("email")%></td>
                <td><%=rs.getString("fullname")%></td>
                <td><%=rs.getString("bitcoin_id")%></td>
                <td>
                    <%
                        if (rs.getBoolean("is_active")) {
                            out.println("<a href='" + Utils.SERVER_URL_ADMIN
                                    + "?request_name=deactive_user&user_id="
                                    + rs.getInt("user_id") + "'>Deactive</a>");
                        } else {
                            out.println("<a href='" + Utils.SERVER_URL_ADMIN
                                    + "?request_name=active_user&user_id="
                                    + rs.getInt("user_id") + "'>Active</a>");
                        }
                    %>
                </td>
                <td>
                    <%

                        if (rs.getBoolean("delete_flag")) {
                            out.println("<a href='" + Utils.SERVER_URL_ADMIN
                                    + "?request_name=enable_user&user_id="
                                    + rs.getInt("user_id") + "'>Enable</a>");
                        } else {
                            out.println("<a href='" + Utils.SERVER_URL_ADMIN
                                    + "?request_name=delete_user&user_id="
                                    + rs.getInt("user_id") + "'>Delete</a>");
                        }
                    %>
                </td>
            </tr>
            <%
                }
            %>
        </table>
        <%
                if (request.getParameter("error") != null) {
                    out.println("<div style='color:red'>" + request.getParameter("error") + "</div>");
                }
            }
        %>


    </body>
</html>
