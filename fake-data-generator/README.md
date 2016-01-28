# Fake data generator

Usage: `python main.py [tables]`

Help: `python main.py --help`

If any error happens while loading data into mysql, check if there are some dimension tables (e.g. dp.Dim_date) filled with data.
Lost connection means there is some FK constraint broken.
