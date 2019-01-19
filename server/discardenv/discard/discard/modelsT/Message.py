from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class Message(models.Model):
	"""
	Message model

	Attributes
	----------
	conversation_fk: Conversation
		Conversation in which message is present
	sender_fk: Account
		User that send the message
	content_of_msg: str
		Text that was sended
	send_time: Date
		Date of message sending
	"""
	message_pk          = models.AutoField(primary_key=True)
	conversation_fk     = models.ForeignKey(Conversation, on_delete=models.CASCADE)
	sender_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
	content_of_msg      = models.CharField(max_length=250)
	send_time           = models.DateTimeField(auto_now_add=True)

	class Meta:
	    db_table = '"message"'
