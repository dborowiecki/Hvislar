from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User

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

    
    class Meta:
        db_table = '"account"'