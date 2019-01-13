from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class Message(models.Model):
    message_pk          = models.AutoField(primary_key=True)
    conversation_fk     = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    sender_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
    content_of_msg      = models.CharField(max_length=250)
    send_time           = models.DateTimeField(auto_now_add=True)
   
    class Meta:
        db_table = '"message"'
