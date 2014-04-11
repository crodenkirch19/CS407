# -*- coding: utf-8 -*-
from south.utils import datetime_utils as datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models


class Migration(SchemaMigration):

    def forwards(self, orm):
        # Adding model 'Floorplan'
        db.create_table(u'beacon_api_floorplan', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(max_length=64)),
            ('blueprint', self.gf('django.db.models.fields.files.ImageField')(max_length=100)),
            ('scale', self.gf('django.db.models.fields.FloatField')()),
            ('owner', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['auth.User'])),
        ))
        db.send_create_signal(u'beacon_api', ['Floorplan'])

        # Adding model 'MobileUser'
        db.create_table(u'beacon_api_mobileuser', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('MobileUser_id', self.gf('django.db.models.fields.CharField')(max_length=21)),
        ))
        db.send_create_signal(u'beacon_api', ['MobileUser'])

        # Adding model 'Beacon'
        db.create_table(u'beacon_api_beacon', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('mac_address', self.gf('django.db.models.fields.CharField')(max_length=17)),
            ('store', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['beacon_api.Floorplan'])),
            ('location_x', self.gf('django.db.models.fields.PositiveIntegerField')()),
            ('location_y', self.gf('django.db.models.fields.PositiveIntegerField')()),
        ))
        db.send_create_signal(u'beacon_api', ['Beacon'])

        # Adding model 'Log'
        db.create_table(u'beacon_api_log', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('store', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['beacon_api.Floorplan'])),
            ('MobileUser', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['beacon_api.MobileUser'])),
        ))
        db.send_create_signal(u'beacon_api', ['Log'])

        # Adding model 'Scan'
        db.create_table(u'beacon_api_scan', (
            (u'id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('log', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['beacon_api.Log'])),
            ('beacon', self.gf('django.db.models.fields.related.ForeignKey')(to=orm['beacon_api.Beacon'])),
            ('distance', self.gf('django.db.models.fields.FloatField')()),
            ('time', self.gf('django.db.models.fields.DateTimeField')()),
        ))
        db.send_create_signal(u'beacon_api', ['Scan'])


    def backwards(self, orm):
        # Deleting model 'Floorplan'
        db.delete_table(u'beacon_api_floorplan')

        # Deleting model 'MobileUser'
        db.delete_table(u'beacon_api_mobileuser')

        # Deleting model 'Beacon'
        db.delete_table(u'beacon_api_beacon')

        # Deleting model 'Log'
        db.delete_table(u'beacon_api_log')

        # Deleting model 'Scan'
        db.delete_table(u'beacon_api_scan')


    models = {
        u'auth.group': {
            'Meta': {'object_name': 'Group'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': u"orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        u'auth.permission': {
            'Meta': {'ordering': "(u'content_type__app_label', u'content_type__model', u'codename')", 'unique_together': "((u'content_type', u'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['contenttypes.ContentType']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        u'auth.user': {
            'Meta': {'object_name': 'User'},
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'symmetrical': 'False', 'related_name': "u'user_set'", 'blank': 'True', 'to': u"orm['auth.Group']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'symmetrical': 'False', 'related_name': "u'user_set'", 'blank': 'True', 'to': u"orm['auth.Permission']"}),
            'username': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '30'})
        },
        u'beacon_api.beacon': {
            'Meta': {'object_name': 'Beacon'},
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'location_x': ('django.db.models.fields.PositiveIntegerField', [], {}),
            'location_y': ('django.db.models.fields.PositiveIntegerField', [], {}),
            'mac_address': ('django.db.models.fields.CharField', [], {'max_length': '17'}),
            'store': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['beacon_api.Floorplan']"})
        },
        u'beacon_api.floorplan': {
            'Meta': {'object_name': 'Floorplan'},
            'blueprint': ('django.db.models.fields.files.ImageField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '64'}),
            'owner': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['auth.User']"}),
            'scale': ('django.db.models.fields.FloatField', [], {})
        },
        u'beacon_api.log': {
            'Meta': {'object_name': 'Log'},
            'MobileUser': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['beacon_api.MobileUser']"}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'store': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['beacon_api.Floorplan']"})
        },
        u'beacon_api.mobileuser': {
            'Meta': {'object_name': 'MobileUser'},
            'MobileUser_id': ('django.db.models.fields.CharField', [], {'max_length': '21'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'})
        },
        u'beacon_api.scan': {
            'Meta': {'object_name': 'Scan'},
            'beacon': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['beacon_api.Beacon']"}),
            'distance': ('django.db.models.fields.FloatField', [], {}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'log': ('django.db.models.fields.related.ForeignKey', [], {'to': u"orm['beacon_api.Log']"}),
            'time': ('django.db.models.fields.DateTimeField', [], {})
        },
        u'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            u'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        }
    }

    complete_apps = ['beacon_api']