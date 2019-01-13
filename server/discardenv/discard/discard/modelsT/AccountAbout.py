from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *
class AccountAbout(models.Model):
    account_about_pk       = models.ForeignKey(Account, on_delete=models.CASCADE, primary_key=True)
    description            = models.TextField()

    class Meta:
        db_table = '"account_about"'