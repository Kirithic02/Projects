<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
<head>
	<title>Product Page</title>
	<style type="text/css">
		body {
			font-family: 'Arial', sans-serif;
			background-color: #fafafa;
			margin: 0;
			padding: 50px;
			color: #333;
		}

		.container {
			max-width: 600px;
			margin: 0 auto;
			background-color: #fff;
			padding: 40px;
			border-radius: 8px;
			box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.1);
		}

		h2, h3 {
			margin: 0 0 20px;
			color: #444;
		}

		ul {
			list-style-type: none;
			padding: 0;
			margin: 0 0 30px;
		}

		ul li {
			padding: 5px 0;
			font-size: 16px;
		}

		input[type="submit"] {
			background-color: #007BFF;
			color: #fff;
			padding: 10px 20px;
			border: none;
			border-radius: 4px;
			cursor: pointer;
			transition: background-color 0.2s;
			font-size: 16px;
		}

		input[type="submit"]:hover {
			background-color: #0056b3;
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
		<h2>Welcome to the Spares Parts Shop</h2>
    
	    
	    <ul>
	        <li>Hi ${user.username}</li>
	    </ul>
	    
	    <form action="userShowProductC/${user.id}" method="post">
			Do You Want To View Our Products  <input type="submit" value="Click Here">
		</form>
		<a href="/SparesDemo/">Logout</a>
	</div>
</body>
</html>
