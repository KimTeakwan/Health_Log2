from django.contrib.contenttypes.models import ContentType
from rest_framework import serializers
from .models import Report
from users.models import CustomUser
from videos.models import Video, Comment

class ReportSerializer(serializers.ModelSerializer):
    # Make reporter readonly, it will be set from the request user
    reporter = serializers.PrimaryKeyRelatedField(read_only=True)
    
    # These fields are used to identify the object being reported
    content_type = serializers.CharField(write_only=True)
    object_id = serializers.IntegerField(write_only=True)

    class Meta:
        model = Report
        fields = [
            'id', 
            'reporter', 
            'content_type', 
            'object_id', 
            'reason', 
            'status', 
            'created_at'
        ]
        read_only_fields = ['status', 'created_at']

    def validate(self, data):
        """
        Validate the content_type and object_id to ensure they point to a valid object.
        """
        try:
            # Map user-friendly content type names to Django's ContentType model
            content_type_str = data.get('content_type').lower()
            if content_type_str not in ['user', 'video', 'comment']:
                raise serializers.ValidationError("Invalid content type. Must be one of 'user', 'video', or 'comment'.")
            
            model_map = {'user': CustomUser, 'video': Video, 'comment': Comment}
            model = model_map[content_type_str]
            self.context['content_type_obj'] = ContentType.objects.get_for_model(model)

        except (KeyError, ContentType.DoesNotExist):
            raise serializers.ValidationError("Invalid content type specified.")

        object_id = data.get('object_id')
        if not self.context['content_type_obj'].model_class().objects.filter(pk=object_id).exists():
            raise serializers.ValidationError(f"No object found with id {object_id} for the specified content type.")
        
        # Prevent users from reporting themselves
        if self.context['content_type_obj'].model_class() == CustomUser and object_id == self.context['request'].user.id:
            raise serializers.ValidationError("You cannot report yourself.")

        return data
    
    def create(self, validated_data):
        """
        Create a new Report instance.
        """
        reporter = self.context['request'].user
        content_type = self.context['content_type_obj']
        object_id = validated_data['object_id']
        reason = validated_data['reason']

        # Check for existing pending report to prevent duplicates
        existing_report = Report.objects.filter(
            reporter=reporter, 
            content_type=content_type, 
            object_id=object_id,
            status=Report.ReportStatus.PENDING
        ).first()

        if existing_report:
            raise serializers.ValidationError("You have already submitted a report for this content.")

        report = Report.objects.create(
            reporter=reporter,
            content_type=content_type,
            object_id=object_id,
            reason=reason
        )
        return report

