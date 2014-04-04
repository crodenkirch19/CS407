from django.contrib import admin
from beacon_api import models

admin.site.register(models.Floorplan)
admin.site.register(models.MobileUser)
admin.site.register(models.Beacon)
admin.site.register(models.Log)
admin.site.register(models.Scan)
