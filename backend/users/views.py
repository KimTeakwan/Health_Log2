from rest_framework import status, generics, permissions
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializers import UserCreateSerializer, UserProfileSerializer, TrainerProfileSerializer
from .models import UserProfile, TrainerProfile

@api_view(['POST'])
def signup(request):
    if request.method == 'POST':
        serializer = UserCreateSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response({'message': 'User created successfully'}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

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
