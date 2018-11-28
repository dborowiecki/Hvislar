from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
from .models import Account
import json

@csrf_exempt
def register(request):
    response = {
            'success': False
        }
    try:
        data = request.POST
        
        account = Account(
            username = request.POST.get("name"),
            passwd   = request.POST.get("password"),
            email    = request.POST.get("email"))

        account.save()
        if Account.objects.get(username= request.POST.get("name")) is not None:
            response['username'] = account.username
            response['email'] = account.email
            response['success'] = True

        return JsonResponse(response)

    except Exception as e:
        #response['error'] = e
        return JsonResponse(response)

@csrf_exempt
def login(request):
    response = {
            'success': 'xD'
        }
    try:
        data = request.POST
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

        return JsonResponse(response)

    except Exception as e:
        print(e)
        return JsonResponse(response)