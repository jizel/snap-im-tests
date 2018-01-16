package travel.snapshot.dp.qa.junit.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import travel.snapshot.dp.qa.junit.helpers.FieldType;

/**
 * Object type used for validation tests. Enables to specify correct, invalid and longer attribute values
 * and use them for validations.
 */
@Getter
@AllArgsConstructor(staticName = "of")
@ToString
public class ObjectField {

    private String path;
    private FieldType type;
    private Boolean required;
    private String correct;
    private String invalid;
    private String longer;


    public String getName() {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public boolean isTopLevel() {
        return getName().equals(path.replace("/", ""));
    }
}
