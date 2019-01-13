from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class ContactRequest(models.Model):
    account_fk               = models.ForeignKey(Account, on_delete=models.CASCADE, primary_key=True)
    requesting_account_fk    = models.ForeignKey(Account, related_name='%(class)s_initiate',on_delete=models.CASCADE)
    request_message             = models.CharField(max_length=250)

    class Meta:
        db_table = '"contact_requests"'
        unique_together = ('account_fk', 'requesting_account_fk')