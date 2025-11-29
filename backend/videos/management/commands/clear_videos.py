from django.core.management.base import BaseCommand
from videos.models import Video

class Command(BaseCommand):
    help = 'Deates all videos from the database'

    def handle(self, *args, **options):
        self.stdout.write(self.style.WARNING('This will delete all videos from the database.'))
        count = Video.objects.count()
        if count == 0:
            self.stdout.write(self.style.SUCCESS('No videos to delete.'))
            return

        confirm = input(f'Are you sure you want to delete {count} video(s)? (y/N) ')
        if confirm.lower() == 'y':
            Video.objects.all().delete()
            self.stdout.write(self.style.SUCCESS(f'Successfully deleted {count} video(s).'))
        else:
            self.stdout.write(self.style.ERROR('Operation cancelled.'))
