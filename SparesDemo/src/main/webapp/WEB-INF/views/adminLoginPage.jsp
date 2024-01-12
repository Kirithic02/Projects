<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Login</title>
    <style type="text/css">
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f7f7f7;
            margin: 0;
            padding: 0;
        }

        .main {
            width: 100%;
            max-width: 1200px;
            margin: 50px auto;
            padding: 20px;
            box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.1);
            background-color: #fff;
            border-radius: 5px;
        }

        .left, .right {
            width: 48%; 
            padding: 20px;
            box-sizing: border-box;
        }

        .left {
            float: left;
            border-right: 1px solid #e7e7e7;
        }

        .right {
            float: right;
        }

        h2 {
            color: #333;
            border-bottom: 2px solid #e7e7e7;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
        }

        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #e7e7e7;
            border-radius: 3px;
            font-size: 14px;
            box-sizing: border-box;
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

        .error {
            background-color: #ffdddd;
            padding: 10px;
            border-radius: 3px;
            margin-top: 10px;
        }

    </style>
</head>
<body>
    <div class="main">
        <div class="left">
            <h2>Admin Login</h2>
            <form action="adminPageC" method="post">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required><br><br>
                
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required><br><br>
                
                <input type="submit" value="Login">
            </form>
            
            <c:if test="${not empty adminerror}">
                <div class="error">
                    ${adminerror}
                </div>
            </c:if>
        </div>
        
        <div class="right">
            <h2>User Login</h2>
            <form action="userPageC" method="post">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required><br><br>
                
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required><br><br>
                
                <input type="submit" value="Login">
            </form>
            
            <c:if test="${not empty usererror}">
                <div class="error">
                    ${usererror}
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
