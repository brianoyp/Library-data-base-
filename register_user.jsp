<%@ page language="java" import="cs5530.*" %>
<html>
<head>
<script>
function check_all_fields(){

	if( document.form1.username.value.trim() == ""){
		alert("Username is empty");
		document.form1.username.focus();
		return false;
	}
	else if( document.form1.fullName.value.trim() == ""){
		alert("fullName is empty");
		document.form1.fullName.focus();
		return false;
	}
	else if( document.form1.address.value.trim() == ""){
		alert("address is empty");
		document.form1.address.focus();
		return false;
	}
	else if( document.form1.phone.value.trim() == ""){
		alert("phone is empty");
		document.form1.phone.focus();
		return false;
	}
	else if( document.form1.email.value.trim() == ""){
		alert("email is empty");
		document.form1.email.focus();
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


	<form name="form1" method="get" onsubmit="return check_all_fields()" action="register_user.jsp">
   <h1>Register New User</h1>
      <table border="1">
        <tr>
		  <td>Username</td>
		  <td><input type="text" name="username" length="10" required></td>
        </tr>
         <tr>
		  <td>Name</td>
		  <td><input type="text" name="fullName" length="10" required></td>
        </tr>
        <tr>
		  <td>Address</td>
		  <td><input type="text" name="address" length="10" required></td>
        </tr>
        <tr>
		  <td>Phone</td>
		  <td><input type="text" name="phone" length="10" required></td>
        </tr>
        <tr>
		  <td>Email</td>
		  <td><input type="text" name="email" length="10" required></td>
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
	String fullName = request.getParameter("fullName").trim();
	String address = request.getParameter("address").trim();
	String phone = request.getParameter("phone").trim();
	String email = request.getParameter("email").trim();
	cs5530.Connector connector = new Connector();
	cs5530.LibraryUtilities libraryUtilities = new LibraryUtilities();
	String result = libraryUtilities.registerUser(username,fullName,address,phone,email,connector.con);
%>  
  <p><%=result%></p> <BR>
  <a href="register_user.jsp"> Register other user </a> <BR><BR>
<%
 connector.closeStatement();
 connector.closeConnection();
} 
%>
<a href="index.html"> Back </a>

</body>
