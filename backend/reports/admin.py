from django.contrib import admin
from .models import Report

@admin.register(Report)
class ReportAdmin(admin.ModelAdmin):
    list_display = ('reporter', 'content_object', 'reason', 'status', 'created_at')
    list_filter = ('status', 'reason', 'created_at')
    search_fields = ('reporter__username', 'reason')
    readonly_fields = ('reporter', 'content_type', 'object_id', 'content_object', 'created_at')

    def get_queryset(self, request):
        return super().get_queryset(request).prefetch_related('reporter', 'content_object')
