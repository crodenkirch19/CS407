from django.conf.urls import patterns, include, url

from beacon_app import views

urlpatterns = patterns('',
    # Examples:
    url(r'^$', views.index),
    url(r'^logs/$', views.print_logs),
    url(r'^stores/$', views.stores_index),
    url(r'^stores/(\d+)/$', views.store_detail),
    url(r'^stores/(\d+)/scans/$', views.store_scans),
    (r'^login/$', 'django.contrib.auth.views.login'),
    (r'^logout/$', 'django.contrib.auth.views.logout', {'next_page': '/'}),

)
