

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2014, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)





(from..to).eachWithIndex() {
    d, i ->

        println "INSERT INTO `DP_SOCIAL_MEDIA`.`FactTwitterPageStats` (`dim_property_id`, `dim_date_id`, " +
                "`impressions`, `engagement`, `followers`, `number_of_tweets`, `reach`, `retweets`, " + 
                "`retweet_reach`, `mentions`, `mention_reach`, `inserted_time_stamp` )" +
         " VALUES (999999,  ${d.format("yyyyMMdd")},  ${i*3},  ${i},  ${i*10},  ${i+100}, ${i*5},  " + 
         " ${i*2}, ${i}, ${i+300}, ${i+50},  '${d.format("yyyy-MM-dd HH:mm:ss")}' );"

}

/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
