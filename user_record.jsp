<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){
	if( document.form1.username.value.trim() == ""){
		alert("username is empty");
		document.form1.username.focus();
		return false;
	
	} else
		return true;
}
</script> 
</head>
<body>

<%
if( request.getParameter("submit") == null ){
%>
	<form name="form1" method="get" onsubmit="return check_all_fields()" action="user_record.jsp">
		<h1>User Record</h1>
   		<table border="1">
   			<tr>
				<td>username</td>
				<td><input type="text" name="username" length="10" required></td>
			</tr>
			<tr>
		 		<td colspan="2" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
	        </tr>
      	</table>
      	<h3>Please enter your information</h3>
    </form> 
<%

} else {
	String username = request.getParameter("username").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.getUserRecord(username, connector.con);
%>  
  	<p><%=result%></p> <BR>
  	<a href="user_record.jsp"> Get Other Users Record </a> <BR><BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
