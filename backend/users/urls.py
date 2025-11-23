from django.urls import path
from . import views

urlpatterns = [
    path('profile/', views.ProfileView.as_view(), name='profile'),
    path('users/<int:pk>/', views.PublicProfileDetailView.as_view(), name='public-profile'),
    path('users/<int:pk>/follow/', views.FollowToggleAPIView.as_view(), name='follow-toggle'),
]
