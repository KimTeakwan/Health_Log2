from django.db.models import Q
from rest_framework import generics, permissions, status
from rest_framework.response import Response
from .models import Video, Like, Comment, Tag
from .serializers import VideoSerializer, CommentSerializer
from users.models import TrainerProfile

class VideoListCreateAPIView(generics.ListCreateAPIView):
    serializer_class = VideoSerializer
    permission_classes = [permissions.AllowAny] # Temporarily changed for diagnostics

    def get_queryset(self):
        queryset = Video.objects.all()
        query = self.request.query_params.get('q', None)
        if query:
            queryset = queryset.filter(
                Q(title__icontains=query) |
                Q(description__icontains=query) |
                Q(tags__name__icontains=query)
            ).distinct()
        return queryset

    def perform_create(self, serializer):
        tag_names = serializer.validated_data.pop('tags', [])
        video = serializer.save(uploader=self.request.user)
        
        tags = []
        for name in tag_names:
            tag, _ = Tag.objects.get_or_create(name=name)
            tags.append(tag)
        
        video.tags.set(tags)

class VideoDetailAPIView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Video.objects.all()
    serializer_class = VideoSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly]

class LikeCreateAPIView(generics.CreateAPIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, *args, **kwargs):
        video_id = self.kwargs.get('pk')
        video = Video.objects.get(pk=video_id)
        like, created = Like.objects.get_or_create(user=request.user, video=video)
        if not created:
            like.delete()
            return Response({'detail': 'Like removed.'}, status=status.HTTP_204_NO_CONTENT)
        return Response({'detail': 'Like added.'}, status=status.HTTP_201_CREATED)

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
        # The video object is already fetched in the create method, 
        # but this view doesn't easily share it. Re-fetching is simple enough.
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
        trainer_profile.save()

        return Response({'detail': 'Comment adopted successfully.'}, status=status.HTTP_200_OK)