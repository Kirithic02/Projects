<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
Welcome 

	<form action="addNew" method="post">
		<label for="name">Name:</label> 
		<input type="text" id="name" name="name" required><br> <br>
		<label for="price">Price:</label> 
		<input type="number" id="price" name="price" required><br> <br> 
		<label for="quantity">Quantity:</label> 
		<input type="number" id="quantity" name="quantity" required><br> <br>
		<input type="submit" value="Add">
	</form>
	
	
	 <c:if test="${not empty addMessage}">
        <div style="color: blue;">
            ${addMessage}
        </div>
    </c:if>
    
    
</body>
</html>