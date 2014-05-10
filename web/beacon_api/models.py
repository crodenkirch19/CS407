from django.db import models
from django.contrib import auth
from beacon_api.shared_functions import triangulate

# A single floorplan representing a store
class Floorplan(models.Model):
    name = models.CharField(max_length=40)
    blueprint = models.ImageField(upload_to="beacon_app/static/blueprint")
    # Ratio of PIXELS to FEET
    scale = models.FloatField()
    owner = models.ForeignKey(auth.models.User)

    def blueprint_url(self):
        return "/" + "/".join(self.blueprint.url.split('/')[1:])

# Single MobileUser of our mobile app.
# We only need to store their Google+ MobileUserId.
class MobileUser(models.Model):
    MobileUser_id = models.CharField(max_length=21)

# Represents a single beacon
class Beacon(models.Model):
    mac_address = models.CharField(max_length=17)
    store = models.ForeignKey(Floorplan)
    # Location data is stored relative to the floorplan image
    # Top-left pixel is (0,0)
    location_x = models.PositiveIntegerField()
    location_y = models.PositiveIntegerField()

# Represents a single comlete scan instance
# Each log will have data from multiple beacons
# But only data from a single MobileUser in a single location
class Log(models.Model):
    store = models.ForeignKey(Floorplan)
    MobileUser = models.ForeignKey(MobileUser)

    def calc_user_path(self):
        """
        Calculates a nx3 array of the user's path during this scan
        First column is x location, measured in FEET
        Second column is y location
        Third column is time, a datetime object.
        """
        # Get an array of Scan objects corresponding to this log
        # Each of these gives the distance to a given beacon at a given time
        scans = Scan.objects.filter(log=self)

        # Scan pattern goes like:
        # 1 second scan, 5 second rest, 1 second scan, etc.
        # So first clump together all scans in this interval
        scan_groups = []
        scan_group = [scans[0]]
        scan_groups.append(scan_group)
        scan_duration = 1.5
        for scan in scans[1:]:
            # Does this new scan belong in the current scan group?
            dt = scan.time - scan_group[0].time
            if dt.total_seconds() < scan_duration:
                # Add it to current scan group
                scan_group.append(scan)
            else:
                # Make a new scan group
                scan_group = []
                scan_group.append(scan)
                scan_groups.append(scan_group)

        # Now simplify each scan group to average the values from each beacon
        # A list of scan groups
        # Each scan group is a list of tuples (distance, beacon, time)
        simplified_groups = []
        for group in scan_groups:
            simplified_group = []

            # Get all beacons in this scan group
            beacons = [scan.beacon for scan in group]
            beacons = set(beacons)

            # Now average values for each beacon
            for beacon in beacons:
                matching_scans = [scan for scan in group if scan.beacon == beacon]
                distances = [scan.calc_distance() for scan in matching_scans]
                avg_distance = float(sum(distances)) / len(distances)
                simplified_group.append((avg_distance, beacon, matching_scans[0].time))

            simplified_groups.append(simplified_group)

        # Triangulate (estimate) the user's position given this information
        return_list = []
        for group in simplified_groups:
            beacon_args = []
            for scan in group:
                beacon_args.append(scan[1].location_x)
                beacon_args.append(scan[1].location_y)
                beacon_args.append(scan[0])
            good_value, xpos, ypos = triangulate(42, 250, 20, 180, *beacon_args)
            if good_value:
                return_list.append((xpos, ypos, group[0].time))

        return return_list

# Represents a single scan instance on a single Beacon
class Scan(models.Model):
    log = models.ForeignKey(Log)
    beacon = models.ForeignKey(Beacon)
    # Units = RSSI
    distance = models.FloatField()
    time = models.DateTimeField()

    def calc_distance(self):
        """
        Calculates the distance in feet from the beacon
        """
        return self.distance