from django.conf import settings
from django.contrib.auth.models import AbstractUser
from django.db import models
from django.db.models.signals import post_save
from django.dispatch import receiver


class CustomUser(AbstractUser):
    ROLE_CHOICES = (
        ('user', 'User'),
        ('trainer', 'Trainer'),
    )
    email = models.EmailField(unique=True)
    role = models.CharField(max_length=10, choices=ROLE_CHOICES, default='user')


class UserProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='userprofile')
    profile_image_url = models.URLField(max_length=1024, blank=True)
    public_email = models.EmailField(max_length=255, blank=True)
    instagram_id = models.CharField(max_length=100, blank=True)
    height = models.FloatField(null=True, blank=True)
    weight = models.FloatField(null=True, blank=True)
    goal = models.TextField(max_length=500, blank=True)

    def __str__(self):
        return self.user.username


class TrainerProfile(models.Model):
    user = models.OneToOneField(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name='trainerprofile')
    profile_image_url = models.URLField(max_length=1024, blank=True)
    public_email = models.EmailField(max_length=255, blank=True)
    instagram_id = models.CharField(max_length=100, blank=True)
    specialty = models.CharField(max_length=100, blank=True)
    certification = models.TextField(max_length=500, blank=True)
    adopted_comment_count = models.IntegerField(default=0)
    level = models.IntegerField(default=0)

    def __str__(self):
        return self.user.username


@receiver(post_save, sender=CustomUser)
def create_user_profile(sender, instance, created, **kwargs):
    if created:
        if instance.role == 'user':
            UserProfile.objects.create(user=instance)
        elif instance.role == 'trainer':
            TrainerProfile.objects.create(user=instance)

class Follow(models.Model):
    follower = models.ForeignKey(settings.AUTH_USER_MODEL, related_name='following_set', on_delete=models.CASCADE)
    following = models.ForeignKey(settings.AUTH_USER_MODEL, related_name='follower_set', on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ('follower', 'following')

    def __str__(self):
        return f'{self.follower} follows {self.following}'
