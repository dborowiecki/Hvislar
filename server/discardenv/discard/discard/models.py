from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User

class Account(models.Model):
    id_account_pk    = models.AutoField(primary_key=True)
    username         = models.CharField(unique = True, max_length=40)
    passwd           = models.CharField(max_length=255)
    email            = models.CharField(unique=True, max_length=50)
    dos              = models.DateTimeField(auto_now_add=True)
    last_time_logged = models.DateTimeField(auto_now_add=True)

    def add_friend(friend):
        success = False
        try:
            contact1 = Contact(
                id_user_fk      =  id_account_pk,
                id_account_fk   =  friend.id_account_pk)
            contact2 = Contact(
                id_user_fk      =  friend.id_account_pk,
                id_account_fk   =  id_account_pk)

            contact1.save()
            contact2.save()
            success  = True
        except Exception as e:
            print(e)
        finally:
            return succes

    class Meta:
        db_table = '"account"'
        

class Contact(models.Model):
    id_contact_pk   = models.AutoField(primary_key=True)
    id_user_fk      = models.ForeignKey(Account, on_delete=models.CASCADE)
    id_account_fk   = models.ForeignKey(Account,related_name='%(class)s_friend', on_delete=models.CASCADE)
    contact_name    = models.CharField(max_length=20)
    status          = models.BooleanField(default=False)
   
    class Meta:
        db_table = '"contact"'


class ContactRequest(models.Model):
    id_account_fk               = models.ForeignKey(Account, on_delete=models.CASCADE)
    id_requesting_account_fk    = models.ForeignKey(Account, related_name='%(class)s_initiate',on_delete=models.CASCADE)
    request_message             = models.CharField(max_length=250)

    class Meta:
        db_table = '"contact_requests"'
   

class Conversation(models.Model):
    id_conversation_pk = models.AutoField(primary_key=True)
  
    class Meta:
        db_table = '"Conversation"'


class Message(models.Model):
    id_message_pk       = models.AutoField(primary_key=True)
    id_conversation_fk  = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    content_of_msg      = models.CharField(max_length=250)
    send_time           = models.DateTimeField(auto_now_add=True)
   
    class Meta:
        db_table = '"message"'

