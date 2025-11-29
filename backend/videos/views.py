from django.db.models import Q, Count, F
from rest_framework import generics, permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.generics import get_object_or_404
from .models import Video, Like, Comment, Tag
from .serializers import VideoSerializer, CommentSerializer
from users.models import TrainerProfile

class VideoListCreateAPIView(generics.ListCreateAPIView):
    serializer_class = VideoSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        user = self.request.user

        if user.is_authenticated:
            following_ids = user.following_set.values_list('following_id', flat=True)
            allowed_videos = Q(visibility='public') | \
                             Q(visibility='followers_only', uploader_id__in=list(following_ids)) | \
                             Q(uploader=user)
            queryset = Video.objects.filter(allowed_videos)
        else:
            queryset = Video.objects.filter(visibility='public')

        # Search by query (title, description, tags)
        query = self.request.query_params.get('q', None)
        if query:
            queryset = queryset.filter(
                Q(title__icontains=query) |
                Q(description__icontains=query) |
                Q(tags__name__icontains=query)
            ).distinct()

        # Sorting
        sort_by = self.request.query_params.get('sort_by', 'latest') # Default to latest
        if sort_by == 'popular':
            queryset = queryset.annotate(likes_count_total=Count('likes')).order_by('-likes_count_total', '-created_at')
        elif sort_by == 'adopted':
            queryset = queryset.annotate(adopted_comments_count_total=Count('comments', filter=Q(comments__is_adopted=True))).order_by('-adopted_comments_count_total', '-created_at')
        else: # 'latest' or any other value
            queryset = queryset.order_by('-created_at')
            
        return queryset.distinct()

    def perform_create(self, serializer):
        tag_names = serializer.validated_data.pop('tags', [])
        video = serializer.save(uploader=self.request.user)
        
        tags = []
        for name in tag_names:
            tag, _ = Tag.objects.get_or_create(name=name)
            tags.append(tag)
        
        video.tags.set(tags)

from rest_framework.exceptions import PermissionDenied

class VideoDetailAPIView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Video.objects.all()
    serializer_class = VideoSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

    def get_object(self):
        video = super().get_object()
        user = self.request.user

        if video.visibility == 'public':
            return video

        if not user.is_authenticated:
            raise PermissionDenied("You must be logged in to view this video.")

        is_uploader = video.uploader == user
        is_follower = user.following_set.filter(following=video.uploader).exists()

        if video.visibility == 'followers_only' and (is_uploader or is_follower):
            return video

        raise PermissionDenied("You do not have permission to view this video.")


from users.models import CustomUser

class LikeToggleAPIView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, *args, **kwargs):
        video = get_object_or_404(Video, pk=self.kwargs.get('pk'))
        Like.objects.get_or_create(user=request.user, video=video) # Create if not exists
        return Response(status=status.HTTP_201_CREATED)

    def delete(self, request, *args, **kwargs):
        video = get_object_or_404(Video, pk=self.kwargs.get('pk'))
        Like.objects.filter(user=request.user, video=video).delete() # Delete if exists
        return Response(status=status.HTTP_204_NO_CONTENT)

class CommentCreateAPIView(generics.CreateAPIView):
    serializer_class = CommentSerializer
    permission_classes = [permissions.IsAuthenticated]

    def create(self, request, *args, **kwargs):
        video_id = self.kwargs.get('pk')
        video = generics.get_object_or_404(Video.objects.all(), pk=video_id)

        if video.requests_feedback and request.user.role != 'trainer':
            return Response(
                {'detail': 'Only trainers can comment on videos requesting feedback.'},
                status=status.HTTP_403_FORBIDDEN
            )

        return super().create(request, *args, **kwargs)

    def perform_create(self, serializer):
        video_id = self.kwargs.get('pk')
        video = generics.get_object_or_404(Video.objects.all(), pk=video_id)
        serializer.save(user=self.request.user, video=video)
class CommentAdoptAPIView(generics.UpdateAPIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, *args, **kwargs):
        comment_id = self.kwargs.get('pk')
        comment = Comment.objects.get(pk=comment_id)

        # Only the video uploader can adopt a comment
        if request.user != comment.video.uploader:
            return Response({'detail': 'You do not have permission to perform this action.'}, status=status.HTTP_403_FORBIDDEN)

        # Only for general users' comments
        if comment.user.role != 'trainer':
            return Response({'detail': 'You can only adopt comments from trainers.'}, status=status.HTTP_400_BAD_REQUEST)

        comment.is_adopted = True
        comment.save()

        trainer_profile = TrainerProfile.objects.get(user=comment.user)
        trainer_profile.adopted_comment_count += 1

        # Update trainer level
        new_level = min(trainer_profile.adopted_comment_count // 100, 5)
        trainer_profile.level = new_level
        
        trainer_profile.save()

        return Response({'detail': 'Comment adopted successfully.'}, status=status.HTTP_200_OK)

from django.contrib.contenttypes.models import ContentType
from reports.models import Report

class ReportVideoAPIView(generics.CreateAPIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, *args, **kwargs):
        video_id = self.kwargs.get('pk')
        video = generics.get_object_or_404(Video.objects.all(), pk=video_id)
        
        reason = request.data.get('reason')
        if not reason:
            return Response({'detail': 'Reason is required.'}, status=status.HTTP_400_BAD_REQUEST)

        # Check if a report already exists to prevent duplicates
        report, created = Report.objects.get_or_create(
            reporter=request.user,
            content_type=ContentType.objects.get_for_model(Video),
            object_id=video.id,
            defaults={'reason': reason}
        )

        if not created:
            return Response({'detail': 'You have already reported this video.'}, status=status.HTTP_400_BAD_REQUEST)

        return Response({'detail': 'Report submitted successfully.'}, status=status.HTTP_201_CREATED)

