"""
Module with database models
"""
from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from .Account import Account
from .AccountManager import AccountManager
#from .AccountInMassConversation import AccountInMassConversation
from .AccountAbout import AccountAbout
from .Contact import Contact
from .ContactRequest import ContactRequest
from .Conversation import Conversation
from .MassConversation import MassConversation