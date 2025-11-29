from django.db import models
from django.contrib.contenttypes.fields import GenericForeignKey
from django.contrib.contenttypes.models import ContentType
from django.conf import settings

class Report(models.Model):
    class ReportReason(models.TextChoices):
        SPAM = 'SPAM', '스팸'
        ABUSE = 'ABUSE', '욕설/비방'
        INAPPROPRIATE = 'INAPPROPRIATE', '부적절한 콘텐츠'
        OTHER = 'OTHER', '기타'

    class ReportStatus(models.TextChoices):
        PENDING = 'PENDING', '대기중'
        REVIEWED = 'REVIEWED', '처리 완료'
        DISMISSED = 'DISMISSED', '반려'

    reporter = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='reports')

    # Generic Foreign Key to the reported object (User, Video, Comment, etc.)
    content_type = models.ForeignKey(ContentType, on_delete=models.CASCADE)
    object_id = models.PositiveIntegerField()
    content_object = GenericForeignKey('content_type', 'object_id')

    reason = models.CharField(max_length=20, choices=ReportReason.choices, default=ReportReason.OTHER)
    description = models.TextField(blank=True)
    status = models.CharField(max_length=20, choices=ReportStatus.choices, default=ReportStatus.PENDING)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Report by {self.reporter} on {self.content_object} for {self.get_reason_display()}"

    class Meta:
        ordering = ['-created_at']
        indexes = [
            models.Index(fields=["content_type", "object_id"]),
        ]
