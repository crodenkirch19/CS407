from django.db import models
from django.contrib import auth

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

        # Triangulate (estimate) the user's position given this information

# Represents a single scan instance on a single Beacon
class Scan(models.Model):
    log = models.ForeignKey(Log)
    beacon = models.ForeignKey(Beacon)
    distance = models.FloatField()
    time = models.DateTimeField()