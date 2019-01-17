from django.http import HttpResponse
from django.shortcuts import render, redirect
from django.contrib.auth import authenticate, login, get_user_model
from django.http import JsonResponse
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
from ..modelsT.Account import Account
import json

"""
This method contains method responsible for handling requests connected with user contacs and 
with user profile (in current application state: adding and reciviing description)

Methods
-------
request_contact : HttpRespose
    Sending contact request to another user.

    Response should be POST type and contain:
        password, email, contact_name, message

request_contact_response : HttpResponse
    Answering another user contact request.

    Response should be POST type and contain:
        password, email, resposed_user, response

add_user_description : HttpResponse
    Adding user description

    Response should be POST type and contain:
        password, email, description

get_user_description : HttpResponse 
    Reciving user description

    Response should be POST type and contain:
        password, email

"""

@csrf_exempt
def request_contact(request):
    """
        Method which after call creates new ContactRequest from one user to another. 

        Parameters
        --------
        request : HttpResponse
            HttpResponse of POST type, need to have key-values:
            'password' and 'email' of requesting user, needed for validation
            'contact_name' - username of account reciving contact request
            'message' -  message which comes with contct request  

        Returns
        --------
        JsonRespose
            With 'success' boolean if request was sended correctly
            If any error occured durning transaction returns also
            'error' with error message.

    """
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
    """
        Calling this method by user accepts or refuse contct with another account

        Parameters
        --------
        request : HttpResponse
            HttpResponse of POST type, need to have key-values:
            'password' and 'email' of user who answers request
            'responsed_user' - username of user which request is answered
            'response' - if equals "Accept" new contact is created, else 
                new contact between users isn't created 
        Returns
        --------
        JsonRespose
            With 'success' boolean if request responsed correctly.
            If any error occured durning transaction returns also
            'error' with error message.

    """
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
def add_user_description(request):
    """
    Method that create add description for account

    Parameters
    --------
    request : HttpResponse
        HttpResponse of POST type, need to have key-values:
        'password' and 'email' of user who answers request
        'description' - description that user want to add to his account

    Returns
    --------
    JsonRespose
        With 'success' boolean if addition was successful.
        If any error occured durning transaction returns also
        'error' with error message.
    """

    response = {
        'success': False
    }
    try:
        passwd               = request.POST.get("password")
        email                = request.POST.get("email")
        description          = request.POST.get("description")
        user                 = Account.objects.get(passwd = passwd, email = email)

        if user is not None:
            if user.add_description(description):
                response['success'] = True
    except Exception as e:
        print(e)
        response['error'] = str(e)
    finally:
        return JsonResponse(response) 

@csrf_exempt
def get_user_description(request):
    """
        Method that recives user description

        Parameters
        --------
        request : HttpResponse
            HttpResponse of POST type, need to have key-values:
            'username' - username whose description is requested

        Returns
        --------
        JsonRespose
            'success' boolean if addition was successful.
            'description' string with description.
            If any error occured durning transaction returns also
            'error' with error message.

    """
    response ={
        'success': False
    }
    try:
        user          = request.POST.get("username")

        account       = Account.objects.get(username = password)

        if account is not None:
            response['description'] = account.get_description()
            response['success'] = True
    except Exception as e:
        print(e)
        response["error"] = str(e)

    return JsonResponse(response)

