from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from .Contact import Contact
from .Account import Account
class ContactList(models.Model):
	"""
	Account user model which store reference to account and one of its contacts

	Attributes
	----------
	account_fk: Account
		Account which contact is referenced

	contact_fk: Contact
		One of account contacts
	"""
	account_fk = models.ForeignKey(Account, on_delete = models.CASCADE, primary_key=True)
	contact_fk = models.ForeignKey(Contact, related_name='frind', on_delete = models.CASCADE)
	friend_account_fk = models.ForeignKey(Account,  on_delete = models.CASCADE,related_name='%(class)s_requests_created')

	class Meta:
	    db_table        = '"contact_list"'
	    unique_together = ('account_fk', 'contact_fk')