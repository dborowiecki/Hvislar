from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class Contact(models.Model):
    contact_pk      = models.AutoField(primary_key=True)
    account_fk      = models.ForeignKey(Account, on_delete=models.CASCADE)
    conversation_fk = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    contact_name    = models.CharField(max_length=20)
    status          = models.BooleanField(default=False)
   
    class Meta:
        db_table = '"contact"'