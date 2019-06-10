package backend;

import java.sql.*;

/**
 *
 * @author mt
 */
public class PostgresConnector {

    private String PG_HOST;
    private String PG_DB;
    private String USER;
    private String PWD;

    Connection con = null;
    Statement stmt = null;

    /**
     *
     * @param host
     * @param db
     * @param user
     * @param pw
     */
    public PostgresConnector(String host, String db, String user, String pw) {
        PG_HOST=host;
        PG_DB= db;
        USER=user;
        PWD= pw;
    }

    /**
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            // url = "jdbc:postgresql://host:port/database",
            con = DriverManager.getConnection("jdbc:postgresql://" + PG_HOST + ":5432/" + PG_DB,
                    USER,
                    PWD);

            stmt = con.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problems setting the connection");
        }
    }

    /**
     *
     */
    public void disconnect() {    // importante: fechar a ligacao 'a BD
        try {
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public Statement getStatement() {
        return stmt;
    }

}
