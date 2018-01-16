package travel.snapshot.dp.qa.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import travel.snapshot.dp.api.webperformance.model.MetricByChannelDto;
import travel.snapshot.dp.api.webperformance.model.ReferralStatsDto;
import java.util.List;

/**
 * This class is not visible in api project and is here as a mock only until compatibility mode is deleted from DP (DP-1511)
 *
 * Can be switched on with property {@code webperformance.compatibility-mode} set to {@code true}.
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
