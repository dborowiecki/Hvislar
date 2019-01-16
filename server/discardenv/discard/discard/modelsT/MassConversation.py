from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *

class MassConversation(models.Model):
    """
    Class which is a model for mass conversation

    Attributes
    ------
    room_name: str
        Room name for conversation, can be random string of characters
        Is present in websocket connection URI

    finished: boolean
        True if conversation is finished
    """ 
    conversation_pk     = models.AutoField(primary_key=True)
    room_name           = models.CharField(unique = True, max_length=255)
    allow_new_users     = models.BooleanField(default = True)
    finished            = models.BooleanField(default=False)


    def auth_user(self, account):
        """Method that checks if user is a participant in mass conversation

        Parameters
        ----------
        account: Account
            User which participation is got to be confirmed

        Return
        ------
        boolean
            True if user is participant or False if he isn't
        """

        x = AccountInMassConversation.objects.get(conversation_fk = self, user_fk = account)
        if x is not None:
            return True
        else:
            return False


    def add_account_to_conversation(self, account):
        """
        Method that adds user to mass converstion

        Parameters
        ----------
        account: Account
            Account that is added to converstion

        Return
        ------
        boolean
            If addition was successful True, False in other case
        """
        added = AccountInMassConversation(
            conversation_fk = self,
            user_fk = account)
        added.save()
        check = AccountInMassConversation.objects.get(conversation_fk = self, user_fk = account_about_pk)
        if check is not None:
            return True
        else:
            return False

    def add_account_to_last_open_conversation(self, account):
        try: 
            last_open_conversation = MassConversation.objects.get(allow_new_users = True)
        except MultipleObjectsReturned:
            last_open_conversation = MassConversation.objects.all(allow_new_users = True)[0]
        finally:
            is last_open_conversation is not None:
                last_open_conversation.add_account_to_conversation(account)
            else:
               #TODO: Return exception that will make chat consumer to create new conversation





    def remove_account_from_conversation(self, account):
        """
        Removes user from mass conversation

        Parameters
        ----------
        account: Account
            Account which is going to be removed

        Raises
        ------
        Exception
            When cannot get user as one of converstion participants
        """
        x = AccountInMassConversation.objects.get(conversation_fk = self, user_fk = account)
        if x is None:
            raise Exception('User {} cannot be found in conversation'.format(account.username))
        x.user_removed = True
        x.save()


    def create_new_conversation(self, name):
        """
        Creating and save new converation in database

        Parameters
        ----------
        name: str
            Name of new converstion

        Return
        ------
        boolean
            True if creation was successful
        """
        new_conversation = MassConversation(room_name = name)
        new_conversation.save()
        check = MassConversation.objects.get(room_name = name)
        if check is not None:
            return True
        else:
            return False


    def create_new_unique_conversation(self):
        new_conv_name = generate_random_name_for_conversation()

        while(MassConversation.objects.all(room_name = new_conv_name) is not None):
            new_conv_name = generate_random_name_for_conversation()

        create_new_conversation(new_conv_name)

    def generate_random_name_for_conversation(self):
        new_name = get_random_string(length=32)
        return new_name

    class Meta:
        db_table = '"mass_conversation"'
        
