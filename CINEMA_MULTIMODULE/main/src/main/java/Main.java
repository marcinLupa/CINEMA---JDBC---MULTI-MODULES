import connection.DbConnection;
import exceptions.MyException;
import service.MenuService;

/**
 * @author Marcin Lupa
 * @version 1.0
 * @since   2019-01-28
 * App Cinema is a ticket sales simulator, that have many extra options, like statistics, filtering, sorting data
 * Also you can update, delate and add data to date base,
 * Of course normally user wont be able to delete or update movies or customers data,
 * but for tests this option is available
 */

public class Main {

    public static void main(String[] args) {

        try {
            new MenuService().manage();
        } catch
        (MyException e) {
            System.err.println("EXCEPTION DATE TIME: " + e.getExceptionInfo().getDateTime());
            System.err.println("EXCEPTION CODE: " + e.getExceptionInfo().getCode().getDescription());
            System.out.println(e.getExceptionInfo().getMessage());
        } finally {
            DbConnection.getInstance().close();
        }
    }
}
