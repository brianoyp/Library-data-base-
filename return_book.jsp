<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){
	if( document.form1.bookID.value.trim() == ""){
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
	<form name="form1" method="get" onsubmit="return check_all_fields()" action="return_book.jsp">
		<h1>Return Book</h1>
   		<table border="1">
			<tr>
				<td>bookID</td>
				<td><input type="text" name="bookID" length="10" required></td>
			</tr>
	        <tr>
	        	<td colspan="2">Return<input type="radio" name="returnType" value="return" required checked></td>
	        </tr>
	        <tr>
	        	<td colspan="2">Lost<input type="radio" name="returnType" value="lost" required></td>
	        </tr>
	        <tr>
		 		<td colspan="2" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
	        </tr>
      	</table>
      	<h3>Please enter your information</h3>
    </form> 
<%

} else {
	String bookID = request.getParameter("bookID").trim();
	String returnType = request.getParameter("returnType");
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result="";
	if(returnType.equals("return")){
		result = libraryUtilities.returnBook(bookID, connector.con);
	} else if (returnType.equals("lost")){
		result = libraryUtilities.lostBook(bookID, connector.con);
	}
%>  
  	<p><%=result%></p> <BR>
  	<a href="return_book.jsp"> Return other books </a> <BR><BR>
<%
 	connector.closeStatement();
 	connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
