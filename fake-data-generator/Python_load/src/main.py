#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv
import argparse
import os
from config import DATE_FROM, DATE_TO, create_custom_properties
from tables import TABLES, FILES


def main(table_filter, out_path, prop_size):
    # check if folder exist, if empty then exit
    if not os.path.exists(out_path):
        os.makedirs(out_path)

    print "Run after script ends:"
    print "mysql -h 10.0.1.100 -u snapshot_admin -pGHbsoSTzuSw7oMM01XLU"
    print 'SET SESSION SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";'

    for metadata in TABLES:
        if len(table_filter) and metadata['table'] not in table_filter:
            continue

        with open('{path}/{name}.tsv'.format(name=metadata['table'], path=out_path), 'wb') as csvfile:
            writer = csv.writer(csvfile, delimiter='\t')

            for property_id in prop_size:
                last_row = [0] * 20

                for iteration in metadata['multiply']:
                    data = {
                        'property_id': property_id,
                        'iter': iteration,
                        'row': last_row
                    }

                    last_row = [column(data) for column in metadata['columns']]
                    writer.writerow(last_row)

        print "LOAD DATA LOCAL INFILE '%(table)s.tsv' INTO TABLE %(table)s FIELDS TERMINATED BY '\\t';" % {
            'table': metadata['table']}

    for sql_file in FILES:
        print "\. %s;" % sql_file


def help():
    return '''Fake data generator for custom size of properties (default 1000) from {from_d} to {to_d}.
    Output path attribute is optional parameter for specifying path for output files.
    Tables attribute is optional filter - coma separated e.g. table1,table2.
    By default, data for all tables are generated.'''.format(
        from_d=DATE_FROM, to_d=DATE_TO)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='', usage=help())
    parser.add_argument('-out_path', type=str, default='',
                        help='''Output path for generated files, output folder has to exist;
                                examples: Windows: C:/Users/ Linux: /Users/''')
    parser.add_argument('-tables', type=str, help='''Table names, as string delimitered by "," ''')
    parser.add_argument('-prop_size', type=int, help='Number of properties for witch you want to generate data',
                        default=1000)
    args = parser.parse_args()

    try:
        tables = args.tables.split(",")
    except:
        tables = []

    main(tables, args.out_path, create_custom_properties(args.prop_size))
