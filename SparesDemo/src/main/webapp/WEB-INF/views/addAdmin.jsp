
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
Welcome

	<form action="addNew" method="post">
		<label for="username">Username:</label> <input type="text"
			id="username" name="username" required><br>
		<br> <label for="password">Password:</label> <input
			type="password" id="password" name="password" required><br>
		<br> <input type="submit" value="Add Admin">
	</form>
	
	 <c:if test="${not empty addMessage}">
        <div style="color: blue;">
            ${addMessage}
        </div>
    </c:if>
	
</body>
</html>