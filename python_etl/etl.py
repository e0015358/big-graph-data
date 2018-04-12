# -*- coding: utf-8 -*-
import csv

new_data = []
with open('201801_fordgobike_tripdata.csv', 'rb') as csvfile:
    header = csvfile.readline()
    # print header
    # print "="*50
    reader = csv.reader(csvfile)
    # start_rows = []
    # end_rows = []
    combined_rows = []
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
        start_row.append('start')
        end_row.append('end')
        combined_rows.append(start_row)
        combined_rows.append(end_row)

with open('bike_trips.csv', 'wb') as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(["time", "station_id", "station_name", "latitude", "longtitude", "bike_id", "status"])
    writer.writerows(row for row in combined_rows)

# with open('end_trips.csv', 'wb') as csvfile:
#     writer = csv.writer(csvfile)
#     writer.writerow(["time", "station_id", "station_name", "latitude", "longtitude", "bike_id", "status"])
#     writer.writerows(row for row in end_rows)
