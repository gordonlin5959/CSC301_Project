from django.shortcuts import render

from rest_framework import viewsets
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.authentication import TokenAuthentication
from rest_framework import filters
from rest_framework.authtoken.serializers import AuthTokenSerializer
from rest_framework.authtoken.views import ObtainAuthToken
from django.core import serializers
from django.http import JsonResponse
import json

from . import models
from . import permissions
import base64

# Create your views here.


class UserView(APIView):
    """Handles creating, creating and updating users."""
    
    def get(self, request, format=None):
        """All users, showing their id,email,name."""
        users = [user.as_json() for user in models.User.objects.all()]
        
        return JsonResponse(users, safe=False)
    
    def post(self, request):
        """Create a new user.
            Request should be a JSON object of email:< >, name:< >, password:<>"""
        
        data = json.loads(request.body)
        user_name = data['name']
        user_email = data['email']
        user_password =data['password']
        
        u1 = models.User.objects.filter(email=user_email)
        for user in u1:
            return JsonResponse({"id": -1})
        
        new_user = models.User(email=user_email,
                               name=user_name)
                               new_user.set_password(user_password)
                               new_user.save()
                               return JsonResponse(new_user.as_json(),safe=False)

def put(self, request, pk=None):
    """Updates user given by id"""
        
        data = json.loads(request.body)
        user_id = data['id']
        user_name = data['name']
        user_email = data['email']
        user_password =data['password']
        
        user = models.User.objects.get(id=user_id)
        user.name = user_name
        user.password = user_password
        user.save()
        
        return JsonResponse({user.as_json()}, safe=False)
    
    def delete(self, request, pk=None):
        """Deletes the user given by id."""
        
        data = json.loads(request.body)
        user_id = data['id']
        user = models.User.objects.get(id=user_id)
        j = JsonResponse({ user.as_json()}, safe=False)
        user.delete()
        return j


class UserFriendView(APIView):
    
    """requests should contain JSON with {'userid': < >, 'friendid': < >'} """
    
    def get(self,request):
        """request contains id of user we want to list the friends of """
        friendships = [friendship.as_json() for friendship in models.UserFriend.objects.all()]
        
        return JsonResponse(friendships, safe=False)
    
    def post(self,request):
        """add a new (user, other user) into the table"""
        
        data = json.loads(request.body)
        user_id = data['id']
        friend_id = data['friendid']
        user = models.User.objects.get(id=user_id)
        friend = models.User.objects.get(id=friend_id)
        
        new_friendship = models.UserFriend(friend1=user,
                                           friend2=friend)
                                           new_friendship.save()
                                           
        return JsonResponse(new_friendship.as_json(), safe=False)
    
    def put(self, request, pk=None):
        """delete """
        data = json.loads(request.body)
        user_id = data['id']
        friend_id = data['friendid']
        user = models.User.objects.get(id=user_id)
        friend = models.User.objects.get(id=friend_id)
        
        friendship = models.UserFriend.objects.get(friend1=friend, friend2=user)
        j = JsonResponse({'friendship deleted':friendship.as_json()}, safe=False)
        friendship.delete()
        return j


class FindFriendView(APIView):
    """The request should contain the id of the user to find the friends of """
    
    def post(self,request):
        """add a new (user, other user) into the table"""
        
        data = json.loads(request.body)
        user_id = data['id']
        user = models.User.objects.get(id=user_id)
        
        friends = models.UserFriend.objects.filter(friend1=user)
        l = [friend.friend2.as_json() for friend in friends]
        return JsonResponse({"friends": l}, safe=False)


class ProfileView(APIView):
    
    def get(self, request):
        
        profiles = models.Profile.objects.all()
        return JsonResponse([profile.as_json() for profile in profiles],safe=False)
    
    def post(self, request):
        
        data = json.loads(request.body)
        user_id = data['id']
        user = models.User.objects.get(id=user_id)
        year = data['year']
        description = data['description']
        name = data['name']
        
        p = models.Profile.objects.filter(user=user)
        if len(p) != 0:
            p[0].name = name
            p[0].description = description
            p[0].year = year
            
            p[0].save()
            return JsonResponse( p[0].as_json())
        else:
            p = models.Profile(user=user,
                               year=year,
                               description=description)
                               p.save()
                               
            return JsonResponse( p.as_json())


