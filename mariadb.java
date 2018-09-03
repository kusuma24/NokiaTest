import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class mariadb{

public static void main(String[] args){
	
c
System.out.println("hello");
String dbUrl = "jdbc:mysql://127.0.0.1/VSAM";
//String dbClass = "org.mariadb.jdbc.Driver";
String dbClass = "oracle.jdbc.OracleDriver";
Object obj= null;
Blob Bl = null;
String query = "Insert into workitemReq values(50,'nbuser','SO4601','3',2,3,'3',0,0,'request','"+Bl+"')";
//String query =" SELECT parameters  FROM workitemreq where workitemid = 'SO467' ";

try {

Class.forName("oracle.jdbc.OracleDriver");
Connection con = DriverManager.getConnection ("jdbc:mysql://127.0.0.1:3306/VSAM","om","om");
System.out.println("con"+con);
Statement stmt = con.createStatement();
ResultSet rs = stmt.executeQuery(query);

while (rs.next()) {
dbtime = rs.getString(1);
System.out.println(dbtime);
} //end while

con.close();
} //end try

catch(ClassNotFoundException e) {
e.printStackTrace();
}

catch(SQLException e) {
e.printStackTrace();
}

}  //end main

}  //end class

