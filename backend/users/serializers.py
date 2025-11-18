from django.contrib.auth import get_user_model
from rest_framework import serializers
from .models import UserProfile, TrainerProfile

# Get the CustomUser model
User = get_user_model()

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('username', 'role')

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile
        fields = ('height', 'weight', 'goal')

class TrainerProfileSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    class Meta:
        model = TrainerProfile
        fields = ('user', 'specialty', 'certification', 'adopted_comment_count')

class CustomUserSerializer(serializers.ModelSerializer):
    """
    Serializer for retrieving user data. Dynamically includes the
    correct profile based on the user's role.
    """
    profile = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ('id', 'username', 'email', 'role', 'profile')

    def get_profile(self, obj):
        if obj.role == 'user':
            profile = UserProfile.objects.get(user=obj)
            return UserProfileSerializer(profile).data
        elif obj.role == 'trainer':
            profile = TrainerProfile.objects.get(user=obj)
            return TrainerProfileSerializer(profile).data
        return None

class UserCreateSerializer(serializers.ModelSerializer):
    """
    Serializer for creating new users.
    """
    class Meta:
        model = User
        fields = ('username', 'password', 'email', 'role')
        extra_kwargs = {'password': {'write_only': True}}

    def create(self, validated_data):
        user = User.objects.create_user(
            username=validated_data['username'],
            email=validated_data['email'],
            password=validated_data['password'],
            role=validated_data.get('role', 'user')
        )
        return user