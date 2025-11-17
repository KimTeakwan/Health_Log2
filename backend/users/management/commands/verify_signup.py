import uuid
from django.core.management.base import BaseCommand
from django.db import transaction
from users.models import CustomUser, UserProfile, TrainerProfile

class Command(BaseCommand):
    help = 'Verifies the user signup process and automatic profile creation.'

    def handle(self, *args, **options):
        # Generate unique usernames and emails to avoid conflicts
        user_username = f'testuser_{uuid.uuid4().hex[:6]}'
        user_email = f'{user_username}@example.com'
        trainer_username = f'testtrainer_{uuid.uuid4().hex[:6]}'
        trainer_email = f'{trainer_username}@example.com'

        try:
            with transaction.atomic():
                self.stdout.write(self.style.SUCCESS("--- Starting Signup Verification ---"))

                # Step 1: Create a regular user
                self.stdout.write(f"Attempting to create user: '{user_username}' with email '{user_email}'...")
                user = CustomUser.objects.create_user(
                    username=user_username,
                    email=user_email,
                    password='password123',
                    role='user'
                )
                self.stdout.write(self.style.SUCCESS(f"Successfully created CustomUser for '{user_username}'."))

                # Step 2: Verify UserProfile creation
                self.stdout.write(f"Verifying UserProfile for '{user_username}'...")
                user_profile_exists = UserProfile.objects.filter(user=user).exists()
                if user_profile_exists:
                    self.stdout.write(self.style.SUCCESS("UserProfile was automatically created."))
                else:
                    self.stdout.write(self.style.ERROR("FAILED: UserProfile was not created."))
                    raise Exception("UserProfile creation failed.")

                self.stdout.write("-" * 20)

                # Step 3: Create a trainer
                self.stdout.write(f"Attempting to create trainer: '{trainer_username}' with email '{trainer_email}'...")
                trainer = CustomUser.objects.create_user(
                    username=trainer_username,
                    email=trainer_email,
                    password='password123',
                    role='trainer'
                )
                self.stdout.write(self.style.SUCCESS(f"Successfully created CustomUser for '{trainer_username}'."))

                # Step 4: Verify TrainerProfile creation
                self.stdout.write(f"Verifying TrainerProfile for '{trainer_username}'...")
                trainer_profile_exists = TrainerProfile.objects.filter(user=trainer).exists()
                if trainer_profile_exists:
                    self.stdout.write(self.style.SUCCESS("TrainerProfile was automatically created."))
                else:
                    self.stdout.write(self.style.ERROR("FAILED: TrainerProfile was not created."))
                    raise Exception("TrainerProfile creation failed.")

                self.stdout.write(self.style.SUCCESS("\n--- Verification Successful! ---"))
                self.stdout.write("Signup process and automatic profile creation are working correctly.")

                # The transaction.atomic() block will be rolled back, deleting the test users.
                self.stdout.write("\nRolling back transaction to clean up test data...")
                raise Exception("ROLLBACK") # Intentionally raise exception to rollback transaction

        except Exception as e:
            if str(e) == "ROLLBACK":
                self.stdout.write(self.style.SUCCESS("Test data has been cleaned up successfully."))
            else:
                self.stdout.write(self.style.ERROR(f"\n--- Verification Failed: {e} ---"))
