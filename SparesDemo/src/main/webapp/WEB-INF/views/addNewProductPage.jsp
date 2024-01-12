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
			background-color: #f5f7f9;
			margin: 0;
			padding: 20px;
		}

		.container {
			background-color: #fff;
			padding: 20px;
			border-radius: 5px;
			box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
		}

		table {
			width: 100%;
			border-collapse: collapse;
		}

		th, td {
			padding: 10px;
			border-bottom: 1px solid #e2e8f0;
		}

		th {
			background-color: #f0f0f0;
		}

		tr:hover {
			background-color: #eaeaea;
		}

		a {
			color: #007BFF;
			text-decoration: none;
			transition: color 0.2s;
		}

		a:hover {
			color: #0056b3;
		}

		input[type="submit"] {
			background-color: #007BFF;
			color: #fff;
			padding: 10px 20px;
			border: none;
			border-radius: 3px;
			cursor: pointer;
			transition: background-color 0.2s;
		}

		input[type="submit"]:hover {
			background-color: #0056b3;
		}

		.message {
			color: blue;
			margin-top: 20px;
		}
	</style>
</head>
<body>
	<div class="container">
		<c:url var="addAction" value="/addNewC" ></c:url>

		<form:form action="${addAction}" commandName="product">
		<table>
			<c:if test="${!empty product.name}">
	<tr>
		<td>
			<form:label path="id">
				<spring:message text="ID"/>
			</form:label>
		</td>
		<td>
			<form:input path="id" readonly="true" size="8"  disabled="true" />
			<form:hidden path="id" />
		</td> 
	</tr>
	</c:if>
	<tr>
		<td>
			<form:label path="name">
				<spring:message text="Name"/>
			</form:label>
		</td>
		<td>
			<form:input path="name" />
		</td> 
	</tr>
	<tr>
		<td>
			<form:label path="price">
				<spring:message text="Price"/>
			</form:label>
		</td>
		<td>
			<form:input path="price" />
		</td>
	</tr>
	<tr>
		<td>
			<form:label path="quantity">
				<spring:message text="Quantity"/>
			</form:label>
		</td>
		<td>
			<form:input path="quantity" />
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<c:if test="${!empty product.name}">
				<input type="submit"
					value="<spring:message text="Edit Product"/>" />
			</c:if>
			<c:if test="${empty product.name}">
				<input type="submit"
					value="<spring:message text="Add Product"/>" />
			</c:if>
		</td>
	</tr>

		</table>	
		</form:form>

		<c:if test="${not empty addMessage}">
			<div class="message">${addMessage}</div>
		</c:if>

		<h1>Product List</h1>
		
		<c:if test="${!empty listProducts}">
	<table class="tg">
	<tr>
		<th width="80">Product ID</th>
		<th width="120">Product Name</th>
		<th width="120">Product Price</th>
		<th width="120">Product Quantity</th>
		<th width="60">Edit</th>
		<th width="60">Delete</th>
	</tr>
	<c:forEach items="${listProducts}" var="product">
		<tr>
			<td>${product.id}</td>
			<td>${product.name}</td>
			<td>${product.price}</td>
			<td>${product.quantity}</td>
			<td><a href="<c:url value='/editC/${product.id}' />" >Edit</a></td>
			<td><a href="<c:url value='/removeC/${product.id}' />" >Delete</a></td>
		</tr>
	</c:forEach>
	</table>
</c:if>

	</div>
	
	<br><br>
	<a href="/SparesDemo/">Logout</a>
</body>
</html>
