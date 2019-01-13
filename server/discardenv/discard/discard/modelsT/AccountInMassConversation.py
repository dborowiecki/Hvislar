from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class AccountInMassConversation(models.Model):
    conversation_fk   = models.ForeignKey(MassConversation, on_delete=models.CASCADE, primary_key=True)
    user_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
    user_removed      = models.BooleanField(default=False)

    class Meta:
        db_table = '"accounts_in_mass_conversation"'
        unique_together = ('conversation_fk', 'user_fk')
