

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2014, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)





(from..to).eachWithIndex() {
    d, i ->

        println "INSERT INTO `DP_SOCIAL_MEDIA`.`FactFacebookPageStats` (`dim_property_id`, `dim_date_id`, " +
                "`impressions`, `engagements`, `followers`, `number_of_posts`, `reach`, `likes`, `unlikes`, `collected_time_stamp`, `inserted_time_stamp` )" +
         " VALUES (999999,  ${d.format("yyyyMMdd")},  ${i*3},  ${i},  ${i*10},  ${i+100}, ${i*5},  ${i*2}, ${i},  CURRENT_TIMESTAMP,  '${d.format("yyyy-MM-dd HH:mm:ss")}' );"

}

/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
