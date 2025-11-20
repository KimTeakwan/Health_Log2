from rest_framework import generics, permissions
from .serializers import UserProfileSerializer, TrainerProfileSerializer
from .models import UserProfile, TrainerProfile

class ProfileView(generics.RetrieveAPIView):
    permission_classes = [permissions.IsAuthenticated]

    def get_serializer_class(self):
        if self.request.user.role == 'trainer':
            return TrainerProfileSerializer
        return UserProfileSerializer

    def get_object(self):
        if self.request.user.role == 'trainer':
            return TrainerProfile.objects.get(user=self.request.user)
        return UserProfile.objects.get(user=self.request.user)
