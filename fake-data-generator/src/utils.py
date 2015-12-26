#!/usr/bin/env python
# -*- coding: utf-8 -*-

from config import DATE_FROM, DATE_TO
from datetime import timedelta
import random

__all__ = ['increment', 'rating', 'dates']

increment   = lambda data, pos: data['row'][pos] + random.randint(5, 40)
rating      = lambda: random.choice([ x * 2. for x in range(2, 11)])

dates = []
date  = DATE_FROM

while date <= DATE_TO:
    dates.append(date.strftime('%Y%m%d'))
    date += timedelta(days=1)