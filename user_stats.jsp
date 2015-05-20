<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){
	if( document.form1.n.value.trim() == ""){
		alert("n is empty");
		document.form1.n.focus();
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
	<form name="form1" method="get" onsubmit="return check_all_fields()" action="user_stats.jsp">
		<h1>User Statistics</h1>
   		<table border="1">
   			<tr>
				<td>Enter 'N'</td>
				<td><input type="text" name="n" length="10" required></td>
			</tr>
			<tr>
		 		<td colspan="2" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
	        </tr>
      	</table>
      	<h3>Please enter your information</h3>
    </form> 
<%

} else {
	String n = request.getParameter("n").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.getUserStatistics(n, connector.con);
%>  
  	<p><%=result%></p> <BR>
  	<a href="user_stats.jsp"> Get Other User Stats </a> <BR><BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
