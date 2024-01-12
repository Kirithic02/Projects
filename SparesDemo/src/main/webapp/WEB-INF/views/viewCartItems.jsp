<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Cart View</title>
    <style type="text/css">
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fc;
            margin: 0;
            padding: 40px;
            color: #2c3e50;
        }

        .container {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            padding: 40px;
        }

        h1 {
            margin: 0 0 20px;
            color: #2c3e50;
            border-bottom: 2px solid #eaecef;
            padding-bottom: 10px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }

        th, td {
            padding: 10px 20px;
            border-bottom: 1px solid #eaecef;
        }

        th {
            background-color: #f8f9fc;
        }

        tr:hover {
            background-color: #eaecef;
        }

        .message {
            margin-top: 20px;
            font-size: 16px;
            color: #34495e;
        }
        
         input[type="submit"]:hover {
            background-color: #0056b3;
        }

        a {
            color: #007BFF;
            text-decoration: none;
            padding: 10px;
            border-radius: 3px;
            background-color: #f7f7f7;
            display: inline-block;
            margin-top: 20px;
            transition: background-color 0.2s;
        }

        a:hover {
            background-color: #e7e7e7;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Admin Cart View</h1>
        
        <c:choose>
            <c:when test="${empty cartItems}">
                <p>No items in the cart.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <th>Product Id</th>
                        <th>CartId</th>
                        <th>Quantity</th>
                        <!-- You can add more columns as required -->
                    </tr>
                    <c:forEach items="${cartItems}" var="item">
                        <tr>
                            <td>${item.productId}</td>
                            <td>${item.cartId}</td>
                            <td>${item.itemQuantity}</td>     
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
		<a href="/SparesDemo/">Logout</a>
    </div>
</body>
</html>
