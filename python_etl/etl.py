# -*- coding: utf-8 -*-
import csv

new_data = []
with open('201801_fordgobike_tripdata.csv', 'rb') as csvfile:
    header = csvfile.readline()
    # print header
    # print "="*50
    reader = csv.reader(csvfile)
    start_rows = []
    end_rows = []
    for row in reader:
        start_row = []
        end_row = []
        for idx, val in enumerate(row):
            if idx == 1:
                start_row.append(val)
            elif idx == 2:
                end_row.append(val)
            elif idx == 3:
                start_row.append(val)
            elif idx == 4:
                start_row.append(val)
            elif idx == 5:
                start_row.append(val)
            elif idx == 6:
                start_row.append(val)
            elif idx == 7:
                end_row.append(val)
            elif idx == 8:
                end_row.append(val)
            elif idx == 9:
                end_row.append(val)
            elif idx == 10:
                end_row.append(val)
            elif idx == 11:
                end_row.append(val)
                start_row.append(val)
        start_rows.append(start_row)
        end_rows.append(end_row)

with open('start_trips.csv', 'wb') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(["time", "station_id", "station_name", "latitude", "longtitude", "bike_id"])
    writer.writerows(row for row in start_rows)

with open('end_trips.csv', 'wb') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(["time", "station_id", "station_name", "latitude", "longtitude", "bike_id"])
    writer.writerows(row for row in end_rows)
