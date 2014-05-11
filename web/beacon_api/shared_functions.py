'''
Created on Apr 22, 2014

@author: Charles Rodenkrich
'''

'''
Takes in the following
mapMinX = x value defining bottom border of map
mapMaxX = x value defining top border of map
mapMinY = y value defining bottom border of map
mapMaxY = y value defining top border of map
followed by any number of beacons which should be entered exactly the same as the first is defined below
RETURNS
three values are returned (useableValue, xUser, yUser)
useable value is 1 if the location was successfully calculated, it is 0 if there was an error and the function was unable to calculate the location
if useable value is returned as 0, an error message should be displayed
userX and userY correspond to the calculated location of the user
'''
import itertools
import math

def triangulate(mapMinX, mapMaxX, mapMinY, mapMaxY, firstBeaconLocationX, firstBeaconLocationY, firstbeaconDist, *otherBeacons):

    '''
    The following variables need to be set for each implementation
    resolution refers to the distance unit that this function is accurate too, a smaller value results in slower function
    '''
    resolution = 0.1
    useableValue = 1
    xUser = 0
    yUser = 0


    '''
    checks to make sure you entered the correct number of argument
    '''
    if (len(otherBeacons)%3) > 0:
        print("Incorrect number of arguments, for each beacon you must endter its x location, y location, and strength, so number of arguments should always be a multiple of three")
        print("funciton will exit now")
        useableValue = 0
        return (useableValue, xUser, yUser)
    if (len(otherBeacons) < 3):
        print("You must have at least two beacons to triangulate on the map")
        useableValue = 0
        return (useableValue, xUser, yUser)

    '''
    number of beacons
    '''
    numBeacons = 1 + (len(otherBeacons)/3)

    '''
    Lists to store the x, y, and strengths of each beacon in order
    '''
    beaconXPoints = list()
    beaconYPoints = list()
    beaconDists = list()

    '''
    add first beacon to the lists
    '''
    beaconXPoints.append(firstBeaconLocationX)
    beaconYPoints.append(firstBeaconLocationY)
    beaconDists.append(firstbeaconDist)

    '''
   parse the other beacons into the list
    '''
    count = 0

    for item in otherBeacons:

        if (count == 0):
            beaconXPoints.append(item)
        if (count == 1):
            beaconYPoints.append(item)
        if (count == 2):
            beaconDists.append(item)

        count = count + 1
        if (count == 3):
            count = 0

    '''
    if you are triangulating using only two beacons, they must both be located along the same edge of the map
    this checks to see if that is true and returns an error if now
    '''
    onEdge = 0
    if (numBeacons == 2):
        if(beaconXPoints[0] == mapMinX):
            if(beaconXPoints[1]== mapMinX):
                onEdge = 1
        if(beaconXPoints[0] == mapMaxX):
            if(beaconXPoints[1]== mapMaxX):
                onEdge = 1
        if(beaconXPoints[0] == mapMinY):
            if(beaconXPoints[1]== mapMinY):
                onEdge = 1
        if(beaconXPoints[0] == mapMaxY):
            if(beaconXPoints[1]== mapMaxY):
                onEdge = 1
        if(onEdge == 0):
            print('You are only using two beacons for triangulation, this is only possible if these beacons are both located on the same edge of the map')
            print('the beacons you entered do not satisfy this condition')
            useableValue = 0
            return (useableValue, xUser, yUser)

    '''
    so at this point in time we can determine a range of x and y values that our individual must be located within
    each beacon gives a circle path where the user might be relative to that beacon, with the center of the circle at the x,y of the beacon and that circles radius equal to the beaconDist
    Each circle has a maximum and minimum x and y value and we know that the user must lie within the quadrant defined by these values
    The x y quadrant containing all areas overlapped by all circles can then be calculated using the code below
    First we will do this to find the X range the user must be in, then we will find the Y range the user must be in
    '''
    yUserMax = mapMaxY
    yUserMin = mapMinY
    xUserMax = mapMaxX
    xUserMin = mapMinX

    for m in range(0, numBeacons):

        maxPossibleX = beaconXPoints[m] + (beaconDists[m])
        minPossibleX = beaconXPoints[m] - (beaconDists[m])
        if (xUserMin < minPossibleX):
            xUserMin = minPossibleX
        if (xUserMax > maxPossibleX):
            xUserMax = maxPossibleX

        maxPossibleY = beaconYPoints[m] + (beaconDists[m])
        minPossibleY = beaconYPoints[m] - (beaconDists[m])
        if (yUserMin < minPossibleY):
            yUserMin = minPossibleY
        if (yUserMax > maxPossibleY):
            yUserMax = maxPossibleY

    '''
    if the beacon distance calculated has an error larger that the value maxError is set too, it is possible that there may not be any location on the map for which all circles created by the beacons overlap
    if this is true then your min values may be larger than you max, below this is checked for and an error is thrown if it is found
    '''
    performYCalc = 1
    performXCalc = 1
    if(yUserMax < yUserMin):
        performYCalc = 0
    if(xUserMax < xUserMin):
        performXCalc = 0
    if(performXCalc == 0):
        if(performYCalc == 0):
            print('error: either your map boundaries or beacon values have been entered incorrectly or')
            print('please retry entering your values or increase the maxError value in this function')
            useableValue = 0
            return (useableValue, xUser, yUser)


    '''
    now things get a little more complex, we have a quadrant defined by max/min x/y values which we know the user must be inside
    here is the method i've devised for finding the users location, this method will first be done with x values
    for the x range we know the user must be in we produce a list that holds every x value between min and max with a space between x values the size of the variable resolution
    we then calculate the two possible y values for each "beacon circle" at each x point.
    we then figure out which combination of y values has the lowest standard deviation (two options of y for each beacon point)
    We then find the standard deviation of the chosen y values calculated from each circle equation
    we know that the correct x value should have the lowest standard deviation between the calculated y points.
    We save this lowest STD x value as the users X value and then calculate the users Y value by average the y values output by the circle equations at that x value
    After this is complete we have a estimated users x and y position
    '''
    if(performXCalc == 1):
        possibleXValues = list()
        calculatedYValues = list()
        bestGuessXfromX = 0
        lowestSTD = float('inf')

        inputValue = xUserMin
        while(inputValue < xUserMax):
            possibleXValues.append(inputValue)
            inputValue = inputValue + resolution
        if(xUserMin == xUserMax):
            possibleXValues.append(xUserMin)

        for item in possibleXValues:

            lowYValues = list()
            highYValues = list()
            listOfLowHigh = list()

            for pointer in range (0, numBeacons):
                lowYValues.append(beaconYPoints[pointer] - math.sqrt(beaconDists[pointer]**2 - (item - beaconXPoints[pointer])**(2)))
                highYValues.append(beaconYPoints[pointer] + math.sqrt(beaconDists[pointer]**2 - (item - beaconXPoints[pointer])**(2)))

            for beacPoint in range (0, numBeacons):
                listOfLowHigh.append([lowYValues[beacPoint], highYValues[beacPoint]])

            for combination in itertools.product(*listOfLowHigh):
                STD = standardDev(combination)
                if(STD < lowestSTD):
                    calculatedYValues = flatten(combination)
                    lowestSTD = STD
                    bestGuessXfromX = item
        bestGuessYfromX = math.fsum(calculatedYValues) * 1.0 / len(calculatedYValues)

    '''
    This is probably not needed but i think will add to accuracy. Here we repeat what was performed in the previous section
    except instead of starting with a list of x values, we start with a list of y values and calculated the x values and their standard deriation
    again this produces a second estimation of the users x and y position
    '''
    if(performYCalc):
        possibleYValues = list()
        calculatedXValues = list()
        bestGuessYfromY = 0
        lowestSTD = float('inf')

        inputValue = yUserMin
        while(inputValue < yUserMax):
            possibleYValues.append(inputValue)
            inputValue = inputValue + resolution
        if(yUserMin == yUserMax):
            possibleYValues.append(yUserMin)

        for item in possibleYValues:

            lowXValues = list()
            highXValues = list()
            listOfLowHigh = list()

            for pointer in range (0, numBeacons):
                lowXValues.append(beaconXPoints[pointer] - math.sqrt(beaconDists[pointer]**2 - (item - beaconYPoints[pointer])**(2)))
                highXValues.append(beaconXPoints[pointer] + math.sqrt(beaconDists[pointer]**2 - (item - beaconYPoints[pointer])**(2)))

            for beacPoint in range (0, numBeacons):
                listOfLowHigh.append([lowXValues[beacPoint], highXValues[beacPoint]])

            for combination in itertools.product(*listOfLowHigh):
                STD = standardDev(combination)
                if(STD < lowestSTD):
                    calculatedXValues = flatten(combination)
                    lowestSTD = STD
                    bestGuessYfromY = item
        bestGuessXfromY = math.fsum(calculatedXValues) * 1.0 / len(calculatedXValues)


    '''
    then we average our two estimations
    '''
    if(performXCalc == 1):
        xUser = bestGuessXfromX
        yUser = bestGuessYfromX
    if(performYCalc == 1):
        xUser = bestGuessXfromY
        yUser = bestGuessYfromY
    if(performXCalc == 1):
        if(performYCalc == 1):
            xUser = (bestGuessXfromX + bestGuessXfromY)/2
            yUser = (bestGuessYfromX + bestGuessYfromY)/2


    '''
    return the final best estimations
    '''
    return (useableValue, xUser, yUser)

def flatten(l):
    '''Flatten a arbitrarily nested lists and return the result as a single list.
    '''
    ret = []
    for i in l:
        if isinstance(i, list) or isinstance(i, tuple):
            ret.extend(flatten(i))
        else:
            ret.append(i)
    return ret

def standardDev(inputList):
    s = flatten(inputList)
    avg = math.fsum(s) * 1.0 / len(s)

    variance = map(lambda x: (x - avg)**2, s)

    avgVar = math.fsum(variance) * 1.0 / len(variance)
    stdDev = (avgVar)**(0.5)

    return stdDev


# ===== RSSI UTILITIES =====

# rssi = received signal strength indicator
# a = rssi 1 meter from device (avg of 10 samples)
# n = propagation constant or path-loss exponent (free space has n = 2)
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

def rssi_to_feet(rssi):
        y = ((rssi - a)/(-10*n))
        distanceMeters = math.pow(10, y)
        distanceFeet = meters_to_feet(distanceMeters)
        return distanceFeet

def test(rssi_test):
        print "feet:", round(rssi_to_feet(rssi_test), 2)