<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){

	if( document.form1.ISBN.value.trim() == ""){
		alert("ISBN is empty");
		document.form1.ISBN.focus();
		return false;
	}
	else if( document.form1.title.value.trim() == ""){
		alert("title is empty");
		document.form1.title.focus();
		return false;
	}
	else if( document.form1.publisher.value.trim() == ""){
		alert("publisher is empty");
		document.form1.publisher.focus();
		return false;
	}
	else if( document.form1.yearOfPublish.value.trim() == ""){
		alert("yearOfPublish is empty");
		document.form1.yearOfPublish.focus();
		return false;
	}
	else if( document.form1.format.value.trim() == ""){
		alert("format is empty");
		document.form1.format.focus();
		return false;
	}
	else if( document.form1.subject.value.trim() == ""){
		alert("subject is empty");
		document.form1.subject.focus();
		return false;
	}
	else if( document.form1.summary.value.trim() == ""){
		alert("summary is empty");
		document.form1.summary.focus();
		return false;
	}
	else if( document.form1.authorString.value.trim() == ""){
		alert("authorString is empty");
		document.form1.authorString.focus();
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


	<form name="form1" method="get" onsubmit="return check_all_fields()" action="add_book.jsp">
   <h1>Add New Book</h1>
      <table border="1">
        <tr>
		  <td>ISBN</td>
		  <td><input type="text" name="ISBN" length="10" required></td>
        </tr>
        <tr>
		  <td>Title</td>
		  <td><input type="text" name="title" length="10" required></td>
        </tr>
        <tr>
		  <td>Publisher</td>
		  <td><input type="text" name="publisher" length="10" required></td>
        </tr>
        <tr>
		  <td>Year of Publish</td>
		  <td><input type="text" name="yearOfPublish" length="10" required></td>
        </tr>
        <tr>
		  <td>Format</td>
		  <td><input type="text" name="format" length="10" required></td>
        </tr>
        <tr>
		  <td>Subject</td>
		  <td><input type="text" name="subject" length="10" required></td>
        </tr>
        <tr>
		  <td>Summary</td>
		  <td><input type="text" name="summary" length="10" required></td>
        </tr>
        <tr>
		  <td>Author(s)</td>
		  <td><input type="text" name="authorString" length="10" required></td>
        </tr>
        <tr>
	 		<td colspan="2" style="text-align:center;"><input type="submit" name="submit" value="submit"></td>
        </tr>
      </table>
      <h3>Please enter your information</h3>
    </form> 
<%

} else {
	String ISBN = request.getParameter("ISBN").trim();
	String title = request.getParameter("title").trim();
	String publisher = request.getParameter("publisher").trim();
	String yearOfPublish = request.getParameter("yearOfPublish").trim();
	String format = request.getParameter("format").trim();
	String subject = request.getParameter("subject").trim();
	String summary = request.getParameter("summary").trim();
	String authorString = request.getParameter("authorString").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.addNewBook(ISBN,title,publisher,yearOfPublish,format,subject,summary,authorString,connector.con);
%>  
  <p><%=result%></p> <BR>
  <a href="add_book.jsp"> Add other new book </a> <BR><BR>
<%
 connector.closeStatement();
 connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
