from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
import json

from django.http import HttpResponse

def register_page(request):
    recived_data = request.POST

    if form.is_valid():
        print(form.cleaned_data)
        username = form.cleaned_data.get("email")
        email = form.cleaned_data.get("email")
        new_user = User.objects.create_user(username, email, password)
        print(new_user)

    return render(request, "auth/register.html", context)