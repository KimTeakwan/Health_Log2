from django.urls import path
from . import views

urlpatterns = [
    path('profile/', views.ProfileView.as_view(), name='profile'),
    path('users/<int:pk>/', views.PublicProfileDetailView.as_view(), name='public-profile'),
    path('users/<int:pk>/follow/', views.FollowToggleAPIView.as_view(), name='follow-toggle'),
    path('users/<int:pk>/followers/', views.FollowerListView.as_view(), name='follower-list'),
    path('users/<int:pk>/following/', views.FollowingListView.as_view(), name='following-list'),
]
