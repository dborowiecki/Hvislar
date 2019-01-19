from django.db import close_old_connections
import discard.modelsT as model
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
                account = model.Account.objects.get(passwd = password, username = login)
                room = self.get_room_name(scope)
                conversation = model.MassConversation.objects.get(room_name = room)
                print("HALOOOOO")
                scope['conversation'] = conversation
                print("CONVERSATION: "+str(scope['conversation']))
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
        auth_user = False
        try:
          #  room = 'lobby'#scope['url_route']['kwargs']['room_name']
            user = account
           # conversation = model.MassConversation.objects.get(room_name = room)
            auth_user = scope['conversation'].auth_user(user)
            if auth_user:
                print("User is authenticated")
            else:
                print("User is not part of conversation")
        except Exception as e:
            print(e)
        finally:
            return auth_user

    def get_room_name(self, scope):
        p = scope['path'].split('/')
        room = p[3]
        return room