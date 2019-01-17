from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from .Account import Account

class AccountAbout(models.Model):
	"""
	A class used as AccoutAboutModel where user description is stored 

	Attributes
	----------
	account_about_pk: Account
		Reference to account whose description is stored

	description: str
		Description content
	"""

	account_about_pk       = models.ForeignKey(Account, on_delete=models.CASCADE, primary_key=True)
	description            = models.TextField()

	class Meta:
	    db_table = '"account_about"'