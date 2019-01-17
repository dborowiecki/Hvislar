from django.db import close_old_connections
from .models import Account, MassConversation
class QueryAuthMiddleware:
    """
    Custom middleware (insecure) that takes user IDs from the query string.
    """

    def __init__(self, inner):
        # Store the ASGI application we were passed
        self.inner = inner
#TODO: SPRAWDZAĆ UŻYTKOWNIKA W BAZIE
    def __call__(self, scope):
        headers = dict(scope['headers'])
        if b'auth' in headers:
            print("LOGIN HEADER FOUND, FINALLY!")
            print(b'auth' in headers)


            try:
                login, password = headers[b'auth'].decode().split(',')
                print(login + ' ' + password)
                account = Account.objects.using('psql_db').get(passwd = password, username = login)
                scope['conversation_auth'] = self.find_conversation(scope, account)
                #TODO: Should check if user is in this particular mass conversation
            except Exception as e:
                print(e)
            finally:
                if account is not None:
                    scope['account'] = account
                else:
                    #TODO Routing should return auth failure in final version
                    scope['account'] = None
                return self.inner(scope)
                
        else:
            print("no header for you")
            scope['account'] = None
        return self.inner(scope)


    def find_conversation(self, scope, account):
        try:
            auth_user = False
            room = 'lobby'#scope['url_route']['kwargs']['room_name']
            user = account
            conversation = MassConversation.objects.using('psql_db').get(room_name = room)
            auth_user = conversation.auth_user(user)
            if auth_user:
                print("User is authenticated")
            else:
                print("User is not part of conversation")
        except Exception as e:
            print(e)
        finally:
            return auth_user