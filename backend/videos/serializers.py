from rest_framework import serializers
from .models import Video, Like, Comment, Tag
from users.serializers import UserSerializer

class TagSerializer(serializers.ModelSerializer):
    class Meta:
        model = Tag
        fields = ['name']

class CommentSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)

    class Meta:
        model = Comment
        fields = ['id', 'user', 'video', 'text', 'is_adopted', 'created_at']
        read_only_fields = ['is_adopted']

class LikeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Like
        fields = ['id', 'user', 'video', 'created_at']

class VideoSerializer(serializers.ModelSerializer):
    comments = CommentSerializer(many=True, read_only=True)
    likes_count = serializers.SerializerMethodField()
    tags = serializers.SlugRelatedField(
        many=True,
        queryset=Tag.objects.all(),
        slug_field='name'
     )

    class Meta:
        model = Video
        fields = ['id', 'title', 'description', 'video_file', 'uploader', 'created_at', 'comments', 'likes_count', 'tags']

    def get_likes_count(self, obj):
        return obj.likes.count()
