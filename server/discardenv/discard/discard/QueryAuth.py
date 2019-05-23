from django.db import close_old_connections
import discard.modelsT as model
class QueryAuthMiddleware:
    """
    Middleware authentication search for user in conversation
    """

    def __init__(self, inner):
        # Store the ASGI application we were passed
        self.inner = inner

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
            except Exception as e:
                print(e)
            finally:
                if account is not None:
                    scope['account'] = account
                else:
                    scope['account'] = None
                return self.inner(scope)
                
        else:
            print("no header for you")
            scope['account'] = None
        return self.inner(scope)


    def find_conversation(self, scope, account):
        auth_user = False
        try:
            user = account
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
