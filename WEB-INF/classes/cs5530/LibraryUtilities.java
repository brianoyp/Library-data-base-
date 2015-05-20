package cs5530;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class LibraryUtilities {

	public LibraryUtilities() {
	}

	public String registerUser(String username, String fullName,
			String address, String phone, String email, Connection con)
			throws Exception {
		try {
			String result = "";

			String queryCheck = "SELECT username From User WHERE username = ?";
			PreparedStatement query_statmentCheck = con
					.prepareStatement(queryCheck);
			query_statmentCheck.setString(1, username);
			ResultSet rsCheck = query_statmentCheck.executeQuery();
			if (rsCheck.next()) {
				return "The username you entered is already taken.";
			}
			rsCheck.close();

			String query = "INSERT User (username, fullName, address, phone, email) values(?, ?, ?, ?, ?)";
			PreparedStatement query_statment = con.prepareStatement(query);

			query_statment.setString(1, username);
			query_statment.setString(2, fullName);
			query_statment.setString(3, address);
			query_statment.setString(4, phone);
			query_statment.setString(5, email);

			query_statment.executeUpdate();

			String query2 = "SELECT LAST_INSERT_ID() as cardID;";
			PreparedStatement query_statment2 = con.prepareStatement(query2);
			ResultSet rs = query_statment2.executeQuery();
			while (rs.next()) {
				result = "Register was successful. Your New cardId is "
						+ rs.getString("cardID");
			}
			rs.close();
			return result;

		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String checkout(String cardID, String bookID, Connection con)
			throws Exception {
		try {
			String result = "";
			String queryCheck = "SELECT availability From Book WHERE bookID = ?";
			PreparedStatement query_statmentCheck = con
					.prepareStatement(queryCheck);
			query_statmentCheck.setString(1, bookID);
			ResultSet rsCheck = query_statmentCheck.executeQuery();
			if (rsCheck.next()) {

				if (rsCheck.getBoolean("availability")) {
					// check waiting list
					String queryCheck2 = "SELECT cardID FROM WaitingList W, Book B WHERE B.bookID = ? AND B.ISBN = W.ISBN";
					PreparedStatement query_statmentCheck2 = con
							.prepareStatement(queryCheck2);
					query_statmentCheck2.setString(1, bookID);
					ResultSet rsCheck2 = query_statmentCheck2.executeQuery();
					boolean goodToGo = true;
					if (rsCheck2.next()) {
						int oldestCardID = rsCheck2.getInt("cardID");
						if (!(oldestCardID + "").equals(cardID))
							goodToGo = false;
					}

					if (goodToGo) {
						String query = "INSERT CheckOut (cardID, bookID, checkOutDate) VALUES (?, ?, CURDATE())";
						PreparedStatement query_statment = con
								.prepareStatement(query);
						query_statment.setString(1, cardID);
						query_statment.setString(2, bookID);
						query_statment.executeUpdate();

						String query2 = "INSERT StillCheckedOut (checkID,dueDate) VALUES (LAST_INSERT_ID(), DATE_ADD(CURDATE(), INTERVAL 30 DAY))";
						PreparedStatement query_statment2 = con
								.prepareStatement(query2);
						query_statment2.executeUpdate();

						String query3 = "DELETE FROM WaitingList WHERE EXISTS (SELECT 1 FROM Book WHERE WaitingList.cardID = ? AND Book.bookID = ? AND Book.ISBN = WaitingList.ISBN) LIMIT 1";
						PreparedStatement query_statment3 = con
								.prepareStatement(query3);
						query_statment3.setString(1, cardID);
						query_statment3.setString(2, bookID);
						query_statment3.executeUpdate();

						String query4 = "UPDATE Book SET availability = false WHERE Book.bookID = ?";
						PreparedStatement query_statment4 = con
								.prepareStatement(query4);
						query_statment4.setString(1, bookID);
						query_statment4.executeUpdate();

						result = "Check-out book completed.";
					} else
						result = "This book is reserved for someone else.";

				} else {
					result = "The book you Entered is not available.";
				}
			} else {
				result = "The book you Entered is not valid.";
			}
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String addWaitList(String ISBN, String cardID, Connection con)
			throws Exception {
		try {
			String result = "";

			String queryCheck2 = "SELECT bookID From Book WHERE availability = 1 AND ISBN = ?";
			PreparedStatement query_statmentCheck2 = con
					.prepareStatement(queryCheck2);
			query_statmentCheck2.setString(1, ISBN);
			ResultSet rsCheck2 = query_statmentCheck2.executeQuery();
			if (rsCheck2.next()) {
				result = "The book you entered is available. There is no need for waiting list";
			}

			else {

				String queryCheck = "SELECT cardID From WaitingList WHERE ISBN = ? AND cardID = ?";
				PreparedStatement query_statmentCheck = con
						.prepareStatement(queryCheck);
				query_statmentCheck.setString(1, ISBN);
				query_statmentCheck.setString(2, cardID);
				ResultSet rsCheck = query_statmentCheck.executeQuery();
				if (rsCheck.next()) {
					result = "You are already in the waiting list for this book.";
				}

				else {

					String query = "INSERT WaitingList (ISBN, cardID, date) VALUES (?, ?, CURDATE())";
					PreparedStatement query_statment = con
							.prepareStatement(query);
					query_statment.setString(1, ISBN);
					query_statment.setString(2, cardID);
					query_statment.executeUpdate();

					String query2 = "UPDATE BookData SET requestedNum = requestedNum+1 WHERE ISBN = ?";
					PreparedStatement query_statment2 = con
							.prepareStatement(query2);
					query_statment2.setString(1, ISBN);
					query_statment2.executeUpdate();
					result = "Wait list Added.";
				}
			}
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String getUserRecord(String username, Connection con)
			throws Exception {
		try {
			String result = "";

			String query = "SELECT fullName, username, address, phone, email FROM User WHERE username = ?";
			PreparedStatement query_statment = con.prepareStatement(query);
			query_statment.setString(1, username);
			ResultSet rs = query_statment.executeQuery();

			while (rs.next()) {
				result = "<table border='1'><tr><td colspan='2' style='text-align:center;'>User Information</td></tr>"
						+ "<tr><td>Name</td><td>"
						+ rs.getString("fullName")
						+ "</td></tr>"
						+ "<tr><td>Username</td><td>"
						+ rs.getString("username")
						+ "</td></tr>"
						+ "<tr><td>Address</td><td>"
						+ rs.getString("address")
						+ "</td></tr>"
						+ "<tr><td>Phone</td><td>"
						+ rs.getString("phone")
						+ "</td></tr>"
						+ "<tr><td>Email</td><td>"
						+ rs.getString("email")
						+ "</td></tr></table>";
			}
			rs.close();
			String query2 = "SELECT B.ISBN, BD.title, C.checkOutDate, C.returnedDate FROM User U, CheckOut C, Book B, BookData BD WHERE U.username = ? AND U.cardID = C.cardID AND C.bookID = B.bookID AND B.ISBN = BD.ISBN";
			PreparedStatement query_statment2 = con.prepareStatement(query2);
			query_statment2.setString(1, username);
			ResultSet rs2 = query_statment2.executeQuery();
			result += "</br><table border='1'><tr><td colspan='6' style='text-align:center;'>Check Out History</td></tr>"
					+ "<tr><td>Title</td><td>ISBN</td><td>Checked Out</td><td>Returned</td></tr>";
			while (rs2.next()) {
				result += "<tr><td>" + rs2.getString("title") + "</td><td>"
						+ rs2.getString("ISBN") + "</td><td>"
						+ rs2.getString("checkOutDate") + "</td><td>"
						+ rs2.getString("returnedDate") + "</td></tr>";
			}
			result += "</table>";
			rs2.close();

			String query3 = "SELECT BD.ISBN, BD.title, B.bookID FROM Lost L, User U, CheckOut C, Book B, BookData BD WHERE U.username = ? AND U.cardID = C.cardID AND L.checkID = C.checkID AND C.bookID = B.bookID AND B.ISBN = BD.ISBN";
			PreparedStatement query_statment3 = con.prepareStatement(query3);
			query_statment3.setString(1, username);
			ResultSet rs3 = query_statment3.executeQuery();
			result += "</br><table border='1'><tr><td colspan='6' style='text-align:center;'>List of books lost by the user</td></tr>"
					+ "<tr><td>Title</td><td>ISBN</td><td>bookID</td></tr>";
			while (rs3.next()) {
				result += "<tr><td>" + rs3.getString("title") + "</td><td>"
						+ rs3.getString("ISBN") + "</td><td>"
						+ rs3.getString("bookID") + "</td></tr>";
			}
			result += "</table>";
			rs3.close();

			String query4 = "SELECT BD.ISBN, BD.title FROM WaitingList W, User U, BookData BD WHERE U.username = ? AND U.cardID = W.cardID AND W.ISBN = BD.ISBN";
			PreparedStatement query_statment4 = con.prepareStatement(query4);
			query_statment4.setString(1, username);
			ResultSet rs4 = query_statment4.executeQuery();

			result += "</br><table border='1'><tr><td colspan='6' style='text-align:center;'>List of books requested</td></tr>"
					+ "<tr><td>Title</td><td>ISBN</td></tr>";
			while (rs4.next()) {
				result += "<tr><td>" + rs4.getString("title") + "</td><td>"
						+ rs4.getString("ISBN") + "</td></tr>";
			}
			result += "</table>";
			rs4.close();

			String query5 = "SELECT BD.ISBN, BD.title, O.score, O.shortText FROM User U, BookData BD, Opinion O WHERE U.username = ? AND U.cardID = O.cardID AND O.ISBN = BD.ISBN";
			PreparedStatement query_statment5 = con.prepareStatement(query5);
			query_statment5.setString(1, username);
			ResultSet rs5 = query_statment5.executeQuery();

			result += "</br><table border='1'><tr><td colspan='6' style='text-align:center;'>List of reviews</td></tr>"
					+ "<tr><td>Title</td><td>ISBN</td><td>Score</td><td>Review</td></tr>";
			while (rs5.next()) {
				result += "<tr><td>" + rs5.getString("title") + "</td><td>"
						+ rs5.getString("ISBN") + "</td><td>"
						+ rs5.getString("score") + "</td><td>"
						+ rs5.getString("shortText") + "</td></tr>";
			}
			result += "</table>";
			rs5.close();

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String addNewBook(String ISBN, String title, String publisher,
			String yearOfPublish, String format, String subject,
			String summary, String authorString, Connection con)
			throws Exception {
		try {
			String result = "";

			String queryCheck = "SELECT ISBN From BookData WHERE ISBN = ?";
			PreparedStatement query_statmentCheck = con
					.prepareStatement(queryCheck);
			query_statmentCheck.setString(1, ISBN);
			ResultSet rsCheck = query_statmentCheck.executeQuery();
			if (rsCheck.next()) {
				result = "There is same ISBN in the system.";
			}

			else {
				List<String> authors = Arrays.asList(authorString.split(","));

				String query = "INSERT BookData (ISBN, title, publisher, yearOfPublish, format, subject, summary)  values(?,?,?,?,?,?,?)";
				PreparedStatement query_statment = con.prepareStatement(query);

				query_statment.setString(1, ISBN);
				query_statment.setString(2, title);
				query_statment.setString(3, publisher);
				query_statment.setString(4, yearOfPublish);
				query_statment.setString(5, format);
				query_statment.setString(6, subject);
				query_statment.setString(7, summary);

				query_statment.executeUpdate();

				for (int i = 0; i < authors.size(); i++) {
					String query2 = "INSERT Author (ISBN, name) VALUES (?,?)";
					PreparedStatement query_statment2 = con
							.prepareStatement(query2);
					query_statment2.setString(1, ISBN);
					query_statment2.setString(2, authors.get(i).trim());
					query_statment2.executeUpdate();
				}
				result = "New book was added.";
			}

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String addNewCopy(String ISBN, String location, Connection con)
			throws Exception {
		try {
			String result = "";

			String query = "INSERT Book (ISBN, location) values(?, ?)";
			PreparedStatement query_statment = con.prepareStatement(query);

			query_statment.setString(1, ISBN);
			query_statment.setString(2, location);

			query_statment.executeUpdate();

			result = "New copy was added.";

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String getLateBookList(Connection con) throws Exception {
		try {
			String result = "";
			Statement stmt = con
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
			String sql = "SELECT BD.title, S.dueDate, U.fullName, U.phone, U.email FROM CheckOut C, StillCheckedOut S, User U, Book B, BookData BD Where S.dueDate < CURDATE() AND S.checkID = C.checkID AND C.bookID = B.bookID AND C.cardID = U.cardID AND B.ISBN = BD.ISBN";
			ResultSet rs = stmt.executeQuery(sql);
			result += "<table border='1'><tr><td colspan='5' style='text-align:center;'>Late Book List</td></tr>"
					+ "<tr><td>Title</td><td>Due Date</td><td>Full Name</td><td>Phone Number</td><td>Email Address</td></tr>";
			while (rs.next()) {
				result += "<tr><td>" + rs.getString("title") + "</td><td>"
						+ rs.getString("dueDate") + "</td><td>"
						+ rs.getString("fullName") + "</td><td>"
						+ rs.getString("phone") + "</td><td>"
						+ rs.getString("email") + "</td></tr>";
			}
			result += "</table>";
			rs.close();

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String addBookReview(String ISBN, String cardID, String score,
			String shortText, Connection con) throws Exception {
		try {
			String result = "";

			String queryCheck = "SELECT cardID From Opinion WHERE ISBN = ? AND cardID = ?";
			PreparedStatement query_statmentCheck = con
					.prepareStatement(queryCheck);
			query_statmentCheck.setString(1, ISBN);
			query_statmentCheck.setString(2, cardID);
			ResultSet rsCheck = query_statmentCheck.executeQuery();
			if (rsCheck.next()) {
				result = "You already entered a review for this book.";
			}

			else {
				String query;
				PreparedStatement query_statment;
				if (shortText.equals("")) {
					query = "INSERT Opinion (cardID, ISBN, date, score) VALUES (?, ?, curdate(), ?)";
					query_statment = con.prepareStatement(query);
					query_statment.setString(1, cardID);
					query_statment.setString(2, ISBN);
					query_statment.setString(3, score);
				} else {
					query = "INSERT Opinion (cardID, ISBN, date, score, shortText) VALUES (?, ?, curdate(), ?, ?)";
					query_statment = con.prepareStatement(query);
					query_statment.setString(1, cardID);
					query_statment.setString(2, ISBN);
					query_statment.setString(3, score);
					query_statment.setString(4, shortText);
				}
				query_statment.executeUpdate();

				result = "Book review was added.";
			}
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String bookBrowsing(String name, String publisher, String title,
			String subject, String orderBy, String availableBooksOnly,
			Connection con) throws Exception {
		try {
			String result = "";

			if (orderBy.equals("1")) {

				String query = "SELECT BD.title, GROUP_CONCAT(A.name SEPARATOR ', ')as author, BD.publisher, BD.yearOfPublish, BD.format, BD.subject, BD.summary, BD.ISBN FROM BookData BD INNER JOIN Author A ON A.ISBN = BD.ISBN WHERE ";
				Boolean isFirst = true;
				if (!name.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "A.name = ? ";
				}

				if (!publisher.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.publisher = ? ";
				}
				if (!title.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.title LIKE ? ";
				}
				if (!subject.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.subject = ? ";
				}
				if (availableBooksOnly == "yes") {
					query = query
							+ "AND BD.ISBN IN (Select BD.ISBN FROM BookData BD, Book B WHERE BD.ISBN = B.ISBN AND B.availability = true) ";
				}
				query = query
						+ "Group BY BD.ISBN ORDER BY BD.yearOfPublish DESC";

				PreparedStatement query_statment = con.prepareStatement(query);

				int i = 1;
				if (!name.equals("")) {
					query_statment.setString(i, name);
					i++;
				}
				if (!publisher.equals("")) {
					query_statment.setString(i, publisher);
					i++;
				}
				if (!title.equals("")) {
					query_statment.setString(i, title);
					i++;
				}
				if (!subject.equals("")) {
					query_statment.setString(i, subject);
					i++;
				}

				ResultSet rs = query_statment.executeQuery();

				result += "<table border='1'><tr><td colspan='8' style='text-align:center;'>Search Result</td></tr>"
						+ "<tr><td>Title</td><td>Author</td><td>Publisher</td><td>Year of Publish</td><td>Format</td><td>Subject</td><td>Summary</td><td>ISBN</td></tr>";
				while (rs.next()) {
					result += "<tr><td>" + rs.getString("title") + "</td><td>"
							+ rs.getString("author") + "</td><td>"
							+ rs.getString("publisher") + "</td><td>"
							+ rs.getString("yearOfPublish") + "</td><td>"
							+ rs.getString("format") + "</td><td>"
							+ rs.getString("subject") + "</td><td>"
							+ rs.getString("summary") + "</td><td>"
							+ rs.getString("ISBN") + "</td><tr>";
				}
				result += "</table>";
				rs.close();
			}

			else if (orderBy.equals("2")) {

				String query = "SELECT BD.title, GROUP_CONCAT(A.name SEPARATOR ', ')as author, BD.publisher, BD.yearOfPublish, BD.format, BD.subject, BD.summary, BD.ISBN, AVG(O.score) as AverageScore FROM BookData BD INNER JOIN Author A ON A.ISBN = BD.ISBN LEFT OUTER JOIN Opinion O ON O.ISBN = BD.ISBN WHERE ";
				Boolean isFirst = true;
				if (!name.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "A.name = ? ";
				}

				if (!publisher.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.publisher = ? ";
				}
				if (!title.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.title LIKE ? ";
				}
				if (!subject.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.subject = ? ";
				}
				if (availableBooksOnly == "yes") {
					query = query
							+ "AND BD.ISBN IN (Select BD.ISBN FROM BookData BD, Book B WHERE BD.ISBN = B.ISBN AND B.availability = true) ";
				}
				query = query + "Group BY BD.ISBN ORDER BY AVG(O.score) DESC";

				PreparedStatement query_statment = con.prepareStatement(query);

				int i = 1;
				if (!name.equals("")) {
					query_statment.setString(i, name);
					i++;
				}
				if (!publisher.equals("")) {
					query_statment.setString(i, publisher);
					i++;
				}
				if (!title.equals("")) {
					query_statment.setString(i, title);
					i++;
				}
				if (!subject.equals("")) {
					query_statment.setString(i, subject);
					i++;
				}

				ResultSet rs = query_statment.executeQuery();

				result += "<table border='1'><tr><td colspan='9' style='text-align:center;'>Search Result</td></tr>"
						+ "<tr><td>Title</td><td>Author</td><td>Publisher</td><td>Year of Publish</td><td>Format</td><td>Subject</td><td>Summary</td><td>ISBN</td><td>AverageScore</td></tr>";
				while (rs.next()) {
					result += "<tr><td>" + rs.getString("title") + "</td><td>"
							+ rs.getString("author") + "</td><td>"
							+ rs.getString("publisher") + "</td><td>"
							+ rs.getString("yearOfPublish") + "</td><td>"
							+ rs.getString("format") + "</td><td>"
							+ rs.getString("subject") + "</td><td>"
							+ rs.getString("summary") + "</td><td>"
							+ rs.getString("ISBN") + "</td><td>"
							+ rs.getString("AverageScore") + "</td><tr>";
				}
				result += "</table>";
				rs.close();
			}

			else if (orderBy.equals("3")) {

				String query = "SELECT BD.title, GROUP_CONCAT(A.name SEPARATOR ', ')as author, BD.publisher, BD.yearOfPublish, BD.format, BD.subject, BD.summary, BD.ISBN, SUM(NB.NumCheckedOutForEachBook) as NumCheckedOut FROM BookData BD INNER JOIN (SELECT B.bookID, B.ISBN, B.availability, COUNT(C.checkID) as NumCheckedOutForEachBook FROM Book B LEFT OUTER JOIN CheckOut C ON C.bookID = B.bookID GROUP BY B.bookID) NB ON NB.ISBN = BD.ISBN INNER JOIN Author A ON A.ISBN = BD.ISBN WHERE ";
				Boolean isFirst = true;
				if (!name.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "A.name = ? ";
				}

				if (!publisher.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.publisher = ? ";
				}
				if (!title.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.title LIKE ? ";
				}
				if (!subject.equals("")) {
					if (isFirst)
						isFirst = false;
					else
						query = query + "AND ";
					query = query + "BD.subject = ? ";
				}
				if (availableBooksOnly == "yes") {
					query = query
							+ "AND BD.ISBN IN (Select BD.ISBN FROM BookData BD, Book B WHERE BD.ISBN = B.ISBN AND B.availability = true) ";
				}
				query = query
						+ "Group BY BD.ISBN ORDER BY SUM(NB.NumCheckedOutForEachBook) DESC";

				PreparedStatement query_statment = con.prepareStatement(query);

				int i = 1;
				if (!name.equals("")) {
					query_statment.setString(i, name);
					i++;
				}
				if (!publisher.equals("")) {
					query_statment.setString(i, publisher);
					i++;
				}
				if (!title.equals("")) {
					query_statment.setString(i, title);
					i++;
				}
				if (!subject.equals("")) {
					query_statment.setString(i, subject);
					i++;
				}

				ResultSet rs = query_statment.executeQuery();
				result += "<table border='1'><tr><td colspan='9' style='text-align:center;'>Search Result</td></tr>"
						+ "<tr><td>Title</td><td>Author</td><td>Publisher</td><td>Year of Publish</td><td>Format</td><td>Subject</td><td>Summary</td><td>ISBN</td><td>Number of Checked Out</td></tr>";
				while (rs.next()) {
					result += "<tr><td>" + rs.getString("title") + "</td><td>"
							+ rs.getString("author") + "</td><td>"
							+ rs.getString("publisher") + "</td><td>"
							+ rs.getString("yearOfPublish") + "</td><td>"
							+ rs.getString("format") + "</td><td>"
							+ rs.getString("subject") + "</td><td>"
							+ rs.getString("summary") + "</td><td>"
							+ rs.getString("ISBN") + "</td><td>"
							+ rs.getString("NumCheckedOut") + "</td><tr>";
				}
				result += "</table>";
				rs.close();
			} else {
				result = "Your Order By Input was Wrong.";
			}

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String returnBook(String bookID, Connection con) throws Exception {
		try {
			String result = "";

			String queryCheck = "SELECT C.bookID From CheckOut C, StillCheckedOut S WHERE C.checkID = S.checkID AND C.bookID = ?";
			PreparedStatement query_statmentCheck = con
					.prepareStatement(queryCheck);
			query_statmentCheck.setString(1, bookID);
			ResultSet rsCheck = query_statmentCheck.executeQuery();
			if (!rsCheck.next()) {
				result = "This book is not checked out.";
			}

			else {
				String query = "UPDATE CheckOut SET returnedDate = curdate() WHERE exists (select 1 from StillCheckedOut WHERE CheckOut.checkID = StillCheckedOut.checkID AND CheckOut.bookID = ?) Limit 1";
				PreparedStatement query_statment = con.prepareStatement(query);
				query_statment.setString(1, bookID);
				query_statment.executeUpdate();

				String query2 = "DELETE FROM StillCheckedOut WHERE EXISTS (SELECT 1 FROM CheckOut WHERE StillCheckedOut.checkID = CheckOut.checkID AND CheckOut.bookID = ?) LIMIT 1";
				PreparedStatement query_statment2 = con
						.prepareStatement(query2);
				query_statment2.setString(1, bookID);
				query_statment2.executeUpdate();

				String query3 = "UPDATE Book SET availability = true WHERE Book.bookID = ?";
				PreparedStatement query_statment3 = con
						.prepareStatement(query3);
				query_statment3.setString(1, bookID);
				query_statment3.executeUpdate();

				result = "Returning books completed.";

				System.out
						.println("List of individuals on the wait list for this book");
				String query4 = "SELECT U.username, U.fullName, U.cardID FROM WaitingList W, Book B, BookData BD, User U WHERE B.bookID = ? AND B.ISBN = BD.ISBN AND BD.ISBN = W.ISBN AND W.cardID = U.cardID";
				PreparedStatement query_statment4 = con
						.prepareStatement(query4);
				query_statment4.setString(1, bookID);
				ResultSet rs = query_statment4.executeQuery();

				result += "<br><table border='3'><tr><td style='text-align:center;'>Wait list for this book</td></tr>"
						+ "<tr><td>Username</td><td>Name</td><td>cardID</td></tr>";
				while (rs.next()) {
					result += "<tr><td>" + rs.getString("username")
							+ "</td><td>" + rs.getString("fullName")
							+ "</td><td>" + rs.getString("cardID")
							+ "</td></tr>";
				}
				result += "</table>";

				rs.close();
			}

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String lostBook(String bookID, Connection con) throws Exception {
		try {
			String result = "";

			String queryCheck = "SELECT bookID From CheckOut C, StillCheckedOut S WHERE C.checkID = S.checkID AND bookID = ?";
			PreparedStatement query_statmentCheck = con
					.prepareStatement(queryCheck);
			query_statmentCheck.setString(1, bookID);
			ResultSet rsCheck = query_statmentCheck.executeQuery();
			if (!rsCheck.next()) {
				result = "This book is not checked out.";
			}

			else {
				String query = "UPDATE CheckOut SET returnedDate = curdate() WHERE exists (select 1 from StillCheckedOut WHERE CheckOut.checkID = StillCheckedOut.checkID AND CheckOut.bookID = ?) Limit 1";
				PreparedStatement query_statment = con.prepareStatement(query);
				query_statment.setString(1, bookID);
				query_statment.executeUpdate();

				String queryLost = "INSERT Lost (checkID) SELECT CheckOut.checkID FROM CheckOut, StillCheckedOut WHERE CheckOut.bookID = ? AND CheckOut.checkID = StillCheckedOut.checkID";
				PreparedStatement query_statment_lost = con
						.prepareStatement(queryLost);
				query_statment_lost.setString(1, bookID);
				query_statment_lost.executeUpdate();

				String query2 = "DELETE FROM StillCheckedOut WHERE EXISTS (SELECT 1 FROM CheckOut WHERE StillCheckedOut.checkID = CheckOut.checkID AND CheckOut.bookID = ?) LIMIT 1";
				PreparedStatement query_statment2 = con
						.prepareStatement(query2);
				query_statment2.setString(1, bookID);
				query_statment2.executeUpdate();

				String query3 = "UPDATE Book SET availability = false, location = 'Lost' WHERE Book.bookID = ?";
				PreparedStatement query_statment3 = con
						.prepareStatement(query3);
				query_statment3.setString(1, bookID);
				query_statment3.executeUpdate();

				result = "Returning books completed.";
			}

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String getBookRecord(String ISBN, Connection con) throws Exception {
		try {
			String result = "";

			String query = "SELECT BD.ISBN, BD.title, GROUP_CONCAT(A.name SEPARATOR ', ')as author, BD.publisher, BD.yearOfPublish, BD.format, BD.subject, BD.summary FROM BookData BD, Author A WHERE BD.ISBN = A.ISBN AND BD.ISBN = ? GROUP BY BD.ISBN";
			PreparedStatement query_statment = con.prepareStatement(query);
			query_statment.setString(1, ISBN);
			ResultSet rs = query_statment.executeQuery();

			result += "<table border='1'><tr><td colspan='8' style='text-align:center;'>All information about the book</td></tr>"
					+ "<tr><td>ISBN</td><td>Title</td><td>Author</td><td>Publisher</td><td>Year of Publish</td><td>Format</td><td>Subject</td><td>Summary</td></tr>";
			while (rs.next()) {
				result += "<tr><td>" + rs.getString("ISBN") + "</td><td>"
						+ rs.getString("title") + "</td><td>"
						+ rs.getString("author") + "</td><td>"
						+ rs.getString("publisher") + "</td><td>"
						+ rs.getString("yearOfPublish") + "</td><td>"
						+ rs.getString("format") + "</td><td>"
						+ rs.getString("subject") + "</td><td>"
						+ rs.getString("summary") + "</td><tr>";
			}
			result += "</table>";
			rs.close();

			String query2 = "SELECT B.bookID, B.location, if(B.availability, 'yes', 'no') as IsAvailable FROM Book B WHERE B.ISBN = ?";
			PreparedStatement query_statment2 = con.prepareStatement(query2);
			query_statment2.setString(1, ISBN);
			ResultSet rs2 = query_statment2.executeQuery();

			result += "<br><table border='1'><tr><td colspan='3' style='text-align:center;'>List of all the copies and its location</td></tr>"
					+ "<tr><td>bookID</td><td>location</td><td>IsAvailable</td></tr>";
			while (rs2.next()) {
				result += "<tr><td>" + rs2.getString("bookID") + "</td><td>"
						+ rs2.getString("location") + "</td><td>"
						+ rs2.getString("IsAvailable") + "</td><tr>";
			}
			result += "</table>";
			rs2.close();

			String query3 = "SELECT B.bookID, U.fullName, C.checkOutDate, C.returnedDate FROM Book B, CheckOut C, User U WHERE B.bookID = C.bookID AND C.cardID = U.cardID AND B.ISBN = ?";
			PreparedStatement query_statment3 = con.prepareStatement(query3);
			query_statment3.setString(1, ISBN);
			ResultSet rs3 = query_statment3.executeQuery();

			result += "<br><table border='1'><tr><td colspan='4' style='text-align:center;'>List of Checkout</td></tr>"
					+ "<tr><td>bookID</td><td>fullName</td><td>checkOutDate</td><td>returnedDate</td></tr>";
			while (rs3.next()) {
				result += "<tr><td>" + rs3.getString("bookID") + "</td><td>"
						+ rs3.getString("fullName") + "</td><td>"
						+ rs3.getString("checkOutDate") + "</td><td>"
						+ rs3.getString("returnedDate") + "</td><tr>";
			}
			result += "</table>";
			rs3.close();

			String query4 = "SELECT AVG(O.score) as AverageReviewScore FROM Opinion O WHERE O.ISBN = ? GROUP BY O.ISBN";
			PreparedStatement query_statment4 = con.prepareStatement(query4);
			query_statment4.setString(1, ISBN);
			ResultSet rs4 = query_statment4.executeQuery();
			while (rs4.next()) {
				result += "<br><p>The average review score for the book is "
						+ rs4.getString("AverageReviewScore") + "</p>";
			}
			System.out.println(" ");
			rs4.close();

			System.out.println("Individual reviews for the book.");
			String query5 = "SELECT shortText as Reviews FROM Opinion O WHERE O.ISBN = ?";
			PreparedStatement query_statment5 = con.prepareStatement(query5);
			query_statment5.setString(1, ISBN);
			ResultSet rs5 = query_statment5.executeQuery();

			result += "<br><table border='1'><tr><td style='text-align:center;'>reviews for the book</td></tr>";
			while (rs5.next()) {
				if (rs5.getString("Reviews") != null)
					result += "<tr><td>" + rs5.getString("Reviews")
							+ "</td><tr>";
			}
			result += "</table>";
			rs5.close();

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String getBookStatistics(String num, Connection con) throws Exception {
		try {
			int n = Integer.parseInt(num);

			String result = "";

			String query = "SELECT BD.title, BD.ISBN, SUM(NB.NumCheckedOutForEachBook) as NumCheckedOut FROM BookData BD INNER JOIN (SELECT B.bookID, B.ISBN, COUNT(C.checkID) as NumCheckedOutForEachBook FROM Book B LEFT OUTER JOIN CheckOut C ON C.bookID = B.bookID GROUP BY B.bookID) NB ON NB.ISBN = BD.ISBN Group BY BD.ISBN ORDER BY SUM(NB.NumCheckedOutForEachBook) DESC LIMIT ?";
			PreparedStatement query_statment = con.prepareStatement(query);
			query_statment.setInt(1, n);
			ResultSet rs = query_statment.executeQuery();

			result += "<table border='1'><tr><td colspan='3' style='text-align:center;'>The list of the n most checked out books</td></tr>"
					+ "<tr><td>ISBN</td><td>Title</td><td>NumCheckedOut</td></tr>";
			while (rs.next()) {
				result += "<tr><td>" + rs.getString("ISBN") + "</td><td>"
						+ rs.getString("title") + "</td><td>"
						+ rs.getString("NumCheckedOut") + "</td><tr>";
			}
			result += "</table>";
			rs.close();

			String query2 = "SELECT BD.title, BD.ISBN, COUNT(W.cardID) as NumRequested FROM BookData BD, WaitingList W WHERE BD.ISBN = W.ISBN Group BY BD.ISBN ORDER BY COUNT(W.cardID) DESC LIMIT ?";
			PreparedStatement query_statment2 = con.prepareStatement(query2);
			query_statment2.setInt(1, n);
			ResultSet rs2 = query_statment2.executeQuery();

			result += "<br><table border='1'><tr><td colspan='3' style='text-align:center;'>The list of the n most requested books</td></tr>"
					+ "<tr><td>ISBN</td><td>Title</td><td>NumRequested</td></tr>";
			while (rs2.next()) {
				result += "<tr><td>" + rs2.getString("ISBN") + "</td><td>"
						+ rs2.getString("title") + "</td><td>"
						+ rs2.getString("NumRequested") + "</td><tr>";
			}
			result += "</table>";
			rs2.close();

			String query3 = "SELECT BD.title, BD.ISBN, COUNT(L.checkID) as NumLost FROM BookData BD, Book B, CheckOut C, Lost L WHERE BD.ISBN = B.ISBN AND B.bookID = C.bookID AND C.checkID = L.checkID Group BY BD.ISBN ORDER BY COUNT(L.checkID) DESC LIMIT ?";
			PreparedStatement query_statment3 = con.prepareStatement(query3);
			query_statment3.setInt(1, n);
			ResultSet rs3 = query_statment3.executeQuery();

			result += "<br><table border='1'><tr><td colspan='3' style='text-align:center;'>The list of the n most lost books</td></tr>"
					+ "<tr><td>ISBN</td><td>Title</td><td>NumLost</td></tr>";
			while (rs3.next()) {
				result += "<tr><td>" + rs3.getString("ISBN") + "</td><td>"
						+ rs3.getString("title") + "</td><td>"
						+ rs3.getString("NumLost") + "</td><tr>";
			}
			result += "</table>";
			rs3.close();

			String query4 = "SELECT A.name as author, SUM(NB.NumCheckedOutForEachBook) as NumCheckedOut FROM BookData BD INNER JOIN (SELECT B.bookID, B.ISBN, COUNT(C.checkID) as NumCheckedOutForEachBook FROM Book B LEFT OUTER JOIN CheckOut C ON C.bookID = B.bookID GROUP BY B.bookID) NB ON NB.ISBN = BD.ISBN INNER JOIN Author A ON A.ISBN = BD.ISBN Group BY A.name ORDER BY SUM(NB.NumCheckedOutForEachBook) DESC LIMIT ?";
			PreparedStatement query_statment4 = con.prepareStatement(query4);
			query_statment4.setInt(1, n);
			ResultSet rs4 = query_statment4.executeQuery();

			result += "<br><table border='1'><tr><td colspan='2' style='text-align:center;'>The list of the n most popular authors</td></tr>"
					+ "<tr><td>author</td><td>NumCheckedOut</td></tr>";
			while (rs4.next()) {
				result += "<tr><td>" + rs4.getString("author") + "</td><td>"
						+ rs4.getString("NumCheckedOut") + "</td><tr>";
			}
			result += "</table>";
			rs4.close();

			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String getUserStatistics(String num, Connection con)
			throws Exception {
		try {
			String result = "";
			int n = Integer.parseInt(num);

			String query = "SELECT U.fullName, COUNT(C.checkID) as NumCheckedOut FROM User U, CheckOut C WHERE U.cardID = C.cardID GROUP BY C.cardID ORDER BY COUNT(C.checkID) DESC LIMIT ?";
			PreparedStatement query_statment = con
					.prepareStatement(query);
			query_statment.setInt(1, n);
			ResultSet rs = query_statment.executeQuery();
			
			result += "<table border='1'><tr><td colspan='2' style='text-align:center;'>The top n users who have checked out the most books</td></tr>"
					+ "<tr><td>fullName</td><td>NumCheckedOut</td></tr>";
			while (rs.next()) {
				result += "<tr><td>" + rs.getString("fullName") + "</td><td>"
						+ rs.getString("NumCheckedOut") + "</td><tr>";
			}
			result += "</table>";
			rs.close();

			String query2 = "SELECT U.fullName, COUNT(O.ISBN) as NumReview FROM User U, Opinion O WHERE U.cardID = O.cardID GROUP BY O.cardID ORDER BY COUNT(O.ISBN) DESC LIMIT ?";
			PreparedStatement query_statment2 = con
					.prepareStatement(query2);
			query_statment2.setInt(1, n);
			ResultSet rs2 = query_statment2.executeQuery();
			
			result += "<br><table border='1'><tr><td colspan='2' style='text-align:center;'>The top n users who have rated the most number of books</td></tr>"
					+ "<tr><td>fullName</td><td>NumReview</td></tr>";
			while (rs2.next()) {
				result += "<tr><td>" + rs2.getString("fullName") + "</td><td>"
						+ rs2.getString("NumReview") + "</td><tr>";
			}
			result += "</table>";
			rs2.close();

			String query3 = "SELECT U.fullName, COUNT(L.checkID) as NumLost FROM User U, Lost L, CheckOut C WHERE U.cardID = C.cardID AND C.checkID = L.checkID GROUP BY U.cardID ORDER BY COUNT(L.checkID) DESC LIMIT ?";
			PreparedStatement query_statment3 = con
					.prepareStatement(query3);
			query_statment3.setInt(1, n);
			ResultSet rs3 = query_statment3.executeQuery();
			
			result += "<br><table border='1'><tr><td colspan='2' style='text-align:center;'>The top n users who have lost the most books</td></tr>"
					+ "<tr><td>fullName</td><td>NumLost</td></tr>";
			while (rs3.next()) {
				result += "<tr><td>" + rs3.getString("fullName") + "</td><td>"
						+ rs3.getString("NumLost") + "</td><tr>";
			}
			result += "</table>";
			rs3.close();
		
			return result;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
