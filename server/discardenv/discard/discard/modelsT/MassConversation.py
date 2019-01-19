from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from django.utils.crypto import get_random_string
from .Account import Account
from threading import Timer
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
    creation_date       = models.DateTimeField(auto_now_add=True)


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

    def get_user_conversations(self, account):
        x =  AccountInMassConversation.objects.filter(user_fk = account).all()
        active = [conv.conversation_fk for conv in x if conv.user_removed == False]
        return active

    

    def add_account_to_last_open_conversation(self, account):
        last_open_conversation = None
        try: 
            last_open_conversation = MassConversation.objects.filter(allow_new_users = True).all()[0]
            print(last_open_conversation.room_name)
        except Exception as e:
            print(e)
        finally:
            if last_open_conversation is not None:
                x = AccountInMassConversation(conversation_fk = last_open_conversation, user_fk = account)
                x.save()
            else:
                self.create_new_unique_conversation()
                last_open_conversation = MassConversation.objects.filter(allow_new_users = True).all()[0]
               #TODO: Fetch this conversation from method to avoid recursion
                x = AccountInMassConversation(conversation_fk = last_open_conversation, user_fk = account)
                x.save()
                #last_open_conversation.add_account_to_conversation(account)
            return last_open_conversation

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
        new_conv_name = self.generate_random_name_for_conversation()

        while(len(MassConversation.objects.filter(room_name = new_conv_name).all()) is not 0):
            new_conv_name = self.generate_random_name_for_conversation()

        self.create_new_conversation(new_conv_name)

    def generate_random_name_for_conversation(self):
        new_name = get_random_string(length=32)
        return new_name

    class Meta:
        db_table = '"mass_conversation"'
        

class AccountInMassConversation(models.Model):
    """
    A model which stores information about account in group conversation.


    Attributes
    -------
    conversation_fk : MassConversation
        Reference to conversation of which user is participant

    user_fk : Account
        Reference to user account which is in conversation

    user_removed : boolean
        Checks if user is still in conversation

    """
    conversation_fk   = models.ForeignKey(MassConversation, on_delete=models.CASCADE, primary_key=True)
    user_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
    user_removed      = models.BooleanField(default=False)


    class Meta:
        db_table = '"accounts_in_mass_conversation"'
        unique_together = ('conversation_fk', 'user_fk')
