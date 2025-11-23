from rest_framework import generics, permissions, status
from django.contrib.auth import get_user_model
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import UserProfileSerializer, TrainerProfileSerializer, PublicProfileSerializer
from .models import UserProfile, TrainerProfile, Follow

class FollowToggleAPIView(APIView):
    """
    API view to follow or unfollow a user.
    """
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, pk, format=None):
        user_to_follow = get_user_model().objects.filter(pk=pk).first()
        if not user_to_follow:
            return Response({'detail': 'User not found.'}, status=status.HTTP_404_NOT_FOUND)

        if user_to_follow == request.user:
            return Response({'detail': 'You cannot follow yourself.'}, status=status.HTTP_400_BAD_REQUEST)

        follow, created = Follow.objects.get_or_create(
            follower=request.user,
            following=user_to_follow
        )

        if created:
            # New follow
            return Response({'detail': f'You are now following {user_to_follow.username}.'}, status=status.HTTP_201_CREATED)
        else:
            # Unfollow
            follow.delete()
            return Response({'detail': f'You have unfollowed {user_to_follow.username}.'}, status=status.HTTP_204_NO_CONTENT)


class PublicProfileDetailView(generics.RetrieveAPIView):
    """
    API view to retrieve a user's public profile by their ID.
    Includes their profile information and a list of their videos.
    """
    queryset = get_user_model().objects.all()
    serializer_class = PublicProfileSerializer
    permission_classes = [permissions.IsAuthenticated]
    lookup_field = 'pk'


class ProfileView(generics.RetrieveUpdateAPIView):
    permission_classes = [permissions.IsAuthenticated]

    def get_serializer_class(self):
        if self.request.user.role == 'trainer':
            return TrainerProfileSerializer
        return UserProfileSerializer

    def get_object(self):
        if self.request.user.role == 'trainer':
            try:
                return TrainerProfile.objects.get(user=self.request.user)
            except TrainerProfile.DoesNotExist:
                return TrainerProfile.objects.create(user=self.request.user)
        try:
            return UserProfile.objects.get(user=self.request.user)
        except UserProfile.DoesNotExist:
            return UserProfile.objects.create(user=self.request.user)

