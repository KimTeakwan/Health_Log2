from django.urls import path
from .views import ReportCreateAPIView

app_name = 'reports'

urlpatterns = [
    path('', ReportCreateAPIView.as_view(), name='report-create'),
]
