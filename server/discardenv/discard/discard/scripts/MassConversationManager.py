import traceback

from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt

from ..scripts.LoginRegister import hash_password
from ..modelsT.Account import Account
from ..modelsT.MassConversation import MassConversation
import json
import threading
from ..test import conversation_closer

@csrf_exempt
def add_user_to_mass_conversation(request):

    response = {
        'success': False
    }
    try:
        passwd               = hash_password(request.POST.get("password"))
        email                = request.POST.get("email")
        user                 = Account.objects.get(passwd = passwd, email = email)

        if user is not None:
            m = MassConversation()
            room = m.add_account_to_last_open_conversation(user)
            k = conversation_closer(room)
            k.change()
            response['success'] = True
            response['room_name'] = room.room_name

    except Exception as e:
        print(e)
        print(traceback.format_exc())
        response['error'] = str(e)
    finally:
        return JsonResponse(response) 



def close_conversation(conversation):
        print('1')
        t = threading.Timer(10.0, close_adding(conversation))
        t.start()
        print('2')

def close_adding(conversation):
    print('D')
    conversation.allow_new_users = False
    conversation.save()

def change_user_state(conversation_name, user):
    try:
        conversation = MassConversation.objects.get(room_name = conversation_name)
        account = Account.objects.get(username=user)
        print("PLACE FOR REMOVE NOIS")
        conversation.remove_account_from_conversation(account)
    except Exception as e:
        print("Error while removing user from conversation")