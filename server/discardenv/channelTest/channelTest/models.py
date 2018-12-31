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



class MassConversation(models.Model):
    conversation_pk     = models.AutoField(primary_key=True)
    room_name           = models.CharField(unique = True, max_length=255)
    finished            = models.BooleanField(default=False)

    def auth_user(self, account):
        x = AccountInMassConversation.objects.using('psql_db').get(conversation_fk = self, user_fk = account)
        if x is not None:
            return True
        else:
            return False

    def add_account_to_conversation(self, account):
        added = AccountInMassConversation(
            conversation_fk = self,
            user_fk = account)
        added.save()
        check = AccountInMassConversation.objects.using('psql_db').get(conversation_fk = self, user_fk = account_about_pk)
        if check is not None:
            return True
        else:
            return False

    def create_new_conversation(self, name):
        new_conversation = MassConversation(room_name = name)
        new_conversation.save()
        check = MassConversation.objects.using('psql_db').get(room_name = name)
        if check is not None:
            return True
        else:
            return False
            

    class Meta:
        db_table = '"mass_conversation"'
        

class AccountInMassConversation(models.Model):
    conversation_fk   = models.ForeignKey(MassConversation, on_delete=models.CASCADE, primary_key=True)
    user_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)

    class Meta:
        db_table = '"accounts_in_mass_conversation"'
        unique_together = ('conversation_fk', 'user_fk')