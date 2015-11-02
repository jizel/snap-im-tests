package test.facts;

import org.junit.Test;

import test.base.TestBase;

public class TestFacts extends TestBase {

	@Test
	public void testFactOtbStayDatesLoad() throws Exception {

		String outcomeSource = null;
		String outcomeTarget = null;

		// TODO: To set params and to deliver from file
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElements where ModificationTimestamp > '2014-01-01'";
		String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates";

		outcomeSource = getQueryResultSource(sqlQueryForSource);
		outcomeTarget = getQueryResultTarget(sqlQueryForTarget);

		System.out.println("Result from the source is: " + outcomeSource);
		System.out.println("Result from the target is: " + outcomeTarget);
		
		verifyOutcome(outcomeSource, outcomeTarget);

	}

}
