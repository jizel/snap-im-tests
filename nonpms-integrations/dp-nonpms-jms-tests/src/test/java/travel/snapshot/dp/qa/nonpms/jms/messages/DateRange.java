package travel.snapshot.dp.qa.nonpms.jms.messages;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRange {

    private LocalDate startDate;
    private LocalDate endDate;

}
