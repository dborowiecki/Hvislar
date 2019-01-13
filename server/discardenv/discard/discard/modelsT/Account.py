from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *

class Account(models.Model):
    account_pk       = models.AutoField(primary_key=True)
    username         = models.CharField(unique = True, max_length=40)
    passwd           = models.CharField(max_length=255)
    email            = models.CharField(unique=True, max_length=50)
    dos              = models.DateTimeField(auto_now_add=True)
    last_time_logged = models.DateTimeField(auto_now_add=True)

    def add_friend(self, friend, c):
        success = False
        try:
            contact = Contact(account_fk =  friend, conversation_fk = c)
            contact.save()
            account_contact_list = ContactList(
                account_fk = self,
                contact_fk = contact)
            account_contact_list.save()
            success  = True
        except Exception as e:
            print(e)
        finally:
            return success

    def get_contact(self, account):
        user_contcts = [f.contact_fk for f in ContactList.objects.filter(account_fk=self).all()]
        for contact in user_contcts:
            if account == contact.account_fk:
                return contact

        return None

    '''
    May need try catch and error check
    '''

    def add_description(self, description):
        try:
            user_about = AccountAbout.objects.get(account_about_pk = self.account_pk)
        except AccountAbout.DoesNotExist:
            user_about = AccountAbout(account_about_pk = self)

        user_about.description = description
        user_about.save()
        return True

    def get_description(self):
        try:
            user_about = AccountAbout.objects.get(account_about_pk = self.account_pk)
            return user_about.description
        except AccountAbout.DoesNotExist:
            raise ValueError('No description')
        
        return False

    class Meta:
        db_table = '"account"'