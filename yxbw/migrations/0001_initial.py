# -*- coding: utf-8 -*-
# Generated by Django 1.10.3 on 2016-11-25 09:19
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Task',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('user', models.CharField(max_length=20)),
                ('time', models.DateTimeField(verbose_name='upload time')),
                ('name', models.CharField(max_length=80)),
                ('fullname', models.CharField(max_length=102)),
                ('accum', models.BooleanField()),
            ],
            options={
                'ordering': ['-time'],
            },
        ),
    ]