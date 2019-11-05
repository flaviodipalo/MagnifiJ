package network.server.database;
import view.cli.Display;

import java.io.Serializable;
import java.sql.*;
import java.text.DecimalFormat;

/**
 * The database class is used for check all the data relative to the users. It keep trace of
 * user's username and password and all the statistics about the matches they play
 */
public class Database implements Serializable{

    private static final String URL = "jdbc:sqlite:src/main/resources/database/game.db";
    private transient Connection connection = null;
    private transient PreparedStatement statement = null;
    private static final int LENGTH_BETWEEN_COLUMNS_RANKING = 25;
    private static final String[] COLUMNS_OF_RANKING = {"USERNAME", "GAMES WON", "GAMES PLAYED", "POINTS PER GAME"};


    public Database(){
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            Display.println("Class not found database" , e);

        }



    }
    /**
     * this function checks if the users is already registered
     * @param username to check
     * @return true if the user already exists else false.
     */


    private boolean alreadyExists(String username) throws SQLException{
        openDB();
        boolean exists = false;
        String sql = "SELECT COUNT(*) AS COUNTER FROM User " +
                "WHERE User.Username = ?;";
        statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            if(resultSet.getInt("COUNTER")==1){
                exists = true;

            }

        }

        resultSet.close();
        closeDB();
        return exists;


    }

    /**
     * This function verify the correct username and password given by the user.
     * @param username given by the client
     * @param password given by the client
     * @return true if username and password match the user table, else false.
     */
    private boolean canLogin(String username, String password) throws SQLException{
        openDB();
        boolean canLogin = false;


        String sql = "SELECT COUNT(*) AS COUNTER FROM User " +
                "WHERE User.Username = ? AND User.Password = ?;";
        statement = connection.prepareStatement(sql);
        statement.setString(1,username);
        statement.setString(2,password);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()){
            if(resultSet.getInt("COUNTER") == 1){
                canLogin = true;
            }
        }
        resultSet.close();
        closeDB();
        return canLogin;

    }

    /**
     * this function inserts the user's username and password into the User table
     * @param username is the user's username
     * @param password is the user's password
     * @throws SQLException if a database access error occurs
     */
    private void insertUser(String username, String password) throws SQLException{
        openDB();
        String sql = "INSERT INTO User (Username, Password) " +
                "VALUES (? , ?);";

        statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        statement.executeUpdate();
        closeDB();
    }

    /**
     *This function closes the statement and connection pointers of the database
     * @throws SQLException if a database access error occurs
     */
    private void closeDB() throws SQLException{
        statement.close();
        connection.close();
    }

    /**
     * This function opens the connection with the database
     * @throws SQLException if database not found
     */
    private void openDB() throws SQLException{
        //open the database
        connection = DriverManager.getConnection(URL);

        //create the statement used for execute sql query
    }

    /**
     *
     * @param username given by the client
     * @param password given by the client
     * @return true if can sign in
     */
     public boolean signIn(String username, String password){
         try {
             return canLogin(username, password);
         } catch (SQLException e) {
             Display.println("SQL Exception" , e);
             return false;
         }
     }

    /**
     *
     * @param username given by the client
     * @param password given by the client
     * @return true if can sign up
     */
     public boolean signUp(String username, String password){
         try {
             if(alreadyExists(username)){
                 return false;
             }else {
                 insertUser(username, password);
                 return true;
             }
         } catch (SQLException e) {
             Display.println("SQL Exception" , e);
             return false;
         }
     }

    /**
     * @param username to add
     * @param points to add
     * @param winner true if the user has won
     * @throws SQLException if a database access error occurs
     */

     public void insertMatch(String username, int points, boolean winner) throws SQLException{
         openDB();
         String sql = "INSERT INTO Match (Username, Points, Victory) " +
                 " VALUES (? , ? , ?) ;";
         statement = connection.prepareStatement(sql);
         statement.setString(1, username);
         statement.setInt(2, points);
         statement.setBoolean(3, winner);
         statement.executeUpdate();
         closeDB();

     }

    /**
     * This function queries the database and return a rank
     * @return as String that contains the rankings
     * @throws SQLException if a database access error occurs
     */
     public String getRanking() throws SQLException{
         openDB();
         String sql = "SELECT Match.Username, IFNULL (victories.Victories, 0) AS victories, IFNULL(numberOfMatches.Nmatches, 0), IFNULL(avg(Match.Points), 0)" +
                 "FROM (Match left  join victories on Match.Username = victories.Username) join numberOfMatches on numberOfMatches.Username = Match.Username " +
                 "GROUP BY Match.Username " +
                 "Order by victories DESC , avg(Match.Points)";

         statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery();

         StringBuilder result = new StringBuilder();
         for (String aCOLUMNS_OF_RANKING : COLUMNS_OF_RANKING) {
             result.append(aCOLUMNS_OF_RANKING);
             result.append(appendSpaces(aCOLUMNS_OF_RANKING.length()));
         }
         result.append("\n\n");
         while (resultSet.next()){
            String name = resultSet.getString(1);
            result.append(name);
            result.append(appendSpaces(name.length()));
            String numberOfVictory = resultSet.getString(2);
            result.append("\t").append(numberOfVictory);
            result.append(appendSpaces(numberOfVictory.length()));
            String numberOfGames = resultSet.getString(3);
            result.append("\t").append(numberOfGames);
            result.append(appendSpaces(numberOfGames.length()));
            String averagePoints = resultSet.getString(4);
            Double averagePointsDouble = Double.parseDouble(averagePoints);
            DecimalFormat df = new DecimalFormat("#.##");
            result.append(df.format(averagePointsDouble));
            result.append(appendSpaces(averagePoints.length()));
            result.append("\n\n");
         }
         closeDB();

         return result.toString();
     }

    private String appendSpaces(int nameLength) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < LENGTH_BETWEEN_COLUMNS_RANKING - nameLength; i++)
            stringBuilder.append(" ");
        return stringBuilder.toString();
    }
}
