<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){

	if( document.form1.name.value.trim() == "" && document.form1.publisher.value.trim() == "" 
		&& document.form1.title.value.trim() == "" && document.form1.subject.value.trim() == ""){
		alert("All fields are empty. Please fill out at least one of the fields.");
		document.form1.name.focus();
		return false;
	}else
		return true;
}
</script> 
</head>
<body>

<%
if( request.getParameter("submit") == null ){
%>


	<form name="form1" method="get" onsubmit="return check_all_fields()" action="book_browsing.jsp">
   <h1>Book Browsing</h1>
      <table border="1">
        <tr>
		  <td colspan="1">Author</td>
		  <td colspan="2"><input type="text" name="name" length="10" required></td>
        </tr>
        <tr>
		  <td colspan="1">Publisher</td>
		  <td colspan="2"><input type="text" name="publisher" length="10" required></td>
        </tr>
        <tr>
		  <td colspan="1">Title</td>
		  <td colspan="2"><input type="text" name="title" length="10" required></td>
        </tr>
        <tr>
		  <td colspan="1">Subject</td>
		  <td colspan="2"><input type="text" name="subject" length="10" required></td>
        </tr>
        <tr>
        	<td colspan="3">Choose Order By</td>
        </tr>
        <tr>
        	<td>Year published<input type="radio" name="orderBy" value="1" required checked></td>
        	<td>Reviews score<input type="radio" name="orderBy" value="2" required></td>
        	<td>Popularity<input type="radio" name="orderBy" value="3" required></td>
        </tr>
        <tr>
        	<td colspan="3">Show only available book</td>
        </tr>
        <tr>
        	<td>Yes<input type="radio" name="availableBooksOnly" value="yes" required checked></td>
        	<td>No<input type="radio" name="availableBooksOnly" value="no" required></td>
        	<td></td>
        </tr>
        <tr>
	 		<td colspan="3" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
        </tr>
      </table>
      <h3>Please enter your information</h3>
    </form> 
<%

} else {
	String name = request.getParameter("name").trim();
	String publisher = request.getParameter("publisher").trim();
	String title = request.getParameter("title").trim();
	String subject = request.getParameter("subject").trim();
	String orderBy = request.getParameter("orderBy").trim();
	String availableBooksOnly = request.getParameter("availableBooksOnly").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.bookBrowsing(name,publisher,title,subject,orderBy,availableBooksOnly,connector.con);
%>  
  <p><%=result%></p> <BR>
  <a href="book_browsing.jsp"> Search Other Books </a> <BR><BR>
<%
 connector.closeStatement();
 connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
