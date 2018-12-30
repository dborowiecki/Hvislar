from django.db import close_old_connections
from .models import Account
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

            # try:
            #     name, password = headers[b'authorization'].decode().split()
            #     if token_name == 'Token':
            #         token = Token.objects.get(key=token_key)
            #         scope['user'] = token.user
            # except Token.DoesNotExist:
            #     scope['user'] = AnonymousUser()
        else:
            print("no header for you")
            scope['account'] = None
        return self.inner(scope)

        # account = Account.objects.get(passwd = scope['passwd'], email = scope['email'])
        # # Look up user from query string (you should also do things like
        # # check it's a valid user ID, or if scope["user"] is already populated)
        # user = User.objects.get(id=int(scope["query_string"]))
        # close_old_connections()
        # # Return the inner application directly and let it run everything else
        # return self.inner(dict(scope, user=user))
