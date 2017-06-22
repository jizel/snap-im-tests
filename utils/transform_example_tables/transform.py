#!/usr/bin/python

import sys
from collections import deque

def transform(infile, outfile):
    lines = None
    header = None

    with open(infile, "r") as f:
        lines = f.readlines()

    res = deque()
    for line in lines:
        line = line.replace(" ", "").strip()
        words = deque(line.split("|"))
        words.pop()
        words.popleft()
        res.append(words)

    header = res.popleft()
    result = []
    for i in range(len(header)):
        intermediate = deque()
        for line in res:
            intermediate.append(line[i])
        newline = "', '".join(intermediate)
        newline = header[i] + ": "  + "['" + newline + "']"
        result.append(newline)

    with open(outfile, 'w') as f:
        f.write("\n".join(result))


if __name__ == '__main__':
    infile = sys.argv[1]
    outfile = sys.argv[2]
    transform(infile, outfile)

