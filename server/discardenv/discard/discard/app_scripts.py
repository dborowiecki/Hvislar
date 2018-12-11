from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
from .models import Account, ContactRequest, Conversation, Contact, ContactList, Message
import json

@csrf_exempt
def register(request):
    response = {
            'success': False
        }
    try:
        data = request.POST
        
        account = Account(
            username = request.POST.get("username"),
            passwd   = request.POST.get("password"),
            email    = request.POST.get("email"))

        account.save()
        if Account.objects.get(username= request.POST.get("username")) is not None:
            response['username'] = account.username
            response['email'] = account.email
            response['success'] = True

    except Exception as e:
        print(e)
        response['error'] = str(e)
    finally:
        return JsonResponse(response)

@csrf_exempt
def login(request):
    response = {
            'success': False
        }
    try:
        data     = request.POST
        passwd   = request.POST.get("password")
        email    = request.POST.get("email")
        print(email)
        print(passwd)
        account = Account.objects.get(passwd = passwd, email = email)

        if account is not None:
            account.last_time_logged = datetime.now()
            response['name'] = account.username
            response['email'] = account.email
            response['success'] = True

    except Exception as e:
        print(e)
        response['error'] = str(e)
    finally:
        return JsonResponse(response)


@csrf_exempt
def request_contact(request):
    response = {
            'success': False
        }
    try:
        passwd          = request.POST.get("password")
        email           = request.POST.get("email")
        contact_name    = request.POST.get("contact_name")
        user_message    = request.POST.get("message")
        user_account    = Account.objects.get(passwd = passwd, email = email)
        contact_account = Account.objects.get(username = contact_name)

        contact_request = ContactRequest(
            account_fk            = contact_account,
            requesting_account_fk = user_account,
            request_message          = user_message)
        #TODO: IF THERE IS RESPONSE FROM 2 USER SHOULD PAIR THEM
        contact_request.save()

        response['success'] = True

    except Exception as e:
        print(e)
        response['error'] = str(e)

    return JsonResponse(response)

@csrf_exempt
def request_contact_response(request):
    response = {
            'success': False
    }
    try:
        passwd               = request.POST.get("password")
        email                = request.POST.get("email")
        responded_user       = request.POST.get("responsed_user")
        response             = request.POST.get("response")
        user_account         = Account.objects.get(passwd = passwd, email = email)
        responded_user       = Account.objects.get(username = responded_user)
        responded_request    = ContactRequest.objects.get(
            account_fk            = user_account.account_pk,
            requesting_account_fk = responded_user.account_pk)

        if response == 'Accept' and responded_request is not None:
            new_conversation = Conversation()
            new_conversation.save()
            added1 = user_account.add_friend(responded_user, new_conversation)
            added2 = responded_user.add_friend(user_account, new_conversation)
            if added1 and added2:
                responded_request.delete()
                response['success'] = True

    except Exception as e:
        print(e)
        response['error'] = str(e)
    finally:
        return JsonResponse(response)


@csrf_exempt
def test_message(request):
    response = {
            'success': False
        }
    try:
        data     = request.POST
        user     = request.POST.get("username")
        password = request.POST.get("password")
        message  = request.POST.get("message")
        account  = Account.objects.get(passwd = password, username = user)

        if account is not None:
            print("User %s sended message: %s" % (name, message))
            response['success'] = True

        return JsonResponse(response)

    except Exception as e:
        print(e)
        response['error'] = str(e)
    
    return JsonResponse(response)

@csrf_exempt
def send_message(request):
    response = {
            'success': False
        }
    try:
        user          = request.POST.get("email")
        password      = request.POST.get("password")
        reciver_name  = request.POST.get("reciver_name")
        message       = request.POST.get("message")
        account       = Account.objects.get(passwd = password, email = user)
        reciver       = Account.objects.get(username = reciver_name)

        conversation = reciver.confirm_contact(account)

        if conversation is not None:
            response['found'] = True
            m = Message(conversation_fk = conversation, content_of_msg = message, sender_fk = account)
            m.save()
          #  conversation.add_message_to_conversation(m)
        else:
            response['found'] = False



        return JsonResponse(response)

    except Exception as e:
        print(e)
        response['error'] = str(e)
    
    return JsonResponse(response)

@csrf_exempt
def get_messages_from_conversation(request):
    response = {
        'success': False
    }
    try:
        user          = request.POST.get("email")
        password      = request.POST.get("password")
        user2         = request.POST.get("interlocutor")
        msg_number    = request.POST.get("number_of_messages")
        msg_from      = None
        msg_from      = request.POST.get("messages_from_time")
        msg_to        = None
        msg_to        = request.POST.get("messages_to_time")
        account       = Account.objects.get(passwd = password, email = user)
        interlocutor  = Account.objects.get(username = user2)

        conversation  = interlocutor.confirm_contact(account)

        if conversation is not None:
            #TODO transform from query set to other
            response['fetched_messages'] = conversation.get_messages_from_conversation(
                number_of_messages = int(msg_number), 
                time_from = msg_from,
                time_to = msg_to)
            response['success'] = True

    except Exception as e:
        print(e)
        response["error"] = str(e)

    return JsonResponse(response)



