from django.conf.urls import patterns, include, url

from beacon_app import views

urlpatterns = patterns('',
    # Examples:
    url(r'^$', views.index),
    url(r'^logs/$', views.logs),
    url(r'^stores/$', views.stores_index),
    url(r'^stores/(\d+)$', views.store_detail),
    (r'^login/$', 'django.contrib.auth.views.login'),
    (r'^logout/$', 'django.contrib.auth.views.logout', {'next_page': '/'}),

)
