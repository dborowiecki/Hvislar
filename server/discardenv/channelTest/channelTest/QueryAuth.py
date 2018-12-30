from django.db import close_old_connections

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
        if b'login' in headers:
            try:
                name, password = headers[b'authorization'].decode().split()
                if token_name == 'Token':
                    token = Token.objects.get(key=token_key)
                    scope['user'] = token.user
            except Token.DoesNotExist:
                scope['user'] = AnonymousUser()
        return self.inner(scope)

        # account = Account.objects.get(passwd = scope['passwd'], email = scope['email'])
        # # Look up user from query string (you should also do things like
        # # check it's a valid user ID, or if scope["user"] is already populated)
        # user = User.objects.get(id=int(scope["query_string"]))
        # close_old_connections()
        # # Return the inner application directly and let it run everything else
        # return self.inner(dict(scope, user=user))
