<%@ page language="java" import="cs5530.*" %>
<html>
<head>
</head>
<body>
	<h1>Late Book List</h1>
<%
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.getLateBookList(connector.con);
%>  
  	<p><%=result%></p> <BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
%>
<a href="index.html"> Back </a>

</body>
