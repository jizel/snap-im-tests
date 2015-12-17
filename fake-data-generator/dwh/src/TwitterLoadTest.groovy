

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2013, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)

def properties = (990001..991000)
def rand = new Random()

properties.each { property_id ->
    def i = [0,0,0,0,0,0,0,0,0];
    (from..to).eachWithIndex() {
        d, j ->


            //                                              impressions                 //engagement
            println "${property_id}\t${d.format("yyyyMMdd")}\t${i[0]}\t${i[1]}\t${i[2]}\t${i[3]}\t${i[4]}\t${i[5]}\t${i[6]}\t${i[7]}\t${i[8]}\t'${d.format("yyyy-MM-dd HH:mm:ss")}'"
        i[0] = i[0]+ +rand.nextInt(50)//impressions
        i[1] = i[1]+ +rand.nextInt(20)//engagement
        i[2] = i[2]+ +rand.nextInt(30)//followers
        i[3] = i[3]+ +rand.nextInt(10)//number_of_tweets
        i[4] = i[4]+ +rand.nextInt(10)//reach
        i[5] = i[5]+ +rand.nextInt(60)//retweets
        i[6] = i[6]+ +rand.nextInt(100)//retweet_reach
        i[7] = i[7]+ +rand.nextInt(5)//mentiones
        i[8] = i[8]+ +rand.nextInt(30)//mention_reach
    }
}


/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
