from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User

class Account(models.Model):
    id_account_pk = models.AutoField(primary_key=True)
    username = models.CharField(unique = True, max_length=40)
    passwd = models.CharField(max_length=255)
    email = models.CharField(unique=True, max_length=50)
    dos = models.DateTimeField(auto_now_add=True)
    last_time_logged = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = '"account"'
        
# @models.permalink
#  def get_absolute_url(self):
#      return ('discard_account_detail', (),
#           {
#             'email': self.email
#           })
#  def save(self, *args, **kwargs):
#      if not self.email:
#          self.email = slugify(self.title)
#     super(Account, self).save(*args, **kwargs)
