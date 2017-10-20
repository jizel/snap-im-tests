package travel.snapshot.dp.qa.jms.messages;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRange {

    private LocalDate startDate;
    private LocalDate endDate;

}
