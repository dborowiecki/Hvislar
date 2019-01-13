from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *

class MassConversation(models.Model):
    conversation_pk     = models.AutoField(primary_key=True)
    room_name           = models.CharField(unique = True, max_length=255)
    finished            = models.BooleanField(default=False)


    def auth_user(self, account):
        x = AccountInMassConversation.objects.get(conversation_fk = self, user_fk = account)
        if x is not None:
            return True
        else:
            return False


    def add_account_to_conversation(self, account):
        added = AccountInMassConversation(
            conversation_fk = self,
            user_fk = account)
        added.save()
        check = AccountInMassConversation.objects.get(conversation_fk = self, user_fk = account_about_pk)
        if check is not None:
            return True
        else:
            return False


    def remove_account_from_conversation(self, account):
        x = AccountInMassConversation.objects.get(conversation_fk = self, user_fk = account)
        if x is None:
            raise Exception('User {} cannot be found in conversation'.format(account.username))
        x.user_removed = True
        x.save()


    def create_new_conversation(self, name):
        new_conversation = MassConversation(room_name = name)
        new_conversation.save()
        check = MassConversation.objects.get(room_name = name)
        if check is not None:
            return True
        else:
            return False


    class Meta:
        db_table = '"mass_conversation"'
        
