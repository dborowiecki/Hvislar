from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User

class Messase(models.Model):
    id_message_pk = models.AutoField(primary_key=True)
	id_conversation_fk = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    content_of_msg = models.CharField(max_length=250)
    send_time = models.DateTimeField(auto_now_add=True)
   
    class Meta:
		db_table = '"message"'