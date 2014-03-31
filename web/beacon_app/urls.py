from django.conf.urls import patterns, include, url

from beacon_app import views

urlpatterns = patterns('',
    # Examples:
    url(r'^$', views.index, name='index'),
    # url(r'^blog/', include('blog.urls')),
)
