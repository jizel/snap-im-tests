

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2013, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)

def properties = (990001..991000)
def rand = new Random()

properties.each { property_id ->
    def i = [0,0,0,0,0];
    (from..to).eachWithIndex() {
        d, j ->


            //                                              impressions                 //engagement
            println "${property_id}\t${d.format("yyyyMMdd")}\t${i[0]}\t${i[1]}\t${i[2]}\t${i[3]}\t${i[4]}\t'snapshot'"
        i[0] = i[0]+ +rand.nextInt(50)//posts_count
        i[1] = i[1]+ +rand.nextInt(70)//followers_count
        i[2] = i[2]+ +rand.nextInt(30)//likes_sum
        i[3] = i[3]+ +rand.nextInt(100)//comments_sum
        i[4] = i[4]+ +rand.nextInt(100)//hashtag_count

    }
}


/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
