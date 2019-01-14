from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *

class Account(models.Model):
    """
    A class used as user account model

    Attributes
    ----------
    username: str 
        Username
    passwd: str
        User password
    email: str
        User email
    dos: Date
        Date of account creation
    last_time_logged: Date
        When user was last time loged in
        
    Methods
    -------
    add_friend : Account, Conversation
        Add account to contact list

    get_contact : Account
        Returns contact with another account

    add_description : str
        Adding description for user account

    get_description : 
        Returns account description

    """
    account_pk       = models.AutoField(primary_key=True)
    username         = models.CharField(unique = True, max_length=40)
    passwd           = models.CharField(max_length=255)
    email            = models.CharField(unique=True, max_length=50)
    dos              = models.DateTimeField(auto_now_add=True)
    last_time_logged = models.DateTimeField(auto_now_add=True)

    def add_friend(self, friend, c):
        """
        Adding another account to account friend list.

        Parameters
        --------
        friend : Account
            Account of friend which is going to be added as friend

        c : Conversation
            Reference to conversation between accounts

        Returns
        --------
        boolean
            True if adding friend was sucessfull 
            False if failed


        """
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
        """
        Method contact with another user

        Parameters
        ----------
        account: Account
            Another account whose contact we want to recive

        Returns
        -------
        Contact
            contact with another user if found
            None if contact with another account was not found
        """
        user_contcts = [f.contact_fk for f in ContactList.objects.filter(account_fk=self).all()]
        for contact in user_contcts:
            if account == contact.account_fk:
                return contact

        return None

    '''
    May need try catch and error check
    '''

    def add_description(self, description):
        """
        Method that adds description for account

        Parameters
        ----------
        description: str
            Description that we want add to account

        Returns
        -------
        boolean
            True if addition was succesfull
            False if addition failed
        """
        try:
            user_about = AccountAbout.objects.get(account_about_pk = self.account_pk)
        except AccountAbout.DoesNotExist:
            user_about = AccountAbout(account_about_pk = self)

        user_about.description = description
        user_about.save()
        return True

    def get_description(self):
        """
        Getting account description

        Returns
        -------
        str
            account description 
        """
        try:
            user_about = AccountAbout.objects.get(account_about_pk = self.account_pk)
            return user_about.description
        except AccountAbout.DoesNotExist:
            raise ValueError('No description')
        
        return None

    class Meta:
        db_table = '"account"'