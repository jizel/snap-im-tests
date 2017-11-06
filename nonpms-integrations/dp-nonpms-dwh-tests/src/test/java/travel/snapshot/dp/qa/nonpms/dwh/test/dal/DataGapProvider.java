package travel.snapshot.dp.qa.nonpms.dwh.test.dal;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DataGapProvider {

    List<Map<String, Object>> getDataGaps(@Param("since") Integer since, @Param("until") Integer until);

}
