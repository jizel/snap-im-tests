#!/usr/bin/env python3

import sys

def convert(listofstrings):
    resultlist = []
    for string in listofstrings:
        string = string.lstrip()
        # Cut out left and right table delimiter
        string = string[1:-2]
        tokens = string.split("|")
        newstring = ",".join([i.strip() for i in tokens])
        resultlist.append(newstring)
    return "\n".join(resultlist)

if __name__ == '__main__':
    result = None
    try:
        with open(sys.argv[1], 'r') as source:
            result = convert(source.readlines())
    except IndexError:
        print("""You should provide path to file with cucumber example
table as the script parameter: %s $path_to_file""" % sys.argv[0])
        exit(1)
    print(result)
    with open("result", "w") as destination:
        destination.write(result)
