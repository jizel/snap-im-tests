package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import travel.snapshot.dp.api.webperformance.model.MetricByChannelDto;
import travel.snapshot.dp.api.webperformance.model.ReferralStatsDto;

import java.util.List;

/**
 * Remove this when legacy stuff will not be supported anymore
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LegacyReferralStatsDto extends ReferralStatsDto {

    /**
     * Array of metric values grouped by countries.
     **/
    @JsonProperty("total_numbers")
    private List<MetricByChannelDto> totalNumbers;

}
