from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt

from ..scripts.LoginRegister import hash_password
from ..modelsT.Account import Account
from ..modelsT.AccountManager import AccountManager
from ..modelsT.Message import Message
from ..modelsT.ConversationManager import ConversationManager
import json


@csrf_exempt
def send_message(request):
    response = {
            'success': False
        }

    try:
        user          = request.POST.get("email")
        password      = hash_password(request.POST.get("password"))
        reciver_name  = request.POST.get("reciver_name")
        message       = request.POST.get("message")
        account       = Account.objects.get(passwd = password, email = user)
        reciver       = Account.objects.get(username = reciver_name)

        contact = AccountManager(reciver).get_contact(account)
        conversation = contact.conversation_fk

        if conversation is not None:
            response['found'] = True
            m = Message(conversation_fk = conversation, content_of_msg = message, sender_fk = account)
            m.save()
            contact.status = True
            contact.save()
            response['success'] = True
        else:
            response['found'] = False



        return JsonResponse(response)

    except Exception as e:
        print(e)
        response['error'] = str(e)
    
    return JsonResponse(response)

@csrf_exempt
def get_messages_from_conversation(request):
    """See: modelsT/ConversationManager.py"""
    response = {
        'success': False
    }
    try:
        user          = request.POST.get("email")
        password      = hash_password(request.POST.get("password"))
        user2         = request.POST.get("interlocutor")
        msg_number    = request.POST.get("number_of_messages")
        msg_from      = None
        msg_from      = request.POST.get("messages_from_time")
        msg_to        = None
        msg_to        = request.POST.get("messages_to_time")
        account       = Account.objects.get(passwd = password, email = user)
        interlocutor  = Account.objects.get(username = user2)

        contact = AccountManager(interlocutor).get_contact(account)
        conversation = contact.conversation_fk

        if conversation is not None:
            response['fetched_messages'] = list()
            messages = ConversationManager(conversation).get_messages_from_conversation(
                number_of_messages = int(msg_number), 
                time_from = msg_from,
                time_to = msg_to)
            
            for message in messages:
                msg = {}
                msg['content'] = message.content_of_msg
                if int(message.sender_fk.account_pk) is account.account_pk:
                    msg['sender'] = 'you'
                else:
                    msg['sender'] = 'zin'
                msg['send_time'] = message.send_time

                response['fetched_messages'].append(msg)
                
            response['success'] = True
            AccountManager(account).get_contact(interlocutor).status = False

    except Exception as e:
        print(e)
        response["error"] = str(e)

    return JsonResponse(response)




