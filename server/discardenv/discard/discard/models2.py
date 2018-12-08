from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User

class Contact(models.Model):
    id_contact_pk = models.AutoField(primary_key=True)
	id_user_fk = models.ForeignKey(Account, on_delete=models.CASCADE)
	id_account_fk = models.ForeignKey(Account, on_delete=models.CASCADE)
    contact_name = models.CharField(max_length=20)
    status = models.BooleanField(default=False)
   
    class Meta:
		db_table = '"contact"'