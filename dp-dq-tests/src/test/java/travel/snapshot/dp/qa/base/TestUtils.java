package travel.snapshot.dp.qa.base;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.ConfigProps;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestUtils {

  public static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

  public static final DbHelper dbHelper = new DbHelper();

  private TestUtils() {
    throw new AssertionError("Utility class - DO NOT INSTANTIATE!");
  }

  public static void testLoad(String sqlQueryForSource, String sqlQueryForTarget) throws Exception {
    final String outcomeSource = getQueryResultSource(sqlQueryForSource).values().toString();
    final String outcomeTarget = getQueryResultTarget(sqlQueryForTarget).values().toString();

    logger.info("Result from the source is: " + outcomeSource);
    logger.info("Result from the target is: " + outcomeTarget);

    assertThat("The outcome from the source and the target is not equal.", outcomeTarget,
        is(outcomeSource));
  }

  public static void testLoad(String sqlQueryForSource, String sqlQueryForTarget, String message)
      throws Exception {
    final String outcomeSource = getQueryResultSource(sqlQueryForSource).values().toString();
    final String outcomeTarget = getQueryResultTarget(sqlQueryForTarget).values().toString();

    logger.info(message);
    logger.info("Result from the source is: " + outcomeSource);
    logger.info("Result from the target is: " + outcomeTarget);

    assertThat("The outcome from the source and the target is not equal.", outcomeTarget,
        is(outcomeSource));
  }

  public static String withStartDate(String queryWithPlaceholder) {
    final String startDate = ConfigProps.getPropValue("etl.startdate");
    if (isEmpty(startDate)) {
      throw new IllegalStateException(
          "etl.startDate property must be defined " + "- check your configuration!");
    }
    // MSSQL requires the date to be quoted, otherwise we get empty result set
    return format(queryWithPlaceholder, "'" + startDate + "'");
  }

  public static Map<String, Object> getQueryResultSource(String sqlQueryForSource)
      throws Exception {
    return dbHelper.sourceTemplate().queryForMap(sqlQueryForSource);
  }

  public static Map<String, Object> getQueryResultTarget(String sqlQueryForTarget)
      throws Exception {
    return dbHelper.targetTemplate().queryForMap(sqlQueryForTarget);
  }

  public static int getQueryResultInt(String sql) throws Exception {
    return dbHelper.targetTemplate().queryForObject(sql, int.class);
  }

  public static void followUpLoadTest(List<String> followUpListToSource,
      List<String> followUpListToTarget) throws Exception {

    Iterator<String> it1 = followUpListToSource.iterator();
    Iterator<String> it2 = followUpListToTarget.iterator();

    while (it1.hasNext() && it2.hasNext()) {
      String followUpQuerySource = it1.next();
      String followUpQueryTarget = it2.next();
      testLoad(followUpQuerySource, followUpQueryTarget);
    }
  }

  public static void followUpLoadTest(List<String> followUpListToSource,
      List<String> followUpListToTarget, List<String> messages) throws Exception {

    Iterator<String> it1 = followUpListToSource.iterator();
    Iterator<String> it2 = followUpListToTarget.iterator();
    Iterator<String> it3 = messages.iterator();

    while (it1.hasNext() && it2.hasNext() && it3.hasNext()) {
      String followUpQuerySource = it1.next();
      String followUpQueryTarget = it2.next();
      String message = it3.next();
      testLoad(followUpQuerySource, followUpQueryTarget, message);
    }
  }

  public static void testLoadFacebook(List<String> factsYesterday, List<String> incrementalsToday,
      List<String> factsToday, List<String> metrics) throws Exception {

    Iterator<String> it1 = factsYesterday.iterator();
    Iterator<String> it2 = incrementalsToday.iterator();
    Iterator<String> it3 = factsToday.iterator();
    Iterator<String> it4 = metrics.iterator();

    while (it1.hasNext() && it2.hasNext() && it3.hasNext() && it4.hasNext()) {
      String followUpFactsYesterday = it1.next();
      String followUpIncrementalsToday = it2.next();
      String followUpFactsToday = it3.next();
      String metric = it4.next();

      logger.info("Metric: " + metric);
      logger.info("Facts yesterday: " + getQueryResultInt(followUpFactsYesterday));
      logger.info("Incrementals today: " + getQueryResultInt(followUpIncrementalsToday));
      logger.info("Facts today: " + getQueryResultInt(followUpFactsToday));

      assertEquals(
          getQueryResultInt(followUpFactsYesterday) + getQueryResultInt(followUpIncrementalsToday),
          getQueryResultInt(followUpFactsToday));
    }
  }

  public static void testLoadTwitter(List<Integer> source, List<Integer> target,
      List<String> metrics) throws Exception {

    Iterator<Integer> it1 = source.iterator();
    Iterator<Integer> it2 = target.iterator();
    Iterator<String> it3 = metrics.iterator();

    while (it1.hasNext() && it2.hasNext() && it3.hasNext()) {
      int sourceNext = it1.next();
      int targetNext = it2.next();
      String metricNext = it3.next();

      logger.info("Metric: " + metricNext);
      logger.info("Source: " + sourceNext);
      logger.info("Target: " + targetNext);
      assertEquals(sourceNext, targetNext);
    }
  }

  public static JSONObject fetchUrlData(String url) throws JSONException {
    StringBuilder sb = new StringBuilder();
    URLConnection urlConnection = null;
    InputStreamReader strRead = null;

    try {
      URL myUrl = new URL(url);
      urlConnection = myUrl.openConnection();
      if (urlConnection != null && urlConnection.getInputStream() != null) {
        strRead = new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset());
        BufferedReader br = new BufferedReader(strRead);

        if (br != null) {
          int i;
          while ((i = strRead.read()) != -1) {
            sb.append((char) i);
          }
          br.close();
        }
      }
      strRead.close();
    } catch (Exception ex) {
      throw new RuntimeException("Problems when calling this URL!", ex);
    }
    JSONObject jsonObject = new JSONObject(sb.toString());
    return jsonObject;
  }

  public static ArrayList<String> getResponseData(String key, String outputField,
      JSONObject jsonObject) throws JSONException {

    JSONArray tsmresponse = (JSONArray) jsonObject.get(key);

    ArrayList<String> list = new ArrayList<String>();

    for (int i = 0; i < tsmresponse.length(); i++) {
      list.add(tsmresponse.getJSONObject(i).getString(outputField));
    }

    logger.info("The response data fetched are: " + list);
    return list;
  }
}
