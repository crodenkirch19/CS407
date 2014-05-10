from django.contrib import admin
from beacon_api import models

admin.site.register(models.Floorplan)
admin.site.register(models.MobileUser)

class BeaconAdmin(admin.ModelAdmin):
    list_display = ('mac_address',)

admin.site.register(models.Beacon, BeaconAdmin)

admin.site.register(models.Log)

class ScanAdmin(admin.ModelAdmin):
    list_display = ('beacon', 'time', 'distance')

admin.site.register(models.Scan, ScanAdmin)
