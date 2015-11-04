package base;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import properties.ConfigProps;

public class TestBase {

	public static String getQueryResultSource(String sqlQueryForSource) throws Exception {
		Connection connToSource = setUpDbConnection(ConfigProps.getPropValue("dma.connectionString"),
				ConfigProps.getPropValue("dma.username"), ConfigProps.getPropValue("dma.password"));
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
		Connection connToTarget = setUpDbConnection(ConfigProps.getPropValue("dwh.connectionString"),
				ConfigProps.getPropValue("dwh.username"), ConfigProps.getPropValue("dwh.password"));
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

		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// Log
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// Log
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// log
			}
		}

	}
}