def put(self, request, pk=None):
    data = json.loads(request.body)
        user_id = data['id']
        user = models.User.objects.get(id=user_id)
        year = data['year']
        description = data['description']
        name = data['name']
        
        user.name = name
        user.save()
        p = models.Profile.objects.get(user=user)
        p.year = year
        p.description = description
        p.save()
        
        return JsonResponse(p.as_json())


class ProfileByIdView(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        user_id = data['id']
        user = models.User.objects.get(id=user_id)
        profile = models.Profile.objects.get(user=user)
        return JsonResponse(profile.as_json(), safe=False)


class GroupView(APIView):
    def get(self, request):
        
        groups = models.UserGroups.objects.all()
        list_of_groups = []
        for group in groups:
            d = dict()
            d['group_id'] = group.group_id
            d['group_name'] = group.group_name
            d['group_description'] = group.group_description
            d['members'] = []
            members = models.UserToGroups.objects.filter(group=group)
            
            for member in members:
                d['members'].append(member.user_profile.as_json())
            
            list_of_groups.append(d)
        
        return JsonResponse({"groups" :list_of_groups},safe=False)
    
    def post(self, request):
        
        data = json.loads(request.body)
        group_name = data['group_name']
        group_description = data['group_description']
        
        groups = models.UserGroups.objects.all()
        id = len(groups) + 1
        
        group = models.UserGroups(group_id=id,
                                  group_name=group_name,
                                  group_description=group_description)
                                  group.save()
                                  
        return JsonResponse(group.as_json(),safe=False)
    
    def put(self, request,pk=None):
        
        data = json.loads(request.body)
        group_name = data['group_name']
        group_description = data['group_description']
        group_id = data['group_id']
        
        group = models.UserGroups.objects.get(group_id=group_id)
        group.group_name = group_name
        group.group_description = group_description
        
        group.save()
        return JsonResponse(group.as_json(),safe=False)
    
    def delete(self,request, pk=None):
        
        data = json.loads(request.body)
        group_id = data['group_id']
        group = models.UserGroups.objects.get(group_id=group_id)
        j = JsonResponse(group.as_json())
        group.delete()
        return j


class FindGroupByUserView(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        id = data['user_id']
        groups_list = []
        user = models.User.objects.get(id=id)
        
        groups = models.UserToGroups.objects.filter(user_profile=user)
        
        for group in groups:
            groups_list.append(group.group.as_json())
        
        return JsonResponse({"groups": groups_list})


class AddMemberView(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        group_id = data['group_id']
        user_id = data['user_id']
        group = models.UserGroups.objects.get(group_id=group_id)
        user = models.User.objects.get(id=user_id)
        
        utg = models.UserToGroups(user_profile=user,
                                  group=group)
                                  utg.save()
                                  
        return JsonResponse({"member added": user.as_json()}, safe=False)
    
    def delete(self, request, pk=None):
        data = json.loads(request.body)
        group_id = data['group_id']
        user_id = data['user_id']
        group = models.UserGroups.objects.get(group_id=group_id)
        user = models.User.objects.get(id=user_id)
        
        utg = models.UserToGroups.objects.get(group=group,user_profile=user)
        j = JsonResponse({"user deleted": user.as_json()}, safe=False)
        utg.delete()
        return j


class ScheduleItemView(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        user_id = data['user_id']
        class_name = data['class_name']
        lecture_section = data['lecture_section']
        user = models.User.objects.get(id=user_id)
        
        s = models.UserSchedule(user=user,
                                class_name=class_name,
                                lecture_section=lecture_section)
                                s.save()
                                
        return JsonResponse({'schedule item created' : s.as_json()}, safe=False)
    
    def delete(self, request, pk=None):
        
        data = json.loads(request.body)
        user_id = data['user_id']
        class_name = data['class_name']
        
        user = models.User.objects.get(id=user_id)
        
        s = models.UserSchedule.objects.get(user=user,class_name=class_name)
        
        j = JsonResponse({"schedule event deleted" : s.as_json()}, safe=False)
        
        s.delete()
        
        return j


class FindScheduleItemById(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        user_id = data['user_id']
        user = models.User.objects.get(id=user_id)
        schedule_list = models.UserSchedule.objects.filter(user=user)
        
        return JsonResponse([s.as_json() for s in schedule_list], safe=False)


class FindMatchesView(APIView):
    
    def post(self, request):
        
        data = json.loads(request.body)
        user_id = data['user_id']
        user = models.User.objects.get(id=user_id)
        schedule_list = models.UserSchedule.objects.filter(user=user)
        matched_users = []
        
        for schedule in schedule_list:
            matched_schedules = models.UserSchedule.objects.filter(class_name=schedule.class_name)
            for matched_schedule in matched_schedules:
                
                # if the matched schedule is not from this user
                if matched_schedule.user.id != user_id:
                    
                    profile = models.Profile.objects.get(user=matched_schedule.user)
                    
                    # if there is nothing in the list of matched users
                    if len(matched_users) == 0:
                        l = []
                        l.append(schedule.class_name)
                        matched_users.append({"name": matched_schedule.user.name,
                                             "description": profile.description,
                                             "year": profile.year,
                                             "matched class": l,
                                             "matched_user_id": matched_schedule.user.id})
                    else:
                        
                        x = True
                        
                        for item in matched_users:
                            if item["matched_user_id"] == matched_schedule.user.id:
                                # if a matched user already in the list
                                y = True
                                
                                # find if the matches class is already in the list
                                for c in item["matched class"]:
                                    
                                    if c == matched_schedule.class_name:
                                        y = False
                        
                            if y:
                                item["matched class"].append(matched_schedule.class_name)
                                
                                x = False
            
                    if x:
                        l = []
                            l.append(schedule.class_name)
                            matched_users.append({"name": matched_schedule.user.name,
                                                 "description": profile.description,
                                                 "year": profile.year,
                                                 "matched class": l,
                                                 "matched_user_id": matched_schedule.user.id})
    
    return JsonResponse({"matches": matched_users},safe=False)


class LogInView(APIView):
    
    def post(self, request):
        
        data = json.loads(request.body)
        email = data['email']
        password = data['password']
        
        user = models.User.objects.filter(email=email)
        
        
        for u in user:
            if u.check_password(password):
                return JsonResponse(u.as_json())
        
    return JsonResponse({"id": -1})


class ChatView(APIView):
    
    def post(self, request):
        
        data = json.loads(request.body)
        user_to_id = data["user_to_id"]
        user_from_id = data["user_from_id"]
        message = data["message"]
        number = len(models.ChatMessageStorage.objects.all()) + 1
        
        user_to = models.User.objects.get(id=user_to_id)
        user_from = models.User.objects.get(id=user_from_id)
        
        c = models.ChatMessageStorage(user_to=user_to,
                                      user_from=user_from,
                                      message=message,
                                      number=number)
            
                                      c.save()
                                      
        return JsonResponse({"message send":c.as_json()})


class FindChatByParticipantsView(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        user1_id = data["user1_id"]
        user2_id = data["user2_id"]
        
        user1 = models.User.objects.get(id=user1_id)
        user2 = models.User.objects.get(id=user2_id)
        
        user1_to_user2_messages = models.ChatMessageStorage.objects.filter(user_to=user1,user_from=user2)
        user2_to_user1_messages = models.ChatMessageStorage.objects.filter(user_to=user2, user_from=user1)
        
        messages = []
        
        for message in user1_to_user2_messages:
            messages.append(message)
        
        for message in user2_to_user1_messages:
            messages.append(message)
        
        chrono_messages = sorted(messages,key=lambda x: x.number, reverse=False)
        
    return JsonResponse({"messages": [m.as_json() for m in chrono_messages]})


class NotificationView(APIView):
    
    def post(self, request):
        data = json.loads(request.body)
        sender_id = data["sender_id"]
        receiver_id = data["receiver_id"]
        message = data["message"]
        
        sender = models.User.objects.get(id=sender_id)
        receiver = models.User.objects.get(id=receiver_id)
        
        ntype = data["type"]
        
        number = len(models.NotificationsStorage.objects.all()) + 1
        n = models.NotificationsStorage(notification_sender=sender,
                                        notification_receiver=receiver,
                                        message=message,
                                        number=number,
                                        type=ntype)
                                        n.save()
                                        
        return JsonResponse(n.as_json())
    
    def put(self, request,pk=None):
        
        data = json.loads(request.body)
        number = data["number"]
        
        notification = models.NotificationsStorage.objects.get(number=number)
        
        j = JsonResponse({"deleted":notification.as_json()})
        
        notification.delete()
        
        return j


class GetNotificationsByUserView(APIView):
    
    def post(self,request):
        data = json.loads(request.body)
        user_id = data['user_id']
        
        user = models.User.objects.get(id=user_id)
        
        notifications = models.NotificationsStorage.objects.filter(notification_receiver=user)
        
        jsArray = []
        
        for notification in notifications:
            jsArray.append(notification.as_json())
        
        return  JsonResponse({"notifications":jsArray},safe=False)

