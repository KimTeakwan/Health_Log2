from django.urls import path
from .views import (
    VideoListCreateAPIView,
    VideoDetailAPIView,
    LikeToggleAPIView,
    CommentCreateAPIView,
    CommentAdoptAPIView,
    ReportVideoAPIView,
)

urlpatterns = [
    path('videos/', VideoListCreateAPIView.as_view(), name='video-list-create'),
    path('videos/<int:pk>/', VideoDetailAPIView.as_view(), name='video-detail'),
    path('videos/<int:pk>/likes/', LikeToggleAPIView.as_view(), name='video-like'),
    path('videos/<int:pk>/report/', ReportVideoAPIView.as_view(), name='video-report'),
    path('videos/<int:pk>/comments/', CommentCreateAPIView.as_view(), name='comment-create'),
    path('comments/<int:pk>/adopt/', CommentAdoptAPIView.as_view(), name='comment-adopt'),
]
