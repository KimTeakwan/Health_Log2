from rest_framework import generics, permissions, status
from django.contrib.auth import get_user_model
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import UserProfileSerializer, TrainerProfileSerializer, PublicProfileSerializer, SimpleUserSerializer
from .models import UserProfile, TrainerProfile, Follow

from rest_framework.generics import get_object_or_404

class FollowToggleAPIView(APIView):
    """
    API view to follow (POST) or unfollow (DELETE) a user.
    """
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, pk, format=None):
        user_to_follow = get_object_or_404(get_user_model(), pk=pk)

        if user_to_follow == request.user:
            return Response({'detail': 'You cannot follow yourself.'}, status=status.HTTP_400_BAD_REQUEST)

        follow, created = Follow.objects.get_or_create(
            follower=request.user,
            following=user_to_follow
        )

        if created:
            return Response({'detail': f'You are now following {user_to_follow.username}.'}, status=status.HTTP_201_CREATED)
        
        return Response({'detail': f'You are already following {user_to_follow.username}.'}, status=status.HTTP_200_OK)

    def delete(self, request, pk, format=None):
        user_to_unfollow = get_object_or_404(get_user_model(), pk=pk)

        follow = Follow.objects.filter(
            follower=request.user,
            following=user_to_unfollow
        )

        if follow.exists():
            follow.delete()
            return Response(status=status.HTTP_204_NO_CONTENT)
        
        return Response({'detail': 'You are not following this user.'}, status=status.HTTP_400_BAD_REQUEST)



class PublicProfileDetailView(generics.RetrieveAPIView):
    """
    API view to retrieve a user's public profile by their ID.
    Includes their profile information and a list of their videos.
    """
    queryset = get_user_model().objects.all()
    serializer_class = PublicProfileSerializer
    permission_classes = [permissions.IsAuthenticated]
    lookup_field = 'pk'


class FollowerListView(generics.ListAPIView):
    serializer_class = SimpleUserSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = get_object_or_404(get_user_model(), pk=self.kwargs['pk'])
        return get_user_model().objects.filter(following_set__following=user)


class FollowingListView(generics.ListAPIView):
    serializer_class = SimpleUserSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        user = get_object_or_404(get_user_model(), pk=self.kwargs['pk'])
        return get_user_model().objects.filter(follower_set__follower=user)


class ProfileView(generics.RetrieveUpdateAPIView):
    permission_classes = [permissions.IsAuthenticated]

    def get_serializer_class(self):
        # This is used for GET requests
        if self.request.user.role == 'trainer':
            return TrainerProfileSerializer
        return UserProfileSerializer

    def get_object(self):
        if self.request.user.role == 'trainer':
            return self.request.user.trainerprofile
        return self.request.user.userprofile

    def update(self, request, *args, **kwargs):
        user = request.user
        profile = self.get_object()

        # Handle nested data for 'first_name' sent from the client
        user_data = request.data.get('user', None)
        if user_data and isinstance(user_data, dict) and 'first_name' in user_data:
            user.first_name = user_data['first_name']
            user.save()

        # Use the appropriate serializer to validate and update the other profile data
        # We pass request.data which might contain other fields for the profile
        serializer = self.get_serializer(profile, data=request.data, partial=True)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        # Re-serialize the instance to ensure the response contains the updated 'first_name'
        response_serializer = self.get_serializer(instance=profile)
        return Response(response_serializer.data, status=status.HTTP_200_OK)

