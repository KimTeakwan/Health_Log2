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
        read_only_fields = ['is_adopted', 'video']
        extra_kwargs = {'text': {'required': False}}

class LikeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Like
        fields = ['id', 'user', 'video', 'created_at']

class VideoSerializer(serializers.ModelSerializer):
    comments = CommentSerializer(many=True, read_only=True)
    likes_count = serializers.SerializerMethodField()
    is_liked = serializers.SerializerMethodField()
    uploader = UserSerializer(read_only=True)
    tags = serializers.SlugRelatedField(
        many=True,
        read_only=True,
        slug_field='name'
     )
    tag_names = serializers.ListField(
        child=serializers.CharField(max_length=100),
        write_only=True,
        required=False
    )

    class Meta:
        model = Video
        fields = ['id', 'title', 'description', 'video_file', 'thumbnail', 'uploader', 'requests_feedback', 'visibility', 'view_count', 'created_at', 'comments', 'likes_count', 'is_liked', 'tags', 'tag_names']

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_is_liked(self, obj):
        request = self.context.get('request', None)
        if request and request.user.is_authenticated:
            return Like.objects.filter(video=obj, user=request.user).exists()
        return False

    def create(self, validated_data):
        tag_names = validated_data.pop('tag_names', [])
        video = super().create(validated_data)
        for name in tag_names:
            if name.strip():
                tag, _ = Tag.objects.get_or_create(name=name.strip())
                video.tags.add(tag)
        return video

    def update(self, instance, validated_data):
        tag_names = validated_data.pop('tag_names', None)
        video = super().update(instance, validated_data)

        if tag_names is not None:
            video.tags.clear()
            for name in tag_names:
                if name.strip():
                    tag, _ = Tag.objects.get_or_create(name=name.strip())
                    video.tags.add(tag)
        return video


class SimpleVideoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Video
        fields = ['id', 'title', 'video_file']
