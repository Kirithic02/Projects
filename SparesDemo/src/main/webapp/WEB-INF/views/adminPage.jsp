<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome Admin</title>       
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f7f7f7;
            margin: 0;
            padding: 20px;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px 40px;
            box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        h2 {
            color: #333;
            border-bottom: 1px solid #e7e7e7;
            padding-bottom: 10px;
        }

        h3 {
            margin-top: 30px;
            color: #555;
        }

        ul {
            list-style-type: none;
            padding-left: 0;
        }

        ul li {
            padding: 8px 0;
            font-size: 16px;
        }

        form {
            margin: 20px 0;
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
        <h2>Welcome to the Admin Page</h2>
        
        
        <ul>
            <li>Hi ${admin.username} Click The Buttons To Do The Actions</li>
        </ul>
        
        
        <form action="adminShowProductC" method = "post">
          Show Products       <input type = "submit" value = "Click">
        </form> 
        
        <form action="adminShowCartItemsC" method = "post">
          Show Cart Items    <input type = "submit" value = "Click">
        </form>
      
        <a href="/SparesDemo/">Logout</a>
    </div>
</body>
</html>
