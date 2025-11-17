from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from .models import CustomUser, UserProfile, TrainerProfile

class UserProfileInline(admin.StackedInline):
    model = UserProfile
    can_delete = False
    verbose_name_plural = 'User Profile'
    fk_name = 'user'

class TrainerProfileInline(admin.StackedInline):
    model = TrainerProfile
    can_delete = False
    verbose_name_plural = 'Trainer Profile'
    fk_name = 'user'

class CustomUserAdmin(UserAdmin):
    inlines = []
    list_display = ('username', 'email', 'first_name', 'last_name', 'is_staff', 'role')
    search_fields = ('username', 'email', 'first_name', 'last_name')
    list_filter = ('role', 'is_staff', 'is_superuser', 'is_active', 'groups')

    # Dynamically add inlines based on user role
    def get_inlines(self, request, obj=None):
        if obj:
            if obj.role == 'user':
                return (UserProfileInline,)
            elif obj.role == 'trainer':
                return (TrainerProfileInline,)
        return super().get_inlines(request, obj)

    # Add role to the fieldsets
    fieldsets = UserAdmin.fieldsets + (
        ('Role', {'fields': ('role',)}),
    )
    add_fieldsets = UserAdmin.add_fieldsets + (
        ('Role', {'fields': ('role',)}),
    )

admin.site.register(CustomUser, CustomUserAdmin)
admin.site.register(UserProfile)
admin.site.register(TrainerProfile)
