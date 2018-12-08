from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User

class Conversation(models.Model):
    id_conversation_pk = models.AutoField(primary_key=True)
  
    class Meta:
db_table = '"Conversation"'