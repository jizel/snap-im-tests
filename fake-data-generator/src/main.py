#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv
from config import PROPERTY_SIZE
from tables import TABLES


def main():
    print "Run after script ends:"
    print "mysql -h 10.0.1.100 -u snapshot_admin -pGHbsoSTzuSw7oMM01XLU"
    print 'SET SESSION SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";'

    for metadata in TABLES:
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

                

if __name__ == '__main__':
    main()