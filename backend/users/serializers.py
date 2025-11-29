from django.contrib.auth import get_user_model
from rest_framework import serializers
from .models import UserProfile, TrainerProfile
from videos.models import Video

# Get the CustomUser model
User = get_user_model()

class PublicProfileSerializer(serializers.ModelSerializer):
    """
    Serializer for displaying a user's public profile.
    Includes role-specific profile and a list of uploaded videos.
    """
    profile = serializers.SerializerMethodField()
    videos = serializers.SerializerMethodField()
    follower_count = serializers.SerializerMethodField()
    following_count = serializers.SerializerMethodField()
    is_following = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = (
            'id', 'username', 'role', 'profile', 'videos',
            'follower_count', 'following_count', 'is_following'
        )

    def get_follower_count(self, obj):
        return obj.follower_set.count()

    def get_following_count(self, obj):
        return obj.following_set.count()

    def get_is_following(self, obj):
        request = self.context.get('request', None)
        if not request or not request.user.is_authenticated:
            return False
        return obj.follower_set.filter(follower=request.user).exists()
    
    def get_profile(self, obj):
        if obj.role == 'user':
            try:
                profile = obj.userprofile
                return UserProfileSerializer(profile).data
            except UserProfile.DoesNotExist:
                return None
        elif obj.role == 'trainer':
            try:
                profile = obj.trainerprofile
                return TrainerProfileSerializer(profile).data
            except TrainerProfile.DoesNotExist:
                return None
        return None

    def get_videos(self, obj):
        from videos.serializers import SimpleVideoSerializer
        videos = Video.objects.filter(uploader=obj)
        return SimpleVideoSerializer(videos, many=True).data


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'username', 'first_name', 'role')

class SimpleUserSerializer(serializers.ModelSerializer):
    profile_image_url = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ('id', 'username', 'profile_image_url')

    def get_profile_image_url(self, obj):
        if obj.role == 'user':
            try:
                return obj.userprofile.profile_image_url
            except UserProfile.DoesNotExist:
                return None
        elif obj.role == 'trainer':
            try:
                return obj.trainerprofile.profile_image_url
            except TrainerProfile.DoesNotExist:
                return None
        return None

class UserProfileSerializer(serializers.ModelSerializer):
    user_id = serializers.IntegerField(source='user.id', read_only=True)
    first_name = serializers.CharField(source='user.first_name', read_only=True)
    follower_count = serializers.SerializerMethodField()
    following_count = serializers.SerializerMethodField()

    class Meta:
        model = UserProfile
        fields = ('user_id', 'first_name', 'profile_image_url', 'public_email', 'instagram_id', 'height', 'weight', 'goal', 'follower_count', 'following_count')

    def get_follower_count(self, obj):
        return obj.user.follower_set.count()

    def get_following_count(self, obj):
        return obj.user.following_set.count()

class TrainerProfileSerializer(serializers.ModelSerializer):
    user_id = serializers.IntegerField(source='user.id', read_only=True)
    level_display = serializers.SerializerMethodField()
    follower_count = serializers.SerializerMethodField()
    following_count = serializers.SerializerMethodField()

    class Meta:
        model = TrainerProfile
        fields = ('user_id', 'profile_image_url', 'public_email', 'instagram_id', 'specialty', 'certification', 'adopted_comment_count', 'level', 'level_display', 'follower_count', 'following_count')

    def get_level_display(self, obj):
        return f"LV.{obj.level}"

    def get_follower_count(self, obj):
        return obj.user.follower_set.count()

    def get_following_count(self, obj):
        return obj.user.following_set.count()

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