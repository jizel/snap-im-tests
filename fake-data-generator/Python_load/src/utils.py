#!/usr/bin/env python
# -*- coding: utf-8 -*-
from datetime import timedelta
import datetime
import random

__all__ = ['increment', 'rating', 'dates', 'currency']

increment   = lambda data, pos: data['row'][pos] + random.randint(5, 40)
rating      = lambda: random.choice([ x * 0.5 for x in range(1, 11)])
currency = ['EUR', 'GBP', 'CHF']

thousand_range = range(1000)

dates = []

def dates_calculation(date_from, date_to):
    date_from = datetime.date(date_from, 1, 1)
    date_to = datetime.date(date_to, 12, 31)
    date = date_from

    while date <= date_to:
        dates.append(date.strftime('%Y%m%d'))
        date += timedelta(days=1)
