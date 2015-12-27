#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv,sys
from config import PROPERTY_SIZE, DATE_FROM, DATE_TO
from tables import TABLES


def main(table_filter):
    print "Run after script ends:"
    print "mysql -h 10.0.1.100 -u snapshot_admin -pGHbsoSTzuSw7oMM01XLU"
    print 'SET SESSION SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";'

    for metadata in TABLES:
        if len(table_filter) and metadata['table'] not in table_filter:
            continue

        with open('%s.tsv' % metadata['table'], 'wb') as csvfile:
            writer = csv.writer(csvfile, delimiter='\t')

            for property_id in PROPERTY_SIZE:
                last_row = [0] * 20

                for iteration in metadata['multiply']:
                    data = {
                        'property_id' : property_id,
                        'iter': iteration,
                        'row': last_row
                    }

                    last_row = [column(data) for column in metadata['columns']]
                    writer.writerow(last_row)

        print "LOAD DATA LOCAL INFILE '%(table)s.tsv' INTO TABLE %(table)s FIELDS TERMINATED BY '\\t';" % {'table': metadata['table']}

              
def help(): 
    sys.exit(
        ("Fake data generator for %d properties from %s to %s.\n" + 
        "Tables attribute is optional filter - coma separated e.g. table1,table2.\n" +
        "By default, data for all tables are generated.\n\n" +  
        "Valid tables are:\n%s\n\n\t%s [tables]") 
        % (len(PROPERTY_SIZE), DATE_FROM, DATE_TO, "".join(map(lambda x: " - %s\n" % x['table'], TABLES)), sys.argv[0])
    )


if __name__ == '__main__':
    try:
        tables = sys.argv[1].split(",")
    except:
        tables = []

    if len(sys.argv) > 2 or (len(tables) > 0 and tables[0] in ["-h", "--help"]):
        help()

    main(tables)