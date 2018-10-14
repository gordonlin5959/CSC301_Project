from django.db import models
from django.contrib.auth.models import AbstractBaseUser
from django.contrib.auth.models import PermissionsMixin
from django.contrib.auth.models import BaseUserManager
from django.contrib.postgres.fields import ArrayField
import base64

# Create your models here.


class UserProfileManager(BaseUserManager):
    """Helps Django work with our custom user model."""
    
    def create_user(self, email, name, password=None):
        """Creates a new user profile."""
        
        if not email:
            raise ValueError('Users must have an email address.')
        
        email = self.normalize_email(email)
        user = self.model(email=email, name=name,)
        user.set_password(password)
        user.save(using=self._db)
        
    return user

def create_superuser(self, email, name, password):
    """Creates and saves a new superuser with given details."""
        
        user = self.create_user(email, name, password)
        
        user.is_superuser = True
        user.is_staff = True
        user.save(using=self._db)
        
        return user


class User(AbstractBaseUser, PermissionsMixin):
    """
        Represents a "user profile" inside out system. Stores all user account
        related data, such as 'email address' and 'name'.
        """
    
    email = models.EmailField(max_length=255, unique=True)
    name = models.CharField(max_length=255)
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    
    objects = UserProfileManager()
    
    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['name']
    
    def get_full_name(self):
        """Django uses this when it needs to get the user's full name."""
        
        return self.name
    
    def get_short_name(self):
        """Django uses this when it needs to get the users abbreviated name."""
        
        return self.name
    
    def __str__(self):
        """Django uses this when it needs to convert the object to text."""
        
        return self.email
    
    def as_json(self):
        return dict(id=self.id,
                    email=self.email,
                    name=self.name)


class UserFriend(models.Model):
    """The UserFriend table works as a tuple, ie (Bob, Joe) (Bob, Doe) so each pair of friends is
        stored as a seperate entry"""
    
    friend1 = models.ForeignKey('User', on_delete=models.CASCADE,related_name='friend1')
    # friend's id is stored
    #friend = models.IntegerField()
    friend2 = models.ForeignKey('User', on_delete=models.CASCADE,related_name='friend2')
    
    def as_json(self):
        return dict(friend1=self.friend1.as_json(),
                    friend2=self.friend2.as_json())


class UserGroups(models.Model):
    """Table to keep track of groups"""
    group_id = models.IntegerField(unique=True)
    group_name = models.CharField(max_length=255)
    group_description = models.CharField(max_length=1000)
    
    def as_json(self):
        return dict(group_id=self.group_id,
                    group_name=self.group_name,
                    group_description=self.group_description)


class UserToGroups(models.Model):
    """(UserProfile, GroupId)"""
    user_profile = models.ForeignKey('User', on_delete=models.CASCADE)
    group = models.ForeignKey('UserGroups', on_delete=models.CASCADE)


class Profile(models.Model):
    user = models.ForeignKey('User', on_delete=models.CASCADE)
    year = models.IntegerField()
    description = models.CharField(max_length=1000)
    
    def as_json(self):
        return dict(user=self.user.as_json(),
                    year=self.year,
                    description=self.description)


class UserSchedule(models.Model):
    user = models.ForeignKey('User', on_delete=models.CASCADE)
    class_name = models.CharField(max_length=5000)
    lecture_section = models.CharField(max_length=5000)
    
    def as_json(self):
        return dict(user = self.user.as_json(),
                    class_name = self.class_name,
                    lecture_section= self.lecture_section)


class ChatMessageStorage(models.Model):
    user_to = models.ForeignKey('User', on_delete=models.CASCADE,related_name='user_to')
    
    user_from = models.ForeignKey('User', on_delete=models.CASCADE,related_name='user_from')
    message = models.CharField(max_length=5000)
    number = models.IntegerField(default=-1)
    
    def as_json(self):
        return dict(user_from = self.user_from.as_json(),
                    user_to = self.user_to.as_json(),
                    message= self.message,
                    number=self.number)


class NotificationsStorage(models.Model):
    
    notification_receiver = models.ForeignKey('User', on_delete=models.CASCADE,related_name='receiver')
    
    notification_sender = models.ForeignKey('User', on_delete=models.CASCADE,related_name='sender')
    
    message = models.CharField(max_length=5000)
    
    number = models.IntegerField(unique=True)
    
    type = models.CharField(max_length=50, default="")
    
    def as_json(self):
        return dict(notification_receiver=self.notification_receiver.as_json(),
                    notification_sender=self.notification_sender.as_json(),
                    message=self.message,
                    number=self.number,
                    type=self.type)
