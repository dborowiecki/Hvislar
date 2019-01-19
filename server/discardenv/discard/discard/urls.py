"""discard URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from .scripts.LoginRegister import *
from .scripts.ContactManager import *
from .scripts.MessageManager import *
from .scripts.MassConversationManager import *

urlpatterns = [
    path('admin/', admin.site.urls),
    path('register/', register),
    path('login/', logon),
    path('getFriendList/', get_user_contacts),
    path('getPotentialFriends/', get_potential_friends),
    path('requestContact/', request_contact),
    path('answerContactRequest/', request_contact_response),
    path('sendMessage/', send_message),
    path('getMessages/', get_messages_from_conversation),
    path('addDescription/', add_user_description),
    path('getDescription/', get_user_description),
    path('joinNewMassConversation/', add_user_to_mass_conversation),
]
