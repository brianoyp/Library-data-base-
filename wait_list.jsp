<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){
	if( document.form1.cardID.value.trim() == ""){
		alert("cardID is empty");
		document.form1.cardID.focus();
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
	<form name="form1" method="get" onsubmit="return check_all_fields()" action="wait_list.jsp">
		<h1>Add Wait List</h1>
   		<table border="1">
   			<tr>
				<td>cardID</td>
				<td><input type="text" name="cardID" length="10" required></td>
			</tr>
			<tr>
				<td>ISBN</td>
				<td><input type="text" name="ISBN" length="10" required></td>
			</tr>
			<tr>
		 		<td colspan="2" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
	        </tr>
      	</table>
      	<h3>Please enter your information</h3>
    </form> 
<%

} else {
	String cardID = request.getParameter("cardID").trim();
	String ISBN = request.getParameter("ISBN").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.addWaitList(ISBN, cardID, connector.con);
%>  
  	<p><%=result%></p> <BR>
  	<a href="wait_list.jsp"> Add Other Books on Waiting List </a> <BR><BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
