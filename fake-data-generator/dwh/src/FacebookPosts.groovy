

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2014, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)





(from..to).eachWithIndex() {
    d, i ->
        println "INSERT INTO `DP_SOCIAL_MEDIA`.`FactFacebookPostStats` (`dim_property_id`, `dim_date_id`, " +
                "`post_id`, `post_date_time_added`, `post_content`,`post_reach`, `post_engagement`, `inserted_time_stamp` )" +
         " VALUES (999999,  ${d.format("yyyyMMdd")}, 'post_id', '2014-01-01 01:00', 'content', ${i},  ${i*10},  '${d.format("yyyy-MM-dd HH:mm:ss")}' );"

}

