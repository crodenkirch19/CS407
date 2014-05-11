#!/usr/bin/python
import math

# print "RSSI to Feet Conversion Script"
# CS 407
# May/10/2014

# rssi = received signal strength indicator
# a = rssi 1 meter from device (avg of 10 samples)
# n = propagation constant or path-loss exponent (free space has n = 2)
rssi = -78.8
a = -78.1
n = 2

#Calculations Based Off Excel Trend Line
#x = -10
#x = -x
#print (x)
#print (0.0001*x**3 - 0.021*x**2 + 1.2796*x + 59.381)

#define conversion from meters to feet
def meters_to_feet(meters):
    return meters * 3.2808399

def convert_rssi_to_feet(rssi_num):
		y = ((rssi - a)/(-10*n))
		distanceMeters = math.pow(10, y)
		distanceFeet = meters_to_feet(distanceMeters)
		return distanceFeet

def test(rssi_test):
		print "feet:", round(convert_rssi_to_feet(rssi_test), 2)

#example usage		
#print "feet:", round(convert_rssi_to_feet(rssi), 2)

rssi = -40
test(rssi)
rssi = -50
test(rssi)
rssi = -60
test(rssi)	
rssi = -70
test(rssi)
rssi = -80
test(rssi)
rssi = -90
test(rssi)
rssi = -100
test(rssi)
