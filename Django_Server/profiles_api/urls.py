from django.conf.urls import url
from django.conf.urls import include

from rest_framework.routers import DefaultRouter

from . import views


urlpatterns = [
               
               url(r'^users/', views.UserView.as_view()),
               url(r'^friends/', views.UserFriendView.as_view()),
               url(r'^find-friends/', views.FindFriendView.as_view()),
               url(r'^profiles/', views.ProfileView.as_view()),
               url(r'^profiles-by-id/', views.ProfileByIdView.as_view()),
               url(r'^usergroups/', views.GroupView.as_view()),
               url(r'^find-group-by-user/', views.FindGroupByUserView.as_view()),
               url(r'^add-member/', views.AddMemberView.as_view()),
               url(r'^schedules/', views.ScheduleItemView.as_view()),
               url(r'^find-schedule/', views.FindScheduleItemById.as_view()),
               url(r'^find-matches/', views.FindMatchesView.as_view()),
               url(r'^log-in/', views.LogInView.as_view()),
               url(r'^chat/', views.ChatView.as_view()),
               url(r'^find-chat-by-users/', views.FindChatByParticipantsView.as_view()),
               url(r'^notification/', views.NotificationView.as_view()),
               url(r'^find-notification-by-user/', views.GetNotificationsByUserView.as_view())
               
               ]
