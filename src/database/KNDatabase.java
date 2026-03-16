package database;

import com.microsoft.sqlserver.jdbc.SQLServerAASEnclaveProvider;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.SQLData;
import java.sql.SQLException;

public class KNDatabase {
    public static void ketNoiDatabase() throws SQLServerException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser("sa");
        ds.setPassword("123456789");
        ds.setServerName("denxif\\SQLEXPRESS");
        ds.setPortNumber(1433);
        ds.setDatabaseName("SQLqlsv2");

        // thêm 2 dòng này để sửa lỗi SSL
        ds.setEncrypt("true");
        ds.setTrustServerCertificate(true);

        try(Connection conn = ds.getConnection()) {
            System.out.println("chay thanh cong");
            System.out.println(conn.getMetaData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

