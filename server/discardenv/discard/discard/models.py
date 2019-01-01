from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User


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

    class Meta:
        db_table = '"account"'
        

class AccountAbout(models.Model):
    account_about_pk       = models.ForeignKey(Account, on_delete=models.CASCADE, primary_key=True)
    description            = models.TextField()

    class Meta:
        db_table = '"account_about"'

class Conversation(models.Model):
    conversation_pk = models.AutoField(primary_key=True)

    '''
    timestamp_from is later date than timestamp_to
    '''
    def get_messages_from_conversation(self, **kwargs):
        time_stamp_from    = kwargs['time_from']
        time_stamp_to      = kwargs['time_to']
        number_of_messages = kwargs['number_of_messages']

        messages = Message.objects.filter(conversation_fk=self)

        if messages is None:
            return None
        '''TODO: timestamp method requires tests'''
        if number_of_messages is None:
            if time_stamp_from or time_stamp_to is None:
                raise ValueError('You should define number of messages or both timestamps')
            else:
                messages = messages.filter(
                    send_time__gt = time_stamp_to).filter(
                    send_time__lt = time_stamp_from)
                return messages

        elif time_stamp_from is not None:
            messages = messages.filter(send_time__lt = time_stamp_from)

            if time_stamp_to is not None:
                if time_stamp_from < time_stamp_to:
                    raise ValueError('time_from should be later than time_to')
                messages =messages.filter(send_time__gt = time_stamp_to)


        messages = messages[:number_of_messages]
        return messages
        #TODO extend request for time period or sth

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
        

class AccountInMassConversation(models.Model):
    conversation_fk   = models.ForeignKey(MassConversation, on_delete=models.CASCADE, primary_key=True)
    user_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
    user_removed      = models.BooleanField(default=False)

    class Meta:
        db_table = '"accounts_in_mass_conversation"'
        unique_together = ('conversation_fk', 'user_fk')
