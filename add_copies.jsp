<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){
	if( document.form1.location.value.trim() == ""){
		alert("location is empty");
		document.form1.location.focus();
		return false;
	
	}
	else if( document.form1.ISBN.value.trim() == ""){
		alert("ISBN is empty");
		document.form1.ISBN.focus();
		return false;
	}
	else
		return true;
}
</script> 
</head>
<body>

<%
if( request.getParameter("submit") == null ){
%>
	<form name="form1" method="get" onsubmit="return check_all_fields()" action="add_copies.jsp">
		<h1>Add More Copy</h1>
   		<table border="1">
   			<tr>
				<td>ISBN</td>
				<td><input type="text" name="ISBN" length="10" required></td>
			</tr>
   			<tr>
				<td>Location</td>
				<td><input type="text" name="location" length="10" required></td>
			</tr>
			<tr>
		 		<td colspan="2" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
	        </tr>
      	</table>
      	<h3>Please enter your information</h3>
    </form> 
<%

} else {
	String location = request.getParameter("location").trim();
	String ISBN = request.getParameter("ISBN").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.addNewCopy(ISBN, location, connector.con);
%>  
  	<p><%=result%></p> <BR>
  	<a href="add_copies.jsp"> Add other copy </a> <BR><BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
