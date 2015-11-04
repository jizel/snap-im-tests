package travel.snapshot.dp.qa.base;

import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.dp.qa.ConfigProps.getPropValue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("UnusedAssignment")
public class TestUtils {

    private TestUtils() {
        throw new AssertionError("Utility class - DO NOT INSTANTIATE!");
    }

    public static String getQueryResultSource(String sqlQueryForSource) throws Exception {
        Connection connToSource = setUpDbConnection(getPropValue("dma.connectionString"),
                getPropValue("dma.username"), getPropValue("dma.password"));
        String resultSource = null;
        PreparedStatement stmtToSource = null;
        ResultSet resultSetSource = null;

        stmtToSource = connToSource.prepareStatement(sqlQueryForSource);
        resultSetSource = stmtToSource.executeQuery();

        while (resultSetSource.next()) {
            System.out.println(resultSetSource.getInt(1));
            resultSource = String.valueOf(resultSetSource.getInt(1)).trim();
        }

        closeDbConnection(connToSource, stmtToSource, resultSetSource);
        System.out.println("Connection to source closed");
        return resultSource;
    }

    public static String getQueryResultTarget(String sqlQueryForTarget) throws Exception {
        Connection connToTarget = setUpDbConnection(getPropValue("dwh.connectionString"),
                getPropValue("dwh.username"), getPropValue("dwh.password"));
        String resultTarget = null;
        PreparedStatement stmtToTarget = null;
        ResultSet resultSetTarget = null;

        stmtToTarget = connToTarget.prepareStatement(sqlQueryForTarget);
        resultSetTarget = stmtToTarget.executeQuery();

        while (resultSetTarget.next()) {
            System.out.println(resultSetTarget.getInt(1));
            resultTarget = String.valueOf(resultSetTarget.getInt(1)).trim();
        }

        closeDbConnection(connToTarget, stmtToTarget, resultSetTarget);
        System.out.println("Connection to target closed");
        return resultTarget;
    }

    public static void verifyOutcome(String resultSource, String resultTarget) {
        Boolean sameOutcome = false;

        if (resultSource.equalsIgnoreCase(resultTarget)) {
            sameOutcome = true;
            System.out.println("The results are EQUAL!");
        } else {
            System.out.println("The results are NOT EQUAL!");
        }

        assertTrue("The outcome from the source and the target is not equal.", sameOutcome);
    }

    public static Connection setUpDbConnection(String url, String user, String pass) throws Exception {
        Connection conn = null;
        conn = DriverManager.getConnection(url, user, pass);
        System.out.println("Connected to database");
        return conn;
    }

    public static void testFactLoad(String sqlQueryForSource, String sqlQueryForTarget) throws Exception {
        String outcomeSource = null;
        String outcomeTarget = null;

        outcomeSource = getQueryResultSource(sqlQueryForSource);
        outcomeTarget = getQueryResultTarget(sqlQueryForTarget);

        System.out.println("Result from the source is: " + outcomeSource);
        System.out.println("Result from the target is: " + outcomeTarget);

        verifyOutcome(outcomeSource, outcomeTarget);

    }

    public static void closeDbConnection(Connection conn, PreparedStatement ps, ResultSet rs) throws Exception {
        closeQuietly(ps);
        closeQuietly(rs);
        closeQuietly(conn);
    }
}
