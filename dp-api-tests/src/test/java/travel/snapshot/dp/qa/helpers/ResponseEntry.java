
package travel.snapshot.dp.qa.helpers;

/**
 *
 * @author konkol
 */
public class ResponseEntry {
    
    private String testedField;
    private Integer responseCode;
    private Integer customCode;

    public String getTestedField() {
        return testedField;
    }

    public void setTestedField(String testedField) {
        this.testedField = testedField;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public Integer getCustomCode() {
        return customCode;
    }

    public void setCustomCode(Integer customCode) {
        this.customCode = customCode;
    }
    
}
