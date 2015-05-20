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
	else if( document.form1.bookID.value.trim() == ""){
		alert("bookID is empty");
		document.form1.bookID.focus();
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
	<form name="form1" method="get" onsubmit="return check_all_fields()" action="check_out_book.jsp">
		<h1>Check Out</h1>
   		<table border="1">
   			<tr>
				<td>cardID</td>
				<td><input type="text" name="cardID" length="10" required></td>
			</tr>
			<tr>
				<td>bookID</td>
				<td><input type="text" name="bookID" length="10" required></td>
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
	String bookID = request.getParameter("bookID").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.checkout(cardID, bookID, connector.con);
%>  
  	<p><%=result%></p> <BR>
  	<a href="check_out_book.jsp"> Checkout other books </a> <BR><BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
