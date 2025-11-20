from rest_framework import authentication
from rest_framework import exceptions
from firebase_admin import auth
from .models import CustomUser

class FirebaseAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.META.get('HTTP_AUTHORIZATION')
        if not auth_header:
            return None

        try:
            id_token = auth_header.split(' ').pop()
            decoded_token = auth.verify_id_token(id_token)
        except Exception as e:
            raise exceptions.AuthenticationFailed('Invalid ID token')

        if not id_token or not decoded_token:
            return None

        try:
            uid = decoded_token.get('uid')
            user, created = CustomUser.objects.get_or_create(
                username=uid,
                defaults={
                    'email': decoded_token.get('email', ''),
                    'role': 'user' # Default role, can be updated later
                }
            )
        except Exception as e:
            raise exceptions.AuthenticationFailed('Failed to create or retrieve user')

        return (user, None)
