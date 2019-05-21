package connection;

import exceptions.ExceptionCode;
import exceptions.MyException;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author Marcin Lupa
 */
public class DbConnection {


    private static DbConnection ourInstance = new DbConnection();

    public static DbConnection getInstance() {
        return ourInstance;
    }

    private DbConnection() {
        connect();
        createTables();
    }

    /**
     * @serialField driver to connect with date base
     * @serialField datebase adress
     */
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DATABASE = "jdbc:sqlite:test.db";
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    /**
     * This method is use to connect to date base
     */
    private void connect() {
        try {
            Class.forName(DRIVER);
            SQLiteConfig conf = new SQLiteConfig();
            conf.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DATABASE, conf.toProperties());

        } catch (Exception e) {
            throw new MyException(ExceptionCode.DB, "DB OPEN CONNECTION EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * Method that creates tables in SQLite, using Sql Table Command Class
     * where comands from sql are rebuild to java methods
     */
    private void createTables() {
        Statement statement;
        try {

            final String sqlMovie =
                    SqlTableCommand.builder()
                            .table("movie")
                            .primaryKey("id")
                            .stringColumn("title", 50, "not null")
                            .stringColumn("genre", 50, "not null")
                            .decimalColumn("price", 4, 2, "not null")
                            .intColumn("duration", "default 0")
                            .dateColumn("releaseDate", "not null")
                            .build().toString();

            final String sqlCustomer =
                    SqlTableCommand.builder()
                            .table("customer")
                            .primaryKey("id")
                            .stringColumn("name", 50, "not null")
                            .stringColumn("surname", 50, "not null")
                            .intColumn("age", "not null")
                            .stringColumn("email", 50, "not null")
                            .intColumn("loyaltyCardId", "")
                            .foreignKey("loyaltyCardId", "loyaltyCard", "id", "on delete set null")
                            .build().toString();


            final String sqlLoyalty_card =
                    SqlTableCommand.builder()
                            .table("loyaltyCard")
                            .primaryKey("id")
                            .dateColumn("expirationDate", "not null")
                            .decimalColumn("discount", 2, 1, "default 0")
                            .intColumn("moviesNumberId", "not null")
                            .build().toString();


            final String sqlSales_stand =
                    SqlTableCommand.builder()
                            .table("salesStand")
                            .primaryKey("id")
                            .intColumn("moviesId", "")
                            .intColumn("customerId", "")
                            .dateTimeColumn("startDateTime", "")
                            .foreignKey("moviesId", "movie", "id", "on delete set null")
                            .foreignKey("customerId","customer","id","on delete set null")
                            .build().toString();


            statement = connection.createStatement();

            statement.execute(sqlMovie);
            statement.execute(sqlCustomer);
            statement.execute(sqlLoyalty_card);
            statement.execute(sqlSales_stand);


        } catch (Exception e) {
            throw new MyException(ExceptionCode.DB, "CREATE TABLES EXCEPTION: " + e.getMessage());
        }
    }

    /**
     * Closing connection
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            throw new MyException(ExceptionCode.DB, "DB CLOSE CONNECTION: " + e.getMessage());
        }
    }

}
