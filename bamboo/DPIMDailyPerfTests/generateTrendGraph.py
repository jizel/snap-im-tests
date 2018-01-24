#!/usr/bin/env python

import numpy as np
import os
import json
import matplotlib
matplotlib.use('Agg')
# Do not switch these lines, it will break everything!
import matplotlib.pyplot as plt

def draw_graph(mins, maxs, means, stds, buildnumbers):
    ind = np.arange(len(buildnumbers))
    fig, ax = plt.subplots()
    rects1 = ax.bar(ind, mins, width, color='g')
    rects2 = ax.bar(ind+width, means, width, color='y', yerr=stds)
    rects3 = ax.bar(ind+2*width, maxs, width, color='r')
    ax.set_xlabel("Build number")
    ax.set_ylabel("Response time, ms")
    ax.set_title("Identity Module performance trend")
    ax.set_xticklabels(buildnumbers)
    ax.set_xticks(ind)
    ax.legend((rects1, rects2, rects3), ("Min response time",
                                         "Average response time",
                                         "Max response time"))
    plt.savefig(os.path.join(os.getcwd(), "result.png"), format="png")

# Declare datasets

buildNumbers = []
meanResponseTimes = []
minResponseTimes = []
maxResponseTimes = []
stdDevs = []
dataPath = os.path.join(os.getcwd(), "TestData")
width = 0.3

# Populate datasets

for i in sorted(os.listdir(dataPath)):
    buildNumbers.append(i)
    with open(os.path.join(dataPath, i, "global_stats.json"), "r") as f:
        results = json.load(f)
        meanResponseTimes.append(results['meanResponseTime']['total'])
        minResponseTimes.append(results['minResponseTime']['total'])
        maxResponseTimes.append(results['maxResponseTime']['total'])
        stdDevs.append(results['standardDeviation']['total'])

# Process datasets

draw_graph(minResponseTimes, maxResponseTimes, meanResponseTimes, stdDevs, buildNumbers)
