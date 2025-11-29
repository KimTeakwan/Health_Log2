from rest_framework import authentication
from rest_framework import exceptions
from firebase_admin import auth
from .models import CustomUser

class FirebaseAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        print("--- [FirebaseAuthentication] Starting authentication check ---")

        # Print all headers to see what the client is sending
        print("Request Headers:", request.META)

        auth_header = request.META.get('HTTP_AUTHORIZATION')
        if not auth_header:
            print("[FirebaseAuthentication] No 'HTTP_AUTHORIZATION' header found. Skipping.")
            return None

        print(f"[FirebaseAuthentication] Found 'HTTP_AUTHORIZATION' header: {auth_header}")

        try:
            id_token = auth_header.split(' ').pop()
            print("[FirebaseAuthentication] Extracted token.")
            decoded_token = auth.verify_id_token(id_token)
            print("[FirebaseAuthentication] Firebase token verified successfully.")
        except Exception as e:
            print(f"[FirebaseAuthentication] ERROR: Token verification failed. Reason: {e}")
            raise exceptions.AuthenticationFailed('Invalid ID token')

        if not id_token or not decoded_token:
            print("[FirebaseAuthentication] Token or decoded_token is empty after verification.")
            return None

        try:
            uid = decoded_token.get('uid')
            print(f"[FirebaseAuthentication] Token UID: {uid}")
            
            user, created = CustomUser.objects.get_or_create(
                username=uid,
                defaults={
                    'email': decoded_token.get('email', ''),
                    'role': 'user' # Default role, can be updated later
                }
            )
            if created:
                print(f"[FirebaseAuthentication] New user created: {user.username}")
            else:
                print(f"[FirebaseAuthentication] Existing user retrieved: {user.username}")
        except Exception as e:
            print(f"[FirebaseAuthentication] ERROR: Failed to create or retrieve user. Reason: {e}")
            raise exceptions.AuthenticationFailed('Failed to create or retrieve user')
        
        print(f"[FirebaseAuthentication] Authentication successful. User: {user.username}")
        return (user, None)
