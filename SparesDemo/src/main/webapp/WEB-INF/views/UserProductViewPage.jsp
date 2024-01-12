<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Product & User Page</title>
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
            overflow: hidden;
        }

        h1, h3 {
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

        input[type="submit"], a {
            background-color: #007BFF;
            color: #fff;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            transition: background-color 0.2s;
        }

        input[type="submit"]:hover, a:hover {
            background-color: #0056b3;
        }

        .message, p {
            margin-top: 20px;
            font-size: 16px;
            color: #34495e;
        }

        .left, .right {
            padding: 20px;
        }

        .left {
            border-right: 1px solid #eaecef;
        }

        ul {
            list-style-type: none;
            padding: 0;
        }

        ul li {
            padding: 5px 0;
            font-size: 16px;
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
        <div class="left">
            <ul>
                <li>Hi ${user.username} Our Products Shown Below</li>
            </ul>
            
            <h1>Product List</h1>
            	
            	<c:if test="${not empty cartMessage}">
							<div style="color: blue;">${cartMessage}</div>
					</c:if>
			
			<c:if test="${!empty listProducts}">
			<table class="tg">
			<tr>
				<th width="80">Product ID</th>
				<th width="120">Product Name</th>
				<th width="120">Product Price</th>
				<th width="120">Product Quantity</th>
				<th width="60">Add To Cart</th>
		<!--    <th width="60">Delete</th>  -->
			</tr>
			<c:forEach items="${listProducts}" var="product">
				<tr>
					<td>${product.id}</td>
					<td>${product.name}</td>
					<td>${product.price}</td>
					<form action="<c:url value='/addToCartC/${product.id}/${user.id}' />" method="post">
		            <td><input type="number" name="quantity" min="1" max="${product.quantity}" required></td>
		            <td><input type="submit" value="Add to cart"></td>
		        </form>
					
				</tr>
			</c:forEach>
			</table>
		</c:if>
        </div>
        <a href="/SparesDemo/">Logout</a>
    </div>
</body>
</html>
