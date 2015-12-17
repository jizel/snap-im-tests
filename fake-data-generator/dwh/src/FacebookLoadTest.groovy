

import java.text.SimpleDateFormat

def from = Calendar.instance

from.set(year: 2013, month: Calendar.JANUARY, date: 1)

def to = Calendar.instance
to.set(year: 2016, month: Calendar.DECEMBER, date: 31)

def properties = (990001..991000)


properties.each { property_id ->
    (from..to).eachWithIndex() {
        d, i ->

            println "${property_id}\t${d.format("yyyyMMdd")}\t${i*10}\t${i+500}\t${i*3}\t${i+100}\t${i*5}\t${i*2}\t${i}\tCURRENT_TIMESTAMP\t'${d.format("yyyy-MM-dd HH:mm:ss")}'"

    }
}


/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
