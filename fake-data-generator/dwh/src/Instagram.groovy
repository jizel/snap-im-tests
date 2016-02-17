

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2014, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)





(from..to).eachWithIndex() {
    d, i ->

        println "INSERT INTO `dp`.`Fact_instagram_daily` " +
                " (`property_id`, `date_id`, `posts_count`, `followers_count`, `likes_sum`, `comments_sum`, `hashtag_count`, `hashtag_value`)" +
         " VALUES (999999,  ${d.format("yyyyMMdd")},  ${i},  ${i*2},  ${i*5},  ${i*7}, ${20000+i},  'snapshot');"

}

/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
