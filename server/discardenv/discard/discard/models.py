from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User


class Account(models.Model):
    account_pk    = models.AutoField(primary_key=True)
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

    def confirm_contact(self, account):
        user_contcts = [f.contact_fk for f in ContactList.objects.filter(account_fk=self).all()]
        for contact in user_contcts:
            if account == contact.account_fk:
                return contact.conversation_fk

        return None



    class Meta:
        db_table = '"account"'
        

class Conversation(models.Model):
    conversation_pk = models.AutoField(primary_key=True)

    def get_messages_from_conversation(self, **kwargs):
        time_stamp_from    = kwargs['time_from']
        time_stamp_to      = kwargs['time_to']
        number_of_messages = kwargs['number_of_messages']

        if time_stamp_from is None:
            messages = Message.objects.filter(conversation_fk = self)[:number_of_messages]
            return messages

        if time_stamp_to is not None:
            messages = Message.objects.filter(
                conversation_fk=self).filter(
                send_time__lt = time_stamp_to)[:number_of_messages]
            return messages

        

        #TODO extend request for time period or sth
        return False


    class Meta:
        db_table = '"conversation"'


class Contact(models.Model):
    contact_pk      = models.AutoField(primary_key=True)
    account_fk      = models.ForeignKey(Account, on_delete=models.CASCADE)
    conversation_fk = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    contact_name    = models.CharField(max_length=20)
    status          = models.BooleanField(default=False)
   
    class Meta:
        db_table = '"contact"'


class ContactRequest(models.Model):
    account_fk               = models.ForeignKey(Account, on_delete=models.CASCADE, primary_key=True)
    requesting_account_fk    = models.ForeignKey(Account, related_name='%(class)s_initiate',on_delete=models.CASCADE)
    request_message             = models.CharField(max_length=250)

    class Meta:
        db_table = '"contact_requests"'
        unique_together = ('account_fk', 'requesting_account_fk')


class Message(models.Model):
    message_pk          = models.AutoField(primary_key=True)
    conversation_fk     = models.ForeignKey(Conversation, on_delete=models.CASCADE)
    sender_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
    content_of_msg      = models.CharField(max_length=250)
    send_time           = models.DateTimeField(auto_now_add=True)
   
    class Meta:
        db_table = '"message"'

class ContactList(models.Model):
    account_fk = models.ForeignKey(Account, on_delete = models.CASCADE, primary_key=True)
    contact_fk = models.ForeignKey(Contact, related_name='frind', on_delete = models.CASCADE)

    class Meta:
        db_table        = '"contact_list"'
        unique_together = ('account_fk', 'contact_fk')
