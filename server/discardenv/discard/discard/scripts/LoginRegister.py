from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
from ..modelsT import Account
import json
import hashlib


def hash_password(password):
    return hashlib.sha256(password.encode("utf-8")).hexdigest()


@csrf_exempt
def register(request):
    response = {
            'success': False
        }
    try:
        data = request.POST

        account = Account(
            username = request.POST.get("username"),
            passwd   = hash_password(request.POST.get("password")),
            email    = request.POST.get("email"))

        print("X"+request.POST.get("email"))
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
def logon(request):
    response = {
            'success': False
        }
    try:
        data     = request.POST
        passwd   = hash_password(request.POST.get("password"))
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