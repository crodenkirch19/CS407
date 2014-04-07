from django.conf.urls import patterns, include, url

from beacon_api import views

urlpatterns = patterns('',
    # Examples:
    url(r'^$', views.index, name='index'),
    url(r'send_scans/$', views.send_scans),
    # url(r'.*', views.not_found)
)
