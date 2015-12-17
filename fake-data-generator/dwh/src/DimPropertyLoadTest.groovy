

def from = Calendar.instance

from.set(year: 2013, month: Calendar.JANUARY, date: 1)


def properties = (990001..991000)


properties.each { property_id ->

    println "${property_id}\t${property_id}99-9999-4999-a999-999999999999\tTest property ${property_id}"
}

/*
from.upto(to) {
    print it[Calendar.YEAR] + " " + it[Calendar.MONTH] + it[Calendar.DAY_OF_MONTH]
}*/
