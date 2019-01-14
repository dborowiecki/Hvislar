from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class Contact(models.Model):
	"""
	Contact model, stores information about account contact with
	another user

	Attributes
	----------
	account_fk : Account
		Account to which contact is assigned

	conversation_fk: Conversation
		Reference to conversation between account and contact

	contact_name: str
		Optional other name for contact username

	status: boolean
		Shows if contct is still active, if True account user might block
		contact with another user

	"""
    contact_pk      = models.AutoField(primary_key=True)
    account_fk      = models.ForeignKey(Account, on_delete=models.CASCADE)
    conversation_fk = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    contact_name    = models.CharField(max_length=20)
    status          = models.BooleanField(default=False)
   
    class Meta:
        db_table = '"contact"'