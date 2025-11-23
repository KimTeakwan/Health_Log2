from rest_framework import generics, permissions
from .models import Report
from .serializers import ReportSerializer

class ReportCreateAPIView(generics.CreateAPIView):
    """
    API view for creating new reports.
    Users must be authenticated to create a report.
    """
    queryset = Report.objects.all()
    serializer_class = ReportSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_serializer_context(self):
        """
        Pass the request object to the serializer context.
        """
        context = super().get_serializer_context()
        context['request'] = self.request
        return context
