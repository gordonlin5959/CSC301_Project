from django.contrib import admin
from . import models
# Register your models here.
admin.site.register(models.User)
admin.site.register(models.UserGroups)
admin.site.register(models.UserFriend)
admin.site.register(models.UserToGroups)
admin.site.register(models.Profile)
admin.site.register(models.UserSchedule)
admin.site.register(models.ChatMessageStorage)
admin.site.register(models.NotificationsStorage)
