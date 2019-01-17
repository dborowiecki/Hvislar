from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
from ..modelsT.Account import Account
from ..modelsT.MassConversation import MassConversation
import json

@csrf_exempt
def add_user_to_mass_conversation(request):

    response = {
        'success': False
    }
    try:
        passwd               = request.POST.get("password")
        email                = request.POST.get("email")
        user                 = Account.objects.get(passwd = passwd, email = email)

        if user is not None:
            m = MassConversation()
            m.add_account_to_last_open_conversation(user)
            response['success'] = True
            #Check if getting aviable conversations properly
            response['room_name'] = m.get_user_conversations(user)[0].room_name

    except Exception as e:
        print(e)
        response['error'] = str(e)
    finally:
        return JsonResponse(response) 