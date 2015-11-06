
package travel.snapshot.dp.qa.helpers;

/**
 *
 * @author konkol
 */
public class ObjectField {
    
    private String name;
    private String type;
    private boolean required;
    private String correct;
    private String invalid;
    private String longer;

    public String getName() {
        return name;
    }

    public void setFieldName(String fieldName) {
        this.name = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getInvalid() {
        return invalid;
    }

    public void setInvalid(String invalid) {
        this.invalid = invalid;
    }

    public String getLonger() {
        return longer;
    }

    public void setLonger(String longer) {
        this.longer = longer;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("fieldName : ").append(name).append('\n');
        builder.append("type : ").append(type).append('\n');
        builder.append("required : ").append(required).append('\n');
        builder.append("correct : ").append(correct).append('\n');
        builder.append("invalid : ").append(invalid).append('\n');
        builder.append("longer : ").append(longer).append('\n');
        return builder.toString();
    }

}
